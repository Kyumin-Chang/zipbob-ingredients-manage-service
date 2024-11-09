package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefrigeratorResponse {
    private Long refrigeratorId;
    private Long memberId;

    public static RefrigeratorResponse of(Refrigerator refrigerator) {
        return new RefrigeratorResponse(refrigerator.getId(), refrigerator.getMemberId());
    }
}
