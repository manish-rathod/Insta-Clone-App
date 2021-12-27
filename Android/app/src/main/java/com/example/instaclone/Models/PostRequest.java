package com.example.instaclone.Models;

import java.io.File;

public class PostRequest {
    private String content;
    private String access;

    public void setContent(String content) {
        this.content = content;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getContent() {
        return content;
    }

    public String getAccess() {
        return access;
    }
}
