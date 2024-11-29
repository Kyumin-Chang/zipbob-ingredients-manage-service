package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Override
    public IngredientAddResponse addIngredient(IngredientAddRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        if (isNotValidIngredientType(request.ingredientType())) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_NAME_ERROR);
        }
        if (ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), request.ingredientType()).isPresent()) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_ALREADY_EXIST);
        }
        Ingredient ingredient = request.toEntity(refrigerator);
        ingredientRepository.save(ingredient);
        return IngredientAddResponse.of(ingredient);
    }

    @Override
    public IngredientDeleteResponse deleteIngredient(IngredientRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        if (isNotValidIngredientType(request.ingredientType())) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_NAME_ERROR);
        }
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), request.ingredientType()).orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredientRepository.delete(ingredient);
        return IngredientDeleteResponse.of(ingredient);
    }

    @Override
    public UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), request.ingredientType()).orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredient.updateQuantity(request.quantity());
        return UpdateQuantityResponse.of(request.memberId(), ingredient);
    }

    @Override
    public ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        List<Ingredient> expiredIngredients = ingredientRepository.findByRefrigeratorId(refrigerator.getId()).stream().filter(ingredient -> ingredient.getExpiredDate().isBefore(LocalDate.now())).toList();
        return ExpiredIngredientResponse.of(refrigerator.getId(), expiredIngredients);
    }

    @Override
    public GetIngredientsByTypeResponse getIngredientsByType(GetIngredientsByTypeRequest request) {
        List<IngredientType> ingredients = IngredientType.getIngredientsByCategory(request.category());
        return GetIngredientsByTypeResponse.of(ingredients);
    }

    private boolean isNotValidIngredientType(IngredientType ingredientType) {
        return !Arrays.asList(IngredientType.values()).contains(ingredientType);
    }

    // 냉장고 재료 여부 확인 및 Message Queue 로 메시지 전공
    @Override
    public CheckAndSendMessageResponse checkAndSendMessage(CheckAndSendMessageRequest request, Long authenticatedMemberId) {
        validationMember(request.memberId(), authenticatedMemberId);
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId())
                .orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        List<IngredientType> sendIngredients = new ArrayList<>();

        for (IngredientType ingredientType : request.ingredients()) {
            boolean exists = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), ingredientType).isPresent();
            if (exists) {
                sendIngredients.add(ingredientType);
            } else {
                throw new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND);
            }
        }
        rabbitMQProducer.sendMessage(request.memberId(), sendIngredients);
        return CheckAndSendMessageResponse.of(request.memberId(), refrigerator.getId(), sendIngredients);
    }

    private void validationMember(Long memberId, Long authenticatedMemberId) {
        if (!Objects.equals(memberId, authenticatedMemberId)) {
            throw new CustomAuthenticationException(CustomAuthenticationExceptionType.AUTHENTICATION_DENIED);
        }
    }
}
