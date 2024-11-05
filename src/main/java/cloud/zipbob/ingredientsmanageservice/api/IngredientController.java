package cloud.zipbob.ingredientsmanageservice.api;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientException;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception.IngredientExceptionType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.request.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.response.*;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.IngredientService;
import cloud.zipbob.ingredientsmanageservice.global.Responder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping("")
    public ResponseEntity<IngredientAddResponse> addIngredient(final @RequestBody IngredientAddRequest request) {
        IngredientAddResponse response = ingredientService.addIngredient(request);
        return Responder.success(response);
    }

    @DeleteMapping("")
    public ResponseEntity<IngredientDeleteResponse> deleteIngredient(final @RequestBody IngredientRequest request) {
        IngredientDeleteResponse response = ingredientService.deleteIngredient(request);
        return Responder.success(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<UpdateQuantityResponse> updateQuantity(final @RequestBody UpdateQuantityRequest request) {
        UpdateQuantityResponse response = ingredientService.updateQuantity(request);
        return Responder.success(response);
    }

    @GetMapping("")
    public ResponseEntity<GetIngredientsResponse> getIngredients(final @RequestBody GetIngredientsRequest request) {
        GetIngredientsResponse response = ingredientService.getIngredients(request);
        return Responder.success(response);
    }

    @GetMapping("/type")
    public ResponseEntity<GetIngredientsByTypeResponse> getIngredientsByType(final @RequestBody GetIngredientsByTypeRequest request) {
        try {
            GetIngredientsByTypeResponse response = ingredientService.getIngredientsByType(request);
            return Responder.success(response);
        } catch (IllegalArgumentException e) {
            throw new IngredientException(IngredientExceptionType.INGREDIENT_TYPE_ERROR);
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<ExpiredIngredientResponse> getExpiredIngredients(final @RequestBody ExpiredIngredientRequest request) {
        ExpiredIngredientResponse response = ingredientService.getExpiredIngredients(request);
        return Responder.success(response);
    }
}
