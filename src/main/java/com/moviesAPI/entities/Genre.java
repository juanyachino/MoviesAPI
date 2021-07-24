package com.moviesAPI.entities;

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
    @Column(name = "image", columnDefinition="LONGBLOB")
    private byte[] image;

    private String name;

    @ManyToMany
    @JoinTable(name = "genres_movies",
            joinColumns = {
                    @JoinColumn(name = "genre_id", referencedColumnName = "id",
                            nullable = false, updatable = false,
                            foreignKey = @ForeignKey(
                                    name="FK_GENRE_ID",
                                    foreignKeyDefinition = "FOREIGN KEY (genre_id) REFERENCES GENRE(id) ON UPDATE CASCADE ON DELETE CASCADE"))},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false,
                            foreignKey = @ForeignKey(
                                    name="FK_MOVIE_ID",
                                    foreignKeyDefinition = "FOREIGN KEY (movie_id) REFERENCES MOVIE(id) ON UPDATE CASCADE ON DELETE CASCADE"))})
    @JsonBackReference
    Set<Movie> movies = new HashSet<>();

    public Genre(){}
    public Genre(String name, byte[] bytes) {
        this.name = name;
        this.image = bytes;
    }

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
