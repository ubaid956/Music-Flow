package com.example.musicflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.database.Cursor;
import android.provider.MediaStore;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    AppCompatActivity is an extension of the standard Activity class that comes from the AndroidX library.
//    It offers compatibility and additional features to help developers create apps that work well across different versions of Android.
//    It's designed to bring modern features, such as material design components and support for action bars, to older Android versions.



//      ActivityCompat: It provides compatibility and utility methods for working with various aspects of activities, fragments, and permissions in Android.
//      1) Permission Handling
//      2) Activity Transitions
//      3) Fragment Management: ActivityCompat can assist with fragment transactions, such as adding, replacing, or removing fragments.
//      4) Requesting Permissions

    RecyclerView recyclerView;
    static final int PERMISSION_REQUEST_CODE = 123;


    ArrayList<AudioModel> songList = new ArrayList<>();

    TextView noMusicTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        noMusicTextView = findViewById(R.id.noMusicTextView);
        NotificationChannel channel = new NotificationChannel("Music Flow","My notification ", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);


        if(checkPermission()){
           loadMusicData();
        }

        else {
            requestPermission();
        }


    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Read Permission is Required, Allows form Setting", Toast.LENGTH_LONG).show();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    void loadMusicData() {

        String [] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,   //stores the file path of media file
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +"!=0";    //filter to retrieve only music file

//        Cursor:  is an interface
//        used when you need to interact with structured data stored in a database, such as reading records from a table.

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        while (cursor.moveToNext()){
            Log.d("File_1",cursor.getString(1));
//            Log.d("File_2",cursor.getString(10));


//            cursor.getString(1):  likely related to the title of the audio file.
//            cursor.getString(0):  is likely related to the path of the audio file.
//            cursor.getString(2):  likely related to the artist or some other attribute of the audio file.

            AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songList.add(songData);
        }

        if(songList.size() ==0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songList, getApplicationContext()));
            Log.d("MySongList", String.valueOf(songList.size()));
        }
    }

//    activity comes into the foreground (resumes) after being paused or stopped.
    @Override
    protected void onResume(){
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songList, getApplicationContext()));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusicData(); // Permission granted, load music data
            } else {
                // Handle permission denial
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}