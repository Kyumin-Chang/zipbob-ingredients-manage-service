package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientException;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.repository.IngredientRepository;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorException;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception.RefrigeratorExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository.RefrigeratorRepository;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationException;
import cloud.zipbob.ingredientsmanageservice.global.exception.CustomAuthenticationExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    @Transactional
    public List<IngredientAddResponse> addIngredient(IngredientAddRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);

        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));

        List<Ingredient> ingredients = request.toEntities(refrigerator);

        List<IngredientAddResponse> responses = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            if (isNotValidIngredientType(ingredient.getType())) {
                throw new IngredientException(IngredientExceptionType.INGREDIENT_NAME_ERROR);
            }

            if (ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), ingredient.getType())
                    .isPresent()) {
                throw new IngredientException(IngredientExceptionType.INGREDIENT_ALREADY_EXIST);
            }

            ingredientRepository.save(ingredient);
            responses.add(IngredientAddResponse.of(ingredient));
        }

        return responses;
    }


    @Override
    @CacheEvict(value = "expiredIngredientsCache", key = "#root.args[1] != null ? #root.args[1] : 'defaultKey'")
    @Transactional
    public IngredientDeleteResponse deleteIngredient(IngredientRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        if (isNotValidIngredientType(request.ingredientType())) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_NAME_ERROR);
        }
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(),
                        request.ingredientType())
                .orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredientRepository.delete(ingredient);
        return IngredientDeleteResponse.of(ingredient);
    }

    @Override
    @Transactional
    public UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(),
                        request.ingredientType())
                .orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredient.updateQuantity(request.quantity());
        return UpdateQuantityResponse.of(request.memberId(), ingredient);
    }

    @Override
    @Cacheable(value = "expiredIngredientsCache", key = "#root.args[1] != null ? #root.args[1] : 'defaultKey'")
    @Transactional(readOnly = true)
    public ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request,
                                                           Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        List<Ingredient> expiredIngredients = ingredientRepository.findByRefrigeratorId(refrigerator.getId()).stream()
                .filter(ingredient -> ingredient.getExpiredDate().isBefore(LocalDate.now())).toList();
        return ExpiredIngredientResponse.of(refrigerator.getId(), expiredIngredients);
    }

    @Override
    @Cacheable(value = "ingredientsTypeCache", key = "#root.args[0].category != null ? #root.args[0].category : 'defaultKey'")
    @Transactional(readOnly = true)
    public GetIngredientsByTypeResponse getIngredientsByType(GetIngredientsByTypeRequest request) {
        List<IngredientType> ingredients = IngredientType.getIngredientsByCategory(request.category());
        return GetIngredientsByTypeResponse.of(ingredients);
    }

    private boolean isNotValidIngredientType(IngredientType ingredientType) {
        return !Arrays.asList(IngredientType.values()).contains(ingredientType);
    }

    @Override
    @Transactional
    public CheckAndSendMessageResponse checkAndSendMessage(CheckAndSendMessageRequest request,
                                                           Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        List<String> sendIngredients = new ArrayList<>();
        List<String> sendQuantities = new ArrayList<>();

        for (IngredientType ingredientType : request.ingredients()) {
            Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(),
                            ingredientType)
                    .orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
            sendIngredients.add(ingredient.getType().getKoreanName());
            sendQuantities.add(ingredient.getQuantity() + ingredient.getUnitType().getKoreanName());
        }
        rabbitMQProducer.sendMessage(sendIngredients, sendQuantities);
        return CheckAndSendMessageResponse.of(request.memberId(), refrigerator.getId(), sendIngredients,
                sendQuantities);
    }

    @Override
    @Transactional
    public RecipeSelectResponse selectRecipeAndDeleteQuantity(RecipeSelectRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));

        List<Map<String, Object>> parsedIngredients = parseIngredients(request.ingredientsAndQuantities());
        updateIngredientsQuantity(refrigerator.getId(), parsedIngredients);

        return RecipeSelectResponse.of(request.memberId(), refrigerator.getId());
    }

    private void validationMember(Long memberId, Long authenticatedMemberId) {
        if (!Objects.equals(memberId, authenticatedMemberId)) {
            throw new CustomAuthenticationException(CustomAuthenticationExceptionType.AUTHENTICATION_DENIED);
        }
    }

    private List<Map<String, Object>> parseIngredients(List<String> ingredientsAndQuantities) {
        Pattern pattern = Pattern.compile("^(.+)\\s(\\d+)\\s*(.*)$");
        List<Map<String, Object>> parsedIngredients = new ArrayList<>();

        for (String entry : ingredientsAndQuantities) {
            Matcher matcher = pattern.matcher(entry);
            if (matcher.matches()) {
                Map<String, Object> ingredientMap = new HashMap<>();
                ingredientMap.put("name", matcher.group(1).trim());
                ingredientMap.put("quantity", Integer.parseInt(matcher.group(2).trim()));
                ingredientMap.put("unitType", UnitType.convertToUnit(matcher.group(3).trim()));
                parsedIngredients.add(ingredientMap);
            } else {
                throw new IllegalArgumentException("Invalid format: " + entry);
            }
        }
        return parsedIngredients;
    }

    private void updateIngredientsQuantity(Long refrigeratorId, List<Map<String, Object>> parsedIngredients) {
        List<String> ingredientNames = parsedIngredients.stream()
                .map(entry -> (String) entry.get("name"))
                .toList();

        List<IngredientType> ingredientTypes = IngredientType.findByKoreanNames(ingredientNames);

        for (int i = 0; i < ingredientTypes.size(); i++) {
            IngredientType type = ingredientTypes.get(i);
            Map<String, Object> parsed = parsedIngredients.get(i);

            Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigeratorId, type)
                    .orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));

            validateAndUpdateIngredient(ingredient, (UnitType) parsed.get("unitType"), (int) parsed.get("quantity"));
        }
    }

    private void validateAndUpdateIngredient(Ingredient ingredient, UnitType unitType, int quantity) {
        if (ingredient.getUnitType() != unitType || ingredient.getQuantity() < quantity) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_DELETE_ERROR);
        }
        ingredient.updateQuantity(ingredient.getQuantity() - quantity);
    }
}
