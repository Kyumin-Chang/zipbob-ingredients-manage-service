package cloud.zipbob.ingredientsmanageservice.global.exception;

import org.springframework.http.HttpStatus;

public enum CustomAuthenticationExceptionType implements BaseExceptionType {
    AUTHENTICATION_DENIED("AT001", "올바르지 않은 사용자의 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    CustomAuthenticationExceptionType(String errorCode, String errorMessage, HttpStatus httpStatus) {
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
