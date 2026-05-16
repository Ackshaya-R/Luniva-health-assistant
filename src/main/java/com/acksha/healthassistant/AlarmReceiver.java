package com.acksha.healthassistant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import androidx.core.app.NotificationCompat;

import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "luniva_reminder_channel";
    private static TextToSpeech textToSpeech;

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("message");

        if (message == null || message.isEmpty()) {
            message = context.getString(R.string.default_reminder_message);
        }

        // 1. Show notification
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(context.getString(R.string.notification_channel_desc));
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        // 2. Speak reminder aloud in selected language
        String finalMessage = message;

        SharedPreferences prefs = context.getSharedPreferences("LunivaPrefs", Context.MODE_PRIVATE);
        String lang = prefs.getString("language_code", "en");

        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {

                if (lang.equals("ta")) {
                    textToSpeech.setLanguage(new Locale("ta", "IN"));
                } else if (lang.equals("hi")) {
                    textToSpeech.setLanguage(new Locale("hi", "IN"));
                } else {
                    textToSpeech.setLanguage(new Locale("en", "IN"));
                }

                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.setPitch(1.0f);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(finalMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    textToSpeech.speak(finalMessage, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }
}