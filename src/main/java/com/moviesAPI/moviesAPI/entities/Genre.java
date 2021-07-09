package com.moviesAPI.moviesAPI.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity // This tells Hibernate to make a table out of this class
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "genres_movies",
            joinColumns = {
                    @JoinColumn(name = "genre_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    @JsonBackReference
    Set<Movie> movies = new HashSet<>();

    public Long getId() {
        return id;
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

    public Set<Movie> getMovies() {
        return movies;
    }
}
