package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;

import java.util.List;

public record CheckAndSendMessageRequest(Long memberId, List<IngredientType> ingredients) {
}
