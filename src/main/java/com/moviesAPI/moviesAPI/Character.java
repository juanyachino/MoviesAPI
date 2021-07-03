package com.moviesAPI.moviesAPI;

import javax.persistence.*;


@Entity // This tells Hibernate to make a table out of this class
public class Character {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;

    private String name;

    private Integer age;

    private Integer weight;

    private String story;

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
}