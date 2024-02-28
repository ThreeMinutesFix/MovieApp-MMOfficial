package com.primemedia.studioflix.list;

public class MoviesCollections {
    int id;
    int type;
    String contentName;
    String year;
    String poster;
    int contentType;

    public MoviesCollections(int id, int type, String contentName, String year, String poster, int contentType) {
        this.id = id;
        this.type = type;
        this.contentName = contentName;
        this.year = year;
        this.poster = poster;
        this.contentType = contentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
}
