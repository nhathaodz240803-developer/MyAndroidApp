package com.example.myapplication;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class NoteService extends Service {

    private static final String TAG        = "NoteService";
    private static final String CHANNEL_ID = "NoteServiceChannel";

    @Override public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent != null) ? intent.getStringExtra("action") : "";

        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Note Manager")
                .setContentText("Running in the background...")
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .build();
        startForeground(1, notif);

        if ("BACKUP".equals(action)) {
            new Thread(() -> {
                Log.d(TAG, "In progress (backing up)...");
                try { Thread.sleep(2000); } catch (InterruptedException e) { Log.e(TAG, "Sleep interrupted", e); }
                // Gửi broadcast báo xong
                sendBroadcast(new Intent("com.example.myapplication.BACKUP_DONE"));
                stopSelf(); // Tự dừng sau khi xong
            }).start();
        }
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Note Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}