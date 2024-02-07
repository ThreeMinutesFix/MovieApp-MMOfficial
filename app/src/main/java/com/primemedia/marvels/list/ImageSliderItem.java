package com.primemedia.marvels.list;

public class ImageSliderItem
{
    private String image;
    private String Title;
    private String poster;
    private int Content_Type;
    private int Content_ID;
    private String URL;
    private String Logo_imgs;
    private String language;
    private String year;
    private String genre;

    public ImageSliderItem(String image, String title, String poster, int content_Type, int content_ID, String URL, String logo_imgs, String language, String year, String genre) {
        this.image = image;
        Title = title;
        this.poster = poster;
        Content_Type = content_Type;
        Content_ID = content_ID;
        this.URL = URL;
        Logo_imgs = logo_imgs;
        this.language = language;
        this.year = year;
        this.genre = genre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(int content_Type) {
        Content_Type = content_Type;
    }

    public int getContent_ID() {
        return Content_ID;
    }

    public void setContent_ID(int content_ID) {
        Content_ID = content_ID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLogo_imgs() {
        return Logo_imgs;
    }

    public void setLogo_imgs(String logo_imgs) {
        Logo_imgs = logo_imgs;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
