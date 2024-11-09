package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExpiredIngredientResponse {
    private Long refrigeratorId;
    private List<Ingredient> expiredIngredients;

    public static ExpiredIngredientResponse of(Long refrigeratorId, List<Ingredient> expiredIngredients) {
        return new ExpiredIngredientResponse(refrigeratorId, expiredIngredients);
    }
}
