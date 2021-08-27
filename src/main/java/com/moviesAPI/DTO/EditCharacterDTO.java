package com.moviesAPI.DTO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EditCharacterDTO {
    @NotNull
    private Long id;

    private String name;
    @Min(1)
    private Integer age;
    @Min(1)
    private Integer weight;

    private String story;

    List<Long> moviesIds = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }
    public List<Long> getMoviesIds() {
        return moviesIds;
    }

    public void setMoviesIds(List<Long> moviesIds) {
        this.moviesIds = moviesIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
