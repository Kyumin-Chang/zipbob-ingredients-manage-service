package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientException;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.CheckAndSendMessageRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.ExpiredIngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.GetIngredientsByTypeRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientAddRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.IngredientRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.RecipeSelectRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.UpdateQuantityRequest;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.CheckAndSendMessageResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.ExpiredIngredientResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.GetIngredientsByTypeResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientAddResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.IngredientDeleteResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.RecipeSelectResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.UpdateQuantityResponse;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.IngredientService;
import cloud.zipbob.ingredientsmanageservice.global.Responder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping("")
    public ResponseEntity<List<IngredientAddResponse>> addIngredient(final @RequestBody IngredientAddRequest request,
                                                                     @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        List<IngredientAddResponse> response = ingredientService.addIngredient(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @DeleteMapping("")
    public ResponseEntity<IngredientDeleteResponse> deleteIngredient(final @RequestBody IngredientRequest request,
                                                                     @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        IngredientDeleteResponse response = ingredientService.deleteIngredient(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateQuantityResponse> updateQuantity(final @RequestBody UpdateQuantityRequest request,
                                                                 @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        UpdateQuantityResponse response = ingredientService.updateQuantity(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @GetMapping("/type")
    public ResponseEntity<GetIngredientsByTypeResponse> getIngredientsByType(
            final @RequestBody GetIngredientsByTypeRequest request) {
        try {
            GetIngredientsByTypeResponse response = ingredientService.getIngredientsByType(request);
            return Responder.success(response);
        } catch (IllegalArgumentException e) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_TYPE_ERROR);
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<ExpiredIngredientResponse> getExpiredIngredients(
            final @RequestBody ExpiredIngredientRequest request,
            @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        ExpiredIngredientResponse response = ingredientService.getExpiredIngredients(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @PostMapping("/recipeRecommend")
    public ResponseEntity<CheckAndSendMessageResponse> checkAndSendMessage(
            final @RequestBody CheckAndSendMessageRequest request,
            @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        CheckAndSendMessageResponse response = ingredientService.checkAndSendMessage(request, authenticatedMemberId);
        return Responder.success(response);
    }

    @PatchMapping("/recipeSelect")
    public ResponseEntity<RecipeSelectResponse> selectRecipe(final @RequestBody RecipeSelectRequest request,
                                                             @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        RecipeSelectResponse response = ingredientService.selectRecipeAndDeleteQuantity(request, authenticatedMemberId);
        return Responder.success(response);
    }
}
