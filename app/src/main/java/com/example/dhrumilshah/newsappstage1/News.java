package com.example.dhrumilshah.newsappstage1;

import java.util.ArrayList;

class News {
    private String title;
    private String section;
    private String webPublicationDateAndTime;
    private String webUrl;
    private String thumbnail;
    private ArrayList<String> authors;

    News(String title, String section, String webPublicationDateAndTime, String webUrl, String thumbnail, ArrayList<String> authors) {
        this.title = title;
        this.section = section;
        this.webPublicationDateAndTime = webPublicationDateAndTime;
        this.webUrl = webUrl;
        this.thumbnail = thumbnail;
        this.authors = authors;
    }

    String getTitle() {
        return title;
    }

    String getSection() {
        return section;
    }

    String getWebPublicationDateAndTime() {
        return webPublicationDateAndTime;
    }

    String getWebUrl() {
        return webUrl;
    }

    String getThumbnail() {
        return thumbnail;
    }

    ArrayList<String> getAuthors() {
        return authors;
    }
}
