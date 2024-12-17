package cloud.zipbob.ingredientsmanageservice.domain.ingredient;

import lombok.Getter;

@Getter
public enum UnitType {
    GRAM("그램"),
    LITER("리터"),
    COUNT("개"),
    MILLILITER("밀리리터"),
    KILOGRAM("킬로그램"),
    PIECE("조각"),
    TABLESPOON("큰 술"),
    TEASPOON("작은 술"),
    CUP("컵"),
    OUNCE("온스"),
    POUND("파운드");

    private final String koreanName;

    UnitType(String koreanName) {
        this.koreanName = koreanName;
    }

    public static UnitType convertToUnit(String koreanName) {
        for (UnitType unitType : UnitType.values()) {
            if (unitType.koreanName.equals(koreanName)) {
                return unitType;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + koreanName);
    }
}

