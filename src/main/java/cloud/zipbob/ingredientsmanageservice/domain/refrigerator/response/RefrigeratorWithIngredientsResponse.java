package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RefrigeratorWithIngredientsResponse {
    private Long refrigeratorId;
    private Long memberId;
    private List<Ingredient> ingredients;

    public static RefrigeratorWithIngredientsResponse of(Refrigerator refrigerator) {
        return new RefrigeratorWithIngredientsResponse(refrigerator.getId(), refrigerator.getMemberId(), refrigerator.getIngredients());
    }
}
