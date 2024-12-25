package cloud.zipbob.ingredientsmanageservice.domain.ingredient;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public enum IngredientType {
    // 야채/과일
    APPLE(Category.FRUIT_VEGETABLE, "사과"),
    BANANA(Category.FRUIT_VEGETABLE, "바나나"),
    ORANGE(Category.FRUIT_VEGETABLE, "오렌지"),
    GRAPE(Category.FRUIT_VEGETABLE, "포도"),
    CUCUMBER(Category.FRUIT_VEGETABLE, "오이"),
    TOMATO(Category.FRUIT_VEGETABLE, "토마토"),
    CARROT(Category.FRUIT_VEGETABLE, "당근"),
    POTATO(Category.FRUIT_VEGETABLE, "감자"),
    BROCCOLI(Category.FRUIT_VEGETABLE, "브로콜리"),
    LETTUCE(Category.FRUIT_VEGETABLE, "상추"),
    SPINACH(Category.FRUIT_VEGETABLE, "시금치"),
    ZUCCHINI(Category.FRUIT_VEGETABLE, "애호박"),
    EGGPLANT(Category.FRUIT_VEGETABLE, "가지"),
    PINEAPPLE(Category.FRUIT_VEGETABLE, "파인애플"),
    STRAWBERRY(Category.FRUIT_VEGETABLE, "딸기"),
    PEACH(Category.FRUIT_VEGETABLE, "복숭아"),
    MANGO(Category.FRUIT_VEGETABLE, "망고"),
    LEMON(Category.FRUIT_VEGETABLE, "레몬"),
    KIWI(Category.FRUIT_VEGETABLE, "키위"),
    BLUEBERRY(Category.FRUIT_VEGETABLE, "블루베리"),

    // 육류
    CHICKEN(Category.MEAT, "닭고기"),
    BEEF(Category.MEAT, "소고기"),
    PORK(Category.MEAT, "돼지고기"),
    LAMB(Category.MEAT, "양고기"),
    DUCK(Category.MEAT, "오리고기"),
    TURKEY(Category.MEAT, "칠면조"),
    BACON(Category.MEAT, "베이컨"),
    SAUSAGE(Category.MEAT, "소시지"),
    HAM(Category.MEAT, "햄"),
    SALMON(Category.MEAT, "연어"),
    TUNA(Category.MEAT, "참치"),
    SHRIMP(Category.MEAT, "새우"),
    CRAB(Category.MEAT, "게"),

    // 가공식품
    TOFU(Category.PROCESSED_FOOD, "두부"),
    NOODLES(Category.PROCESSED_FOOD, "면"),
    CANNED_TUNA(Category.PROCESSED_FOOD, "참치캔"),
    BREAD(Category.PROCESSED_FOOD, "빵"),
    RICE_CAKE(Category.PROCESSED_FOOD, "떡"),
    SPAM(Category.PROCESSED_FOOD, "스팸"),
    FISH_CAKE(Category.PROCESSED_FOOD, "어묵"),
    CURRY_POWDER(Category.PROCESSED_FOOD, "카레가루"),
    PASTA(Category.PROCESSED_FOOD, "파스타"),
    PIZZA_BASE(Category.PROCESSED_FOOD, "피자 도우"),

    // 유제품
    MILK(Category.DAIRY, "우유"),
    CHEESE(Category.DAIRY, "치즈"),
    BUTTER(Category.DAIRY, "버터"),
    YOGURT(Category.DAIRY, "요거트"),
    WHIPPED_CREAM(Category.DAIRY, "휘핑크림"),
    ICE_CREAM(Category.DAIRY, "아이스크림"),
    CREAM_CHEESE(Category.DAIRY, "크림치즈"),
    EVAPORATED_MILK(Category.DAIRY, "연유"),

    // 조미료/소스
    SALT(Category.SEASONING_SAUCE, "소금"),
    SUGAR(Category.SEASONING_SAUCE, "설탕"),
    SOY_SAUCE(Category.SEASONING_SAUCE, "간장"),
    VINEGAR(Category.SEASONING_SAUCE, "식초"),
    SESAME_OIL(Category.SEASONING_SAUCE, "참기름"),
    PEPPER(Category.SEASONING_SAUCE, "후추"),
    RED_PEPPER_FLAKES(Category.SEASONING_SAUCE, "고추가루"),
    GARLIC(Category.SEASONING_SAUCE, "마늘"),
    GINGER(Category.SEASONING_SAUCE, "생강"),
    KETCHUP(Category.SEASONING_SAUCE, "케찹"),
    MAYONNAISE(Category.SEASONING_SAUCE, "마요네즈"),
    MUSTARD(Category.SEASONING_SAUCE, "머스타드"),
    CHILI_SAUCE(Category.SEASONING_SAUCE, "칠리소스"),
    BARBECUE_SAUCE(Category.SEASONING_SAUCE, "바베큐소스"),
    OYSTER_SAUCE(Category.SEASONING_SAUCE, "굴소스"),
    FISH_SAUCE(Category.SEASONING_SAUCE, "액젓"),
    CURRY_PASTE(Category.SEASONING_SAUCE, "카레 페이스트");

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
        FRUIT_VEGETABLE,
        MEAT,
        PROCESSED_FOOD,
        DAIRY,
        SEASONING_SAUCE
    }
}
