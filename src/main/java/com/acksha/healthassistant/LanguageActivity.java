package com.acksha.healthassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LanguageActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        RadioGroup languageGroup = findViewById(R.id.languageGroup);
        Button saveBtn = findViewById(R.id.saveLanguageBtn);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        SharedPreferences prefs = getSharedPreferences("LunivaPrefs", MODE_PRIVATE);
        String lang = prefs.getString("language_code", "en");
        if (lang.equals("ta")) languageGroup.check(R.id.tamilBtn);
        else if (lang.equals("hi")) languageGroup.check(R.id.hindiBtn);
        else languageGroup.check(R.id.englishBtn);

        saveBtn.setOnClickListener(v -> {
            int checkedId = languageGroup.getCheckedRadioButtonId();
            String code = "en";
            if (checkedId == R.id.tamilBtn) code = "ta";
            else if (checkedId == R.id.hindiBtn) code = "hi";
            LocaleHelper.saveLanguage(this, code);
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        bottomNav.setSelectedItemId(R.id.nav_lang);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_meds) startActivity(new Intent(this, MedicationActivity.class));
            else if (id == R.id.nav_sos) startActivity(new Intent(this, EmergencyActivity.class));
            else if (id == R.id.nav_chat) startActivity(new Intent(this, ChatbotActivity.class));
            else if (id == R.id.nav_lang) return true;
            return true;
        });
    }
}
