package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;

public interface IngredientService {
    IngredientAddResponse addIngredient(IngredientAddRequest request);

    IngredientDeleteResponse deleteIngredient(IngredientRequest request);

    UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request);

    ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request);

    GetIngredientsByTypeResponse getIngredientsByType(GetIngredientsByTypeRequest request);
}
