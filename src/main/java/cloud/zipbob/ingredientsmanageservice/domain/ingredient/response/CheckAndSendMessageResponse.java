package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckAndSendMessageResponse {
    private Long memberId;
    private Long refrigeratorId;
    private List<String> ingredients;
    private List<String> quantities;
    private final String message = "큐에 메시지가 정상적으로 등록되었습니다.";

    public static CheckAndSendMessageResponse of(Long memberId, Long refrigeratorId, List<String> ingredients,
                                                 List<String> quantities) {
        return new CheckAndSendMessageResponse(memberId, refrigeratorId, ingredients, quantities);
    }
}
