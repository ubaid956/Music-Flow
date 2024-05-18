package com.example.musicflow;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer instance;
    public static int currentIndex = -1;

    public static  MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
       return instance;
    }

}
