package com.primemedia.marvels.list;

public class TrendingList {
    private int ID;
    private int Type;
    private int contentType;
    private String Thumbnail;

    public TrendingList(int ID, int type, int contentType, String thumbnail) {
        this.ID = ID;
        Type = type;
        this.contentType = contentType;
        Thumbnail = thumbnail;
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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }
}
