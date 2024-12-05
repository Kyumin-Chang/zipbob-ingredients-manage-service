package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckAndSendMessageResponse {
    private Long memberId;
    private Long refrigeratorId;
    private List<IngredientType> ingredients;
    private List<UnitType> unitTypes;
    private List<Integer> quantities;
    private final String message = "큐에 메시지가 정상적으로 등록되었습니다.";

    public static CheckAndSendMessageResponse of(Long memberId, Long refrigeratorId, List<IngredientType> ingredients,
                                                 List<UnitType> unitTypes, List<Integer> quantities) {
        return new CheckAndSendMessageResponse(memberId, refrigeratorId, ingredients, unitTypes, quantities);
    }
}
