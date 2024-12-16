package com.hemaya.mssdemo.utils.langauge;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.service.autofill.UserData;

import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;

import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
          return   context.createConfigurationContext(config);
        } else {
            config.locale = locale;
             resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }
    public static Context getLanguageAwareContext(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(new Locale(new SharedPreferenceStorage(context).getLanguage()));
        return context.createConfigurationContext(configuration);
    }

}

