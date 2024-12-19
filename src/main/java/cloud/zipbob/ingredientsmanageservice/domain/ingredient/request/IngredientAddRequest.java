package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record IngredientAddRequest(
        Long memberId,
        List<IngredientType> ingredientTypes,
        @Positive(message = "양의 정수만 입력이 가능합니다.") List<Integer> quantities,
        List<UnitType> unitTypes,
        List<LocalDate> expiredDates) {

    public List<Ingredient> toEntities(Refrigerator refrigerator) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientTypes.size(); i++) {
            ingredients.add(Ingredient.builder()
                    .refrigerator(refrigerator)
                    .type(ingredientTypes.get(i))
                    .quantity(quantities.get(i))
                    .unitType(unitTypes.get(i))
                    .expiredDate(expiredDates.get(i))
                    .build());
        }
        return ingredients;
    }
}


