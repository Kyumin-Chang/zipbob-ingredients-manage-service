package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import java.util.List;

public record RecipeSelectRequest(Long memberId, List<String> ingredientsAndQuantities) {
}
