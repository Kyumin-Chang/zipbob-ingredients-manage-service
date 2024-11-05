package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class IngredientAddResponse {
    private IngredientType type;
    private LocalDate addedDate;
    private LocalDate expiredDate;
    private Refrigerator refrigerator;

    public static IngredientAddResponse of(Ingredient ingredient) {
        return new IngredientAddResponse(ingredient.getType(), ingredient.getAddedDate(), ingredient.getExpiredDate(), ingredient.getRefrigerator());
    }
}
