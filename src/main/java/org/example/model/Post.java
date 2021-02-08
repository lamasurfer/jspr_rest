package org.example.model;

import com.google.gson.annotations.Expose;

public class Post {
    @Expose
    private long id;
    @Expose
    private String content;
    private boolean flaggedAsRemoved;

    public Post() {
    }

    public Post(long id, String content) {
        this.id = id;
        this.content = content;
        this.flaggedAsRemoved = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFlaggedAsRemoved() {
        return flaggedAsRemoved;
    }

    public void setFlaggedAsRemoved(boolean flaggedAsRemoved) {
        this.flaggedAsRemoved = flaggedAsRemoved;
    }
}
