package com.moviesAPI.moviesAPI.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(name = "image", columnDefinition="LONGBLOB")
    private byte[] image;

    private String title;

    private Integer releaseYear;

    private Integer rating;

    public Movie(){}
    public Movie(byte[] bytes, String title, Integer releaseYear, Integer rating) {
        this.image = bytes;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "movies", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("movies")
    private Set<Character> characters = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL,mappedBy = "movies", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Genre> genres = new HashSet<>();

    public Long getId() {
        return id;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Set<Character> getCharacters() {
        return characters;
    }

    public Set<Genre> getGenres() {
        return genres;
    }
}