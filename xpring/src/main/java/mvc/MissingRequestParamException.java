package mvc;

public class MissingRequestParamException extends RuntimeException {
    private final String paramName;

    public MissingRequestParamException(String paramName) {
        super("Required request parameter '" + paramName + "' is missing");
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
