package com.example.bookcollection;

import java.io.Serializable;

public class books implements Serializable {

    String id;
    String title;
    String author;
    String year;
    String fav;
    String url;

    public books() {
    }

    public books(String id, String title, String author, String year, String fav, String url) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.fav = fav;
        this.url = url;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}
