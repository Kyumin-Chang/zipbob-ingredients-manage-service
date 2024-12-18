package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeSelectResponse {
    private Long memberId;
    private Long refrigeratorId;
    private final String message = "재료 업데이트가 완료되었습니다.";

    public static RecipeSelectResponse of(Long memberId, Long refrigeratorId) {
        return new RecipeSelectResponse(memberId, refrigeratorId);
    }
}
