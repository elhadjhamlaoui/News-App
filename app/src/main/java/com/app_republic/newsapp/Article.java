package com.app_republic.newsapp;

public class Article {
    private String title, description, author, date, section, link;

    public Article(String title, String description, String author, String date, String section, String link) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.date = date;
        this.section = section;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getSection() {
        return section;
    }

    public String getLink() {
        return link;
    }

}
