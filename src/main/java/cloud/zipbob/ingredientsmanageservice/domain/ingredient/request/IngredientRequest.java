package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;

public record IngredientRequest(Long memberId, IngredientType ingredientType) {
}
