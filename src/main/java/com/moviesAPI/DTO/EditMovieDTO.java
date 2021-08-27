package com.moviesAPI.DTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EditMovieDTO {
    @NotNull
    private Long id;
    private String title;

    @Min(1900)
    private Integer releaseYear;

    @Min(1)
    @Max(5)
    private Integer rating;
    List<Long> charactersIds = new ArrayList<>();
    List<Long> genresIds = new ArrayList<>();

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

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<Long> getCharactersIds() {
        return charactersIds;
    }

    public void setCharactersIds(List<Long> charactersIds) {
        this.charactersIds = charactersIds;
    }

    public List<Long> getGenresIds() {
        return genresIds;
    }

    public void setGenresIds(List<Long> genresIds) {
        this.genresIds = genresIds;
    }
}
