package cloud.zipbob.ingredientsmanageservice.domain.ingredient.exception;

import cloud.zipbob.ingredientsmanageservice.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum IngredientExceptionType implements BaseExceptionType {
    INGREDIENT_NOT_FOUND("I001", "해당 회원에게 재료가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INGREDIENT_NAME_ERROR("I002", "존재하지 않는 재료입니다.", HttpStatus.BAD_REQUEST),
    INGREDIENT_TYPE_ERROR("I003", "존재하지 않은 재료의 타입입니다.", HttpStatus.BAD_REQUEST),
    INGREDIENT_ALREADY_EXIST("I004", "이미 해당 회원의 냉장고에 재료가 존재합니다.", HttpStatus.CONFLICT),
    INGREDIENT_DELETE_ERROR("I005", "냉장고에 재료가 부족하거나 단위가 맞지 않습니다.", HttpStatus.NOT_FOUND);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    IngredientExceptionType(String errorCode, String errorMessage, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
