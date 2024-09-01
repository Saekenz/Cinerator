package at.saekenz.cinerator.model.actor;

public enum EActorSearchParam {
    NAME("name"),
    BIRTH_DATE("birthDate"),
    BIRTH_COUNTRY("birthCountry"),
    AGE("age");

    private final String paramName;

    EActorSearchParam(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() { return paramName; }
}
