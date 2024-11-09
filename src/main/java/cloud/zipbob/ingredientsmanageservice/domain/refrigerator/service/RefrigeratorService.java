package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorCreateRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.request.RefrigeratorRequest;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorResponse;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.response.RefrigeratorWithIngredientsResponse;

public interface RefrigeratorService {
    RefrigeratorResponse createRefrigerator(RefrigeratorCreateRequest request);

    RefrigeratorResponse deleteRefrigerator(RefrigeratorRequest request);

    RefrigeratorWithIngredientsResponse getRefrigerator(RefrigeratorRequest request);
}
