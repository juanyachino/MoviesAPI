package com.moviesAPI.DTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class GenreDTO {
    @NotNull
    private String name;
    List<Long> moviesIds = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMoviesIds() {
        return moviesIds;
    }

    public void setMoviesIds(List<Long> moviesIds) {
        this.moviesIds = moviesIds;
    }
}
