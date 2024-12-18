package cloud.zipbob.ingredientsmanageservice.domain.ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum IngredientType {
    //TODO: 재료 업데이트

    // 주 재료
    EGG(Category.MAIN, "계란"),
    MILK(Category.MAIN, "우유"),
    BUTTER(Category.MAIN, "버터"),
    CHICKEN(Category.MAIN, "닭고기"),
    BEEF(Category.MAIN, "소고기"),
    PORK(Category.MAIN, "돼지고기"),
    TOFU(Category.MAIN, "두부"),
    POTATO(Category.MAIN, "감자"),
    CARROT(Category.MAIN, "당근"),
    NOODLES(Category.MAIN, "면"),

    // 조미료
    SALT(Category.SEASONING, "소금"),
    PEPPER(Category.SEASONING, "후추"),
    SOY_SAUCE(Category.SEASONING, "간장"),
    VINEGAR(Category.SEASONING, "식초"),
    SESAME_OIL(Category.SEASONING, "참기름"),
    RED_PEPPER_FLAKES(Category.SEASONING, "고추가루"),
    SUGAR(Category.SEASONING, "설탕"),
    COOKING_WINE(Category.SEASONING, "맛술");

    private final Category category;
    private final String koreanName;

    IngredientType(Category category, String koreanName) {
        this.category = category;
        this.koreanName = koreanName;
    }

    public static List<IngredientType> getIngredientsByCategory(Category category) {
        return Arrays.stream(values())
                .filter(type -> type.getCategory() == category)
                .collect(Collectors.toList());
    }

    public static List<IngredientType> findByKoreanNames(List<String> koreanNames) {
        Map<String, IngredientType> nameToTypeMap = Arrays.stream(values())
                .collect(Collectors.toMap(IngredientType::getKoreanName, type -> type));

        return koreanNames.stream()
                .map(nameToTypeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public enum Category {
        MAIN,
        SEASONING
    }
}
