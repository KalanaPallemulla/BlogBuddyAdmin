package com.kalz.blogbuddyadminpannel;

public class User {

    public String image, name, about;

    public User() {

    }


    public User(String image, String name, String about) {
        this.image = image;
        this.name = name;
        this.about = about;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
