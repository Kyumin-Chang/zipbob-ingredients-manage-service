package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception;

import cloud.zipbob.ingredientsmanageservice.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum RefrigeratorExceptionType implements BaseExceptionType {
    ALREADY_EXIST_REFRIGERATOR("R001", "해당 회원의 냉장고가 이미 존재합니다.", HttpStatus.CONFLICT),
    REFRIGERATOR_NOT_FOUND("R001", "해당 회원의 냉장고가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    RefrigeratorExceptionType(String errorCode, String errorMessage, HttpStatus httpStatus) {
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
