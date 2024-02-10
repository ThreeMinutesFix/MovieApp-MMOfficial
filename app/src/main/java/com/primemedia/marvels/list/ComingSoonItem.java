package com.primemedia.marvels.list;

public class ComingSoonItem {
    private int ID;
    private int Type;
    private String Title;
    private String Year;
    private String Thumbnail;
    private String logoMovies;
    String rating;
    String certificate_type;
    String desc;
    String genres;

    public ComingSoonItem(int ID, int type, String title, String year, String thumbnail, String logoMovies, String rating, String certificate_type, String desc, String genres) {
        this.ID = ID;
        Type = type;
        Title = title;
        Year = year;
        Thumbnail = thumbnail;
        this.logoMovies = logoMovies;
        this.rating = rating;
        this.certificate_type = certificate_type;
        this.desc = desc;
        this.genres = genres;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getLogoMovies() {
        return logoMovies;
    }

    public void setLogoMovies(String logoMovies) {
        this.logoMovies = logoMovies;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCertificate_type() {
        return certificate_type;
    }

    public void setCertificate_type(String certificate_type) {
        this.certificate_type = certificate_type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
