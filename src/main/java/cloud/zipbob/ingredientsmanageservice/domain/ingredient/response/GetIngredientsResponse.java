package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetIngredientsResponse {
    private Long memberId;
    private Long refrigeratorId;
    private List<Ingredient> ingredients;

    public static GetIngredientsResponse of(Long memberId, Long refrigeratorId, List<Ingredient> ingredients) {
        return new GetIngredientsResponse(memberId, refrigeratorId, ingredients);
    }
}
