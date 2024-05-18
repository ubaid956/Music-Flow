package com.example.musicflow;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String path;
    public AudioModel(String path, String title, String duration){
        this.path = path;
        this.title = title;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    String title;
    String duration;

}
