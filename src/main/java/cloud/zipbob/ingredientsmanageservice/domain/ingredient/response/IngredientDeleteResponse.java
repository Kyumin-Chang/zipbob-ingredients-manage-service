package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientDeleteResponse {
    private Long refrigeratorId;
    private IngredientType ingredientType;

    public static IngredientDeleteResponse of(Ingredient ingredient) {
        return new IngredientDeleteResponse(ingredient.getRefrigerator().getId(), ingredient.getType());
    }
}
