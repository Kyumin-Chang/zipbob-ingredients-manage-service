package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RefrigeratorResponse {
    private Long refrigeratorId;
    private Long memberId;
    private List<Ingredient> ingredients;

    public static RefrigeratorResponse of(Refrigerator refrigerator) {
        return new RefrigeratorResponse(refrigerator.getId(), refrigerator.getMemberId(), refrigerator.getIngredients());
    }
    // TODO 응답 dto 재료 유무로 분리하기 (성능 최적화 인지?)
}
