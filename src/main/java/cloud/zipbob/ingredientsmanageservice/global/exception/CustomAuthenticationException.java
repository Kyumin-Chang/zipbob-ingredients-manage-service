package cloud.zipbob.ingredientsmanageservice.global.exception;

public class CustomAuthenticationException extends BaseException {
    private final BaseExceptionType exceptionType;

    public CustomAuthenticationException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
