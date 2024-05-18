package com.example.musicflow.Services;
import static android.app.Service.START_STICKY;

import static com.example.musicflow.CreateNotification.ACTION_PLAY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionServices extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("TRACKS_TRACKS")
                .putExtra("actionname", intent.getAction()));
    }

}
