package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.CheckAndSendMessageRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.ExpiredIngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.GetIngredientsByTypeRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientAddRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.RecipeSelectRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.UpdateQuantityRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.CheckAndSendMessageResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.ExpiredIngredientResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.GetIngredientsByTypeResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientAddResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientDeleteResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.RecipeSelectResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.UpdateQuantityResponse;

public interface IngredientService {

    IngredientAddResponse addIngredient(IngredientAddRequest request, Long authenticatedMemberId);

    IngredientDeleteResponse deleteIngredient(IngredientRequest request, Long authenticatedMemberId);

    UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request, Long authenticatedMemberId);

    ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request, Long authenticatedMemberId);

    GetIngredientsByTypeResponse getIngredientsByType(GetIngredientsByTypeRequest request);

    CheckAndSendMessageResponse checkAndSendMessage(CheckAndSendMessageRequest request, Long authenticatedMemberId);

    RecipeSelectResponse selectRecipeAndDeleteQuantity(RecipeSelectRequest request, Long authenticatedMemberId);
}
