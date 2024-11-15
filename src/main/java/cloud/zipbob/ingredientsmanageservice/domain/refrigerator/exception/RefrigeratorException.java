package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.exception;

import cloud.zipbob.ingredientsmanageservice.global.exception.BaseException;
import cloud.zipbob.ingredientsmanageservice.global.exception.BaseExceptionType;

public class RefrigeratorException extends BaseException {
    private final BaseExceptionType exceptionType;

    public RefrigeratorException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
