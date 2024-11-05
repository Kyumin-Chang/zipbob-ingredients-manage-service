package cloud.zipbob.ingredientsmanageservice.domain.ingredient.request;

import cloud.zipbob.ingredientsmanageservice.domain.ingredient.Ingredient;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.IngredientType;
import cloud.zipbob.ingredientsmanageservice.domain.ingredient.UnitType;
import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record IngredientAddRequest(Long memberId, IngredientType ingredientType,
                                   @Positive(message = "양의 정수만 입력이 가능합니다.") int quantity,
                                   UnitType unitType,
                                   LocalDate expiredDate) {
    public Ingredient toEntity(Refrigerator refrigerator) {
        return Ingredient.builder().refrigerator(refrigerator).type(ingredientType).quantity(quantity).unitType(unitType)
                .expiredDate(expiredDate).build();
    }
}
