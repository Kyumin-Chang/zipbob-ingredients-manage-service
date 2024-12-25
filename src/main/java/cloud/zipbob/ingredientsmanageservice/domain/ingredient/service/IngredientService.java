package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;

import java.util.List;

public interface IngredientService {

    List<IngredientAddResponse> addIngredient(IngredientAddRequest request, Long authenticatedMemberId);

    IngredientDeleteResponse deleteIngredient(IngredientRequest request, Long authenticatedMemberId);

    UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request, Long authenticatedMemberId);

    ExpiredIngredientResponse getExpiredIngredients(Long memberId, Long authenticatedMemberId);

    GetIngredientsByTypeResponse getIngredientsByType(IngredientType.Category category);

    CheckAndSendMessageResponse checkAndSendMessage(CheckAndSendMessageRequest request, Long authenticatedMemberId);

    RecipeSelectResponse selectRecipeAndDeleteQuantity(RecipeSelectRequest request, Long authenticatedMemberId);
}
