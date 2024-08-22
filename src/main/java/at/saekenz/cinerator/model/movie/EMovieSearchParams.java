package at.saekenz.cinerator.model.movie;

public enum EMovieSearchParams {
    TITLE("title"),
    DIRECTOR("director"),
    GENRE("genre"),
    RELEASE_DATE("release date"),
    YEAR("year"),
    IMDB_ID("IMDb ID"),
    COUNTRY("country");

    private String paramName;

    EMovieSearchParams(String paramName) { this.paramName = paramName; }

    public String getParamName() { return paramName; }
}
