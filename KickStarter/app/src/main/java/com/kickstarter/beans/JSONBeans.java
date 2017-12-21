package com.kickstarter.beans;

import android.net.Uri;

import java.net.URI;

public class JSONBeans {
    int id;
    String title;
    Uri uri;
    String publisher;
    String category;
    Uri hostname;
    long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Uri getHostname() {
        return hostname;
    }

    public void setHostname(Uri hostname) {
        this.hostname = hostname;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
