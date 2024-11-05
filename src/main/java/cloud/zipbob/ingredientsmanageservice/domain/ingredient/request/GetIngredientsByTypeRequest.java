package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;

public record GetIngredientsByTypeRequest(IngredientType.Category category) {
}
