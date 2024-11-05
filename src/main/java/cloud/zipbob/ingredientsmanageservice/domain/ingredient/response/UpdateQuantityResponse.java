package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateQuantityResponse {
    private Long memberId;
    private IngredientType ingredientType;
    private int quantity;

    public static UpdateQuantityResponse of(Long memberId, Ingredient ingredient) {
        return new UpdateQuantityResponse(memberId, ingredient.getType(), ingredient.getQuantity());
    }
}
