package at.saekenz.cinerator.model.movie;

import java.time.LocalDate;

public class MovieDTO {

    private Long id;
    private String title;
    private LocalDate releaseDate;
    private String runtime;
    private String director;
    private String genre;
    private String country;
    private String imdbId;
    private String posterUrl;

    public MovieDTO() {}

    public MovieDTO(Long id, String title, LocalDate releaseDate,
                    String runtime, String director, String genre,
                    String country, String imdbId, String posterUrl) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.director = director;
        this.genre = genre;
        this.country = country;
        this.imdbId = imdbId;
        this.posterUrl = posterUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getReleaseYear() {
        return releaseDate != null ? releaseDate.getYear() : 0;
    }
}
