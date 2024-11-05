package cloud.zipbob.ingredientsmanageservice.domain.ingredient;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum IngredientType {

    //주 재료
    EGG(Category.MAIN),
    MILK(Category.MAIN),
    BUTTER(Category.MAIN),
    CHICKEN(Category.MAIN),
    BEEF(Category.MAIN),
    PORK(Category.MAIN),
    TOFU(Category.MAIN),
    POTATO(Category.MAIN),
    CARROT(Category.MAIN),
    NOODLES(Category.MAIN),

    //조미료
    SALT(Category.SEASONING),
    PEPPER(Category.SEASONING),
    SOY_SAUCE(Category.SEASONING),
    VINEGAR(Category.SEASONING),
    SESAME_OIL(Category.SEASONING),
    RED_PEPPER_FLAKES(Category.SEASONING),
    SUGAR(Category.SEASONING),
    COOKING_WINE(Category.SEASONING);

    private final Category category;

    IngredientType(Category category) {
        this.category = category;
    }

    public static List<IngredientType> getIngredientsByCategory(Category category) {
        return Arrays.stream(values())
                .filter(type -> type.getCategory() == category)
                .collect(Collectors.toList());
    }

    public enum Category {
        MAIN,
        SEASONING
    }
}
