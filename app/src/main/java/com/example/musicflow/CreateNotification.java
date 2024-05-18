package com.example.musicflow;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import androidx.core.app.NotificationManagerCompat;

import com.example.musicflow.Services.NotificationActionServices;


public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;
    public static boolean isNotificationDis = false;
    public static PendingIntent pendingIntent,pendingClick;

    public static void createNotification(Context context, AudioModel track, int playbutton, int pos, int size) {

        //Notification Compat is an API
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

        //PendingIntent -> class
        //the returned object can be handed to other applications so that they can perform the action
        // you described on your behalf at a later time.

        try{
            Intent showNotification = new Intent(context, MusicPlayerActivity.class);
            pendingIntent = PendingIntent.getActivity(context, 0, showNotification, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        }
        catch (Exception e){
            e.getStackTrace();
        }

        PendingIntent pendingIntentPrevious;
        int drw_previous;
        if (pos == 0) {
            pendingIntentPrevious = null;
            //                drw_previous = 0;
        } else {
            Intent intentPrevious = new Intent(context, NotificationActionServices.class)
                    .setAction(ACTION_PREVIUOS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        drw_previous = R.drawable.baseline_skip_previous_24;

        Intent clickIntent = new Intent(context, MainActivity.class);
        pendingClick  = PendingIntent.getActivity(context, 1, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent intentPlay = new Intent(context, NotificationActionServices.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentNext;
        int drw_next;
        if (pos == size) {
            pendingIntentNext = null;
            //                drw_next = 0;
        } else {

            //Give BroadCastReciver.class or Service.class
            Intent intentNext = new Intent(context, NotificationActionServices.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        drw_next = R.drawable.baseline_skip_next_24;

        //create notification
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)

                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Music Flow")
                .setContentText(track.getTitle())
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)//show notification for only first time
                .setShowWhen(false)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                //Give icon, give String and give pendingIntent

//                    .setContentIntent(controller.getSessionActivity())
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(playbutton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingClick)
                .setChannelId(CHANNEL_ID)
//                .setOngoing(true)

                .build();

        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        notificationManagerCompat.notify(1, notification);

    }
}