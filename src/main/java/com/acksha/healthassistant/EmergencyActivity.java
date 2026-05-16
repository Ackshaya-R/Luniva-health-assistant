package com.acksha.healthassistant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmergencyActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        EditText emergencyNumber = findViewById(R.id.emergencyNumber);
        Button saveContactBtn = findViewById(R.id.saveContactBtn);
        Button sosBtn = findViewById(R.id.sosBtn);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences prefs = getSharedPreferences("LunivaPrefs", MODE_PRIVATE);
        emergencyNumber.setText(prefs.getString("emergency_number", ""));

        saveContactBtn.setOnClickListener(v -> {
            prefs.edit().putString("emergency_number", emergencyNumber.getText().toString().trim()).apply();
            Toast.makeText(this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show();
        });

        sosBtn.setOnClickListener(v -> {
            String number = emergencyNumber.getText().toString().trim();
            if (number.isEmpty()) {
                Toast.makeText(this, getString(R.string.save_number_first), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }
            startActivity(callIntent);
        });

        bottomNav.setSelectedItemId(R.id.nav_sos);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_meds) startActivity(new Intent(this, MedicationActivity.class));
            else if (id == R.id.nav_sos) return true;
            else if (id == R.id.nav_chat) startActivity(new Intent(this, ChatbotActivity.class));
            else if (id == R.id.nav_lang) startActivity(new Intent(this, LanguageActivity.class));
            return true;
        });
    }
}
