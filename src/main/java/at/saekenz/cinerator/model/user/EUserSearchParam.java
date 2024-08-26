package at.saekenz.cinerator.model.user;

public enum EUserSearchParam {
    USERNAME("username"),
    PASSWORD("password"),
    ROLE("role");

    private final String paramName;

    EUserSearchParam(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
