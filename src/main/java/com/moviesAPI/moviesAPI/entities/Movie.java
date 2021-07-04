package com.moviesAPI.moviesAPI.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;

    private String title;

    private String date;

    private Integer rating;

    @ManyToMany(mappedBy = "movies", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Character> characters = new HashSet<>();

    @ManyToMany(mappedBy = "movies", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Character> genres = new HashSet<>();

    public Integer getId() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
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

    public Set<Character> getGenres() {
        return genres;
    }
}