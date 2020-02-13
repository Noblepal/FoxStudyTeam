package com.trichain.foxstudyteam.models;

public class News {
    private String category, title, link, imagr_url;

    public News(String category, String title, String link, String imagr_url) {
        this.category = category;
        this.title = title;
        this.link = link;
        this.imagr_url = imagr_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImagr_url() {
        return imagr_url;
    }

    public void setImagr_url(String imagr_url) {
        this.imagr_url = imagr_url;
    }
}
