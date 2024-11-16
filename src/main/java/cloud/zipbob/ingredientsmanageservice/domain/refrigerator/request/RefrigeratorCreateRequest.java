package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;

public record RefrigeratorCreateRequest(Long memberId) {
    public Refrigerator toEntity() {
        return Refrigerator.builder().memberId(memberId).build();
    }
}
