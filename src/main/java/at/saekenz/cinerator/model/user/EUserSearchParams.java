package at.saekenz.cinerator.model.user;

public enum EUserSearchParams {
    USERNAME("username"),
    PASSWORD("password"),
    ROLE("role");

    private final String paramName;

    EUserSearchParams(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
