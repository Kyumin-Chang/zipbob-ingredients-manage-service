package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CheckAndSendMessageResponse {
    private Long memberId;
    private Long refrigeratorId;
    private List<IngredientType> ingredients;
    private final String message = "큐에 메시지가 정상적으로 등록되었습니다.";

    public static CheckAndSendMessageResponse of(Long memberId, Long refrigeratorId, List<IngredientType> ingredients) {
        return new CheckAndSendMessageResponse(memberId, refrigeratorId, ingredients);
    }
}
