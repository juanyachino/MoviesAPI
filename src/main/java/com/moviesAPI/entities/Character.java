package com.moviesAPI.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Lob
    @Column(name = "image", columnDefinition="LONGBLOB")
    private byte[] image;
    @NotEmpty
    private String name;
    @Min(1)
    private Integer age;
    @Min(1)
    private Integer weight;
    @NotEmpty
    private String story;

    @ManyToMany
    @JoinTable(name = "characters_movies",
            joinColumns = {
                    @JoinColumn(name = "character_id", referencedColumnName = "id",
                            nullable = false, updatable = false,
                            foreignKey = @ForeignKey(
                            name="FK_CHAR_ID",
                            foreignKeyDefinition = "FOREIGN KEY (character_id) REFERENCES CHARACTERS(id) ON UPDATE CASCADE ON DELETE CASCADE"))},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false,
                            foreignKey = @ForeignKey(
                            name="FK_MOVIE_ID",
                            foreignKeyDefinition = "FOREIGN KEY (movie_id) REFERENCES MOVIES(id) ON UPDATE CASCADE ON DELETE CASCADE")
                    )})
    @JsonIgnoreProperties("characters")
    Set<Movie> movies = new HashSet<>();

    public Character(){ }
    public Character(byte[] image, String name, Integer age, Integer weight, String story) {
        this.image = image;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.story = story;
    }


    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

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

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}