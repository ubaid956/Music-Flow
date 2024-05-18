package com.example.musicflow;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.musicflow.Services.OnClearFromRecentServices;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.Collections;


public class MusicPlayerActivity extends AppCompatActivity implements Playable {

    NotificationManager notificationManager;

    TextView tittleTv, currentTv, totalTimetv;
    SeekBar seekBar;
    ImageView pausePlay, nextbtn, previousbtn, musicIcon, repeat, shuffle;
    ArrayList<AudioModel> songList;
    public static MediaPlayer myMediaPlayer = MyMediaPlayer.getInstance();
    AudioModel currentSong;

    boolean isPlaying = false;

    boolean isChange, isChangeTwice, shuffleOn = false;

//    Bitmap walpaper;

    public MusicPlayerActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        tittleTv = findViewById(R.id.song_title);
        currentTv = findViewById(R.id.current_time);
        totalTimetv = findViewById(R.id.total_time);
        repeat = findViewById(R.id.repeat);
        shuffle = findViewById(R.id.shuffle);


//       walpaper= (Bitmap) getLockScreenWallpaper(getApplicationContext());

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");

                switch (action) {
                    case CreateNotification.ACTION_PREVIUOS:
                        MyMediaPlayer.currentIndex--;
                        setResourcesWith();

//                        playPreviousSong();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if (isPlaying) {
                            pausePlay();
                        } else {
                            pausePlay();
                        }
                        break;
                    case CreateNotification.ACTION_NEXT:
                        playNextSong();
                        break;
                }
            }
        };

        createChannel();
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentServices.class));
        tittleTv.setSelected(true);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause);


        nextbtn = findViewById(R.id.next);
        previousbtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.img);
        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWith();

//        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if(myMediaPlayer!=null){
//                    seekBar.setProgress(myMediaPlayer.getCurrentPosition());
//                    currentTv.setText(convertToMMS(myMediaPlayer.getCurrentPosition()+" "));
//                    if(myMediaPlayer.isPlaying()){
//                        pausePlay.setImageResource(R.drawable.pause);
//                    }
//                    else {
//                        pausePlay.setImageResource(R.drawable.play);
//                    }
//                }
//                new Handler().postDelayed(this, 100);
//            }
//        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(myMediaPlayer!=null && fromUser){
//                    myMediaPlayer.seekTo(progress);
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

    }



//    public void recoverWalpaper(){
//        WallpaperManager manager = WallpaperManager.getInstance(this);
//        try{
//            manager.setBitmap(walpaper, null, true, WallpaperManager.FLAG_LOCK);
//
//        }catch (Exception e){
//            e.getStackTrace();
//        }
//    }

    void setResourcesWith() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        tittleTv.setText(currentSong.getTitle());
        totalTimetv.setText(convertToMMSS(currentSong.getDuration()));
        repeat.setOnClickListener(v -> {
            if (!isChange) {
                repeat.setImageResource(R.drawable.repeatpurple);
                playNextSong();
                isChange = true;
                isChangeTwice = false; // Reset the second change flag
            } else if (!isChangeTwice) {
                repeat.setImageResource(R.drawable.repeatonepink);
                myMediaPlayer.setOnCompletionListener(mp -> myMediaPlayer.start());
                isChangeTwice = true;
            } else {
                repeat.setImageResource(R.drawable.baseline_repeat_24);
                isChange = isChangeTwice = false;
            }
        });
        shuffle.setOnClickListener(v -> {
            if (!shuffleOn) {
                shuffle.setImageResource(R.drawable.shuffle_on);
                shuffleOn = true;
                Shuffling();
            } else {
                shuffle.setImageResource(R.drawable.shuffle);
                shuffleOn = false;
            }
        });
        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlay();
            }
        });
        nextbtn.setOnClickListener(v -> playNextButton());
        previousbtn.setOnClickListener(v -> playPreviousSong());
        playMusic();

    }

    public void pausePlay() {
        if (myMediaPlayer.isPlaying()) {
            CreateNotification.isNotificationDis = false;
//            recoverWalpaper();
            pausePlay.setImageResource(R.drawable.baseline_play_circle_24);
            CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                    R.drawable.baseline_play_circle_24, MyMediaPlayer.currentIndex, songList.size() - 1);
            myMediaPlayer.pause();


        } else {
            pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
            CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                    R.drawable.baseline_pause_circle_24, MyMediaPlayer.currentIndex, songList.size() - 1);
            CreateNotification.isNotificationDis = true;
            myMediaPlayer.start();

        }
    }

    /**
     * please check permission outside
     * @return Bitmap or Drawable
     */
    public static Object getLockScreenWallpaper(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);// TODO: Consider calling
//    ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                          int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
//                return TODO;
//The FileDescriptor returned by Parcel#readFileDescriptor, allowing you to close it when done with it.
        ParcelFileDescriptor pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
        if (pfd == null)
            pfd = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM);
        if (pfd != null)
        {
            final Bitmap result = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
            try
            {
                pfd.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }
        return wallpaperManager.getDrawable();
    }

    public void playMusic() {

        try{
          CreateNotification.isNotificationDis = true;
            myMediaPlayer.reset();
            myMediaPlayer.setDataSource(currentSong.getPath());
//            Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icon);
//            WallpaperManager manager = WallpaperManager.getInstance(this);
//            try{
//                manager.setBitmap(icon, null, true, WallpaperManager.FLAG_LOCK);
//
//            }catch (Exception e){
//                e.getStackTrace();
//            }
            myMediaPlayer.prepare();
            seekBar.setMax(myMediaPlayer.getDuration());
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    seekBar.setProgress(myMediaPlayer.getCurrentPosition());
                    int currentPosition = myMediaPlayer.getCurrentPosition();
                    currentTv.setText(convertToMMSS(currentPosition+""));

                }
            },0,900 );

            pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        myMediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            myMediaPlayer.start();
            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextButton();
                }
            });



        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void playNextButton(){
        if(MyMediaPlayer.currentIndex == songList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex +=1;
        myMediaPlayer.reset();
        pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
        CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                R.drawable.baseline_pause_circle_24,MyMediaPlayer.currentIndex , songList.size()-1);
        setResourcesWith();

    }
    public void playNextSong(){
        if(MyMediaPlayer.currentIndex == songList.size()-1){
             return;
        }
        myMediaPlayer.setOnCompletionListener(mp -> {
            MyMediaPlayer.currentIndex +=1;
            myMediaPlayer.reset();
            pausePlay.setImageResource(R.drawable.baseline_pause_circle_24);
            CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                    R.drawable.baseline_pause_circle_24,MyMediaPlayer.currentIndex , songList.size()-1);
            setResourcesWith();
        });
    }
    public void Shuffling(){
        myMediaPlayer.setOnCompletionListener(mp -> {
            Collections.shuffle(songList);
            myMediaPlayer.reset();
            CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                    R.drawable.baseline_pause_circle_24,MyMediaPlayer.currentIndex , songList.size()-1);
            setResourcesWith();
        });

    }
    public  void playPreviousSong(){
        if(MyMediaPlayer.currentIndex == 0){
            MyMediaPlayer.currentIndex = songList.size()-1;
            myMediaPlayer.reset();
            CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                    R.drawable.baseline_pause_circle_24,MyMediaPlayer.currentIndex , songList.size()-1);
            setResourcesWith();

        }
        MyMediaPlayer.currentIndex -=1;

        myMediaPlayer.reset();
        CreateNotification.createNotification(getApplicationContext(), songList.get(MyMediaPlayer.currentIndex),
                R.drawable.baseline_pause_circle_24,MyMediaPlayer.currentIndex , songList.size()-1);
        setResourcesWith();
    }

    public static String convertToMMSS(String duration){
        long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }



    //must create the notification channel before posting any notifications on Android 8.0 and higher,
    public  void createChannel() {
        NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                "KOD Dev", NotificationManager.IMPORTANCE_LOW);


        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null){
            notificationManager.createNotificationChannel(channel);
        }
    }


}