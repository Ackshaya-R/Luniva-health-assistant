package com.acksha.healthassistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedicationActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;

    EditText medicineName, reminderTime;
    Button voiceBtn, saveReminderBtn;
    BottomNavigationView bottomNav;
    TextToSpeech textToSpeech;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        medicineName = findViewById(R.id.medicineName);
        reminderTime = findViewById(R.id.reminderTime);
        voiceBtn = findViewById(R.id.voiceBtn);
        saveReminderBtn = findViewById(R.id.saveReminderBtn);
        bottomNav = findViewById(R.id.bottomNav);

        // Text to Speech init
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                SharedPreferences prefs = getSharedPreferences("LunivaPrefs", MODE_PRIVATE);
                String lang = prefs.getString("language_code", "en");

                if (lang.equals("ta")) {
                    textToSpeech.setLanguage(new Locale("ta", "IN"));
                } else if (lang.equals("hi")) {
                    textToSpeech.setLanguage(new Locale("hi", "IN"));
                } else {
                    textToSpeech.setLanguage(new Locale("en", "IN"));
                }

                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.setPitch(1.0f);
            }
        });

        // Voice input
        voiceBtn.setOnClickListener(v -> startVoiceInput());

        // Manual reminder save
        saveReminderBtn.setOnClickListener(v -> setReminder());

        // Bottom navigation
        bottomNav.setSelectedItemId(R.id.nav_meds);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_meds) return true;
            else if (id == R.id.nav_sos) startActivity(new Intent(this, EmergencyActivity.class));
            else if (id == R.id.nav_chat) startActivity(new Intent(this, ChatbotActivity.class));
            else if (id == R.id.nav_lang) startActivity(new Intent(this, LanguageActivity.class));

            return true;
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        SharedPreferences prefs = getSharedPreferences("LunivaPrefs", MODE_PRIVATE);
        String lang = prefs.getString("language_code", "en");

        if (lang.equals("ta")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN");
        } else if (lang.equals("hi")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.voice_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    private void setReminder() {
        String messageText = medicineName.getText().toString().trim();
        String mins = reminderTime.getText().toString().trim();

        if (messageText.isEmpty() || mins.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
            speakText(getString(R.string.fill_fields));
            return;
        }

        try {
            int minutes = Integer.parseInt(mins);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, minutes);

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("message", messageText);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }

            String confirmMessage = getString(R.string.reminder_set_success);
            Toast.makeText(this, confirmMessage, Toast.LENGTH_SHORT).show();
            speakText(confirmMessage);

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.enter_minutes_only), Toast.LENGTH_SHORT).show();
            speakText(getString(R.string.enter_minutes_only));
        }
    }

    private void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0).toLowerCase().trim();

                // Try extracting minutes
                int extractedMinutes = extractMinutes(spokenText);

                // Remove timing words from reminder text
                String reminderMessage = cleanReminderText(spokenText);

                if (!reminderMessage.isEmpty()) {
                    medicineName.setText(reminderMessage);
                }

                if (extractedMinutes > 0) {
                    reminderTime.setText(String.valueOf(extractedMinutes));

                    // Auto set reminder directly
                    setReminder();
                } else {
                    // If no time found, just fill reminder field
                    medicineName.setText(spokenText);
                    Toast.makeText(this, getString(R.string.say_time_format), Toast.LENGTH_SHORT).show();
                    speakText(getString(R.string.say_time_format));
                }
            }
        }
    }

    private int extractMinutes(String spokenText) {
        // English examples: "drink water after 1 minute", "call doctor in 5 minutes"
        Pattern englishPattern = Pattern.compile("(\\d+)\\s*(minute|minutes|min)");
        Matcher englishMatcher = englishPattern.matcher(spokenText);
        if (englishMatcher.find()) {
            return Integer.parseInt(englishMatcher.group(1));
        }

        // Tamil examples: "1 நிமிடம்", "5 நிமிஷம்"
        Pattern tamilPattern = Pattern.compile("(\\d+)\\s*(நிமிடம்|நிமிஷம்)");
        Matcher tamilMatcher = tamilPattern.matcher(spokenText);
        if (tamilMatcher.find()) {
            return Integer.parseInt(tamilMatcher.group(1));
        }

        // Hindi examples: "1 मिनट", "5 मिनिट"
        Pattern hindiPattern = Pattern.compile("(\\d+)\\s*(मिनट|मिनिट)");
        Matcher hindiMatcher = hindiPattern.matcher(spokenText);
        if (hindiMatcher.find()) {
            return Integer.parseInt(hindiMatcher.group(1));
        }

        return -1;
    }

    private String cleanReminderText(String spokenText) {
        // Remove common time phrases in English
        spokenText = spokenText.replaceAll("\\bafter\\b", "")
                .replaceAll("\\bin\\b", "")
                .replaceAll("\\b\\d+\\s*(minute|minutes|min)\\b", "");

        // Remove Tamil timing words
        spokenText = spokenText.replaceAll("\\b\\d+\\s*(நிமிடம்|நிமிஷம்)\\b", "")
                .replaceAll("கழித்து", "");

        // Remove Hindi timing words
        spokenText = spokenText.replaceAll("\\b\\d+\\s*(मिनट|मिनिट)\\b", "")
                .replaceAll("बाद", "");

        return spokenText.trim();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}