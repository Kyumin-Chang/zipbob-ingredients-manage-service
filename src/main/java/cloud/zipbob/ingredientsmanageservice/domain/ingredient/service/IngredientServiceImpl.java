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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RefrigeratorRepository refrigeratorRepository;

    @Override
    public IngredientAddResponse addIngredient(IngredientAddRequest request) {
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
    public IngredientDeleteResponse deleteIngredient(IngredientRequest request) {
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        if (isNotValidIngredientType(request.ingredientType())) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_NAME_ERROR);
        }
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), request.ingredientType()).orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredientRepository.delete(ingredient);
        return IngredientDeleteResponse.of(ingredient);
    }

    @Override
    public UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request) {
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(request.memberId()).orElseThrow(() -> new RefrigeratorException(RefrigeratorExceptionType.REFRIGERATOR_NOT_FOUND));
        Ingredient ingredient = ingredientRepository.findByRefrigeratorIdAndType(refrigerator.getId(), request.ingredientType()).orElseThrow(() -> new IngredientException(IngredientExceptionType.INGREDIENT_NOT_FOUND));
        ingredient.updateQuantity(request.quantity());
        return UpdateQuantityResponse.of(request.memberId(), ingredient);
    }

    @Override
    public ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request) {
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
}
