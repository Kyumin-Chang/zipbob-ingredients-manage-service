package cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception;

import cloud.zipbob.ingredientsmanageservice.global.exception.BaseException;
import cloud.zipbob.ingredientsmanageservice.global.exception.BaseExceptionType;

public class IngredientException extends BaseException {
    private final BaseExceptionType exceptionType;

    public IngredientException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
