package cloud.zipbob.ingredientsmanageservice.domain.ingredient.service;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;

public interface IngredientService {

    IngredientAddResponse addIngredient(IngredientAddRequest request, Long authenticatedMemberId);

    IngredientDeleteResponse deleteIngredient(IngredientRequest request, Long authenticatedMemberId);

    UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request, Long authenticatedMemberId);

    ExpiredIngredientResponse getExpiredIngredients(ExpiredIngredientRequest request, Long authenticatedMemberId);

    GetIngredientsByTypeResponse getIngredientsByType(GetIngredientsByTypeRequest request);

    // 냉장고 재료 여부 확인 및 Queue에 메시지 전공
    CheckAndSendMessageResponse checkAndSendMessage(CheckAndSendMessageRequest request, Long authenticatedMemberId);
}
