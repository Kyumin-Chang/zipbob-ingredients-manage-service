package cloud.zipbob.ingredientsmanageservice.domain.ingredient.response;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class IngredientAddResponse {
    private Long refrigeratorId;
    private IngredientType type;
    private int quantity;
    private UnitType unitType;
    private LocalDate addedDate;
    private LocalDate expiredDate;

    public static IngredientAddResponse of(Ingredient ingredient) {
        return new IngredientAddResponse(ingredient.getRefrigerator().getId(), ingredient.getType(), ingredient.getQuantity(), ingredient.getUnitType(), ingredient.getAddedDate(), ingredient.getExpiredDate());
    }
}
