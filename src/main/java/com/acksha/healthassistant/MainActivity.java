package com.acksha.healthassistant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
            }
        }

        bottomNav = findViewById(R.id.bottomNav);

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                }

                if (id == R.id.nav_meds) {
                    startActivity(new Intent(this, MedicationActivity.class));
                    return true;
                }

                if (id == R.id.nav_sos) {
                    startActivity(new Intent(this, EmergencyActivity.class));
                    return true;
                }

                if (id == R.id.nav_chat) {
                    startActivity(new Intent(this, ChatbotActivity.class));
                    return true;
                }

                if (id == R.id.nav_lang) {
                    startActivity(new Intent(this, LanguageActivity.class));
                    return true;
                }

                return false;
            });
        }
    }
}