package at.saekenz.cinerator.model.actor;

public enum EActorSearchParam {
    NAME("name"),
    BIRTH_DATE("birth_date"),
    BIRTH_COUNTRY("birth_country"),
    AGE("age");

    private final String paramName;

    EActorSearchParam(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() { return paramName; }
}
