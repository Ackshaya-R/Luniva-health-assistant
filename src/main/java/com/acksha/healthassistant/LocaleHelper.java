package com.acksha.healthassistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public class LocaleHelper {
    public static Context setLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("LunivaPrefs", Context.MODE_PRIVATE);
        String languageCode = prefs.getString("language_code", "en");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences("LunivaPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("language_code", languageCode).apply();
    }
}
