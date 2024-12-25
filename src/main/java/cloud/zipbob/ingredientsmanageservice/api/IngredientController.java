package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientException;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.IngredientService;
import cloud.zipbob.ingredientsmanageservice.global.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam IngredientType.Category category) {
        try {
            GetIngredientsByTypeResponse response = ingredientService.getIngredientsByType(category);
            return Responder.success(response);
        } catch (IllegalArgumentException e) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_TYPE_ERROR);
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<ExpiredIngredientResponse> getExpiredIngredients(
            @RequestParam Long memberId,
            @RequestHeader("X-Member-Id") Long authenticatedMemberId) {
        ExpiredIngredientResponse response = ingredientService.getExpiredIngredients(memberId, authenticatedMemberId);
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
