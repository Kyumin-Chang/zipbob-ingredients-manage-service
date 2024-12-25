package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorWithIngredientsResponse;

public interface RefrigeratorService {
    RefrigeratorResponse createRefrigerator(RefrigeratorCreateRequest request, Long authenticatedMemberId);

    RefrigeratorResponse deleteRefrigerator(RefrigeratorRequest request, Long authenticatedMemberId);

    RefrigeratorWithIngredientsResponse getRefrigerator(Long memberId, Long authenticatedMemberId);
}
