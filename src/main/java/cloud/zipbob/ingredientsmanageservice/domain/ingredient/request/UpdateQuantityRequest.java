package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;

public record UpdateQuantityRequest(Long memberId, IngredientType ingredientType, int quantity) {
}
