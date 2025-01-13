package com.hemaya.mssdemo.view.splash;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.home.HomeView;
import com.hemaya.mssdemo.view.identification.IdentificationView;

import java.util.Locale;
import java.util.Timer;

public class SplashView extends BaseActivity {

    ProgressBar progressBar;
    UserDatabaseHelper userDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init() {
        setLanguage();
        progressBar = findViewById(R.id.progressBar);
        userDatabaseHelper = new UserDatabaseHelper(this);
        progressBar.setIndeterminate(true);

        new Handler().postDelayed(() -> {
            progressBar.setIndeterminate(false);
            if (userDatabaseHelper.isAtLeastOneUserUsed()) {
                Intent intent = new Intent(this, HomeView.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(this, IdentificationView.class);
                startActivity(intent);
            }
            finish(); // Close the SplashActivity so it won't be in the back stack
        }, 1000);
    }

    private void setLanguage() {
        // Set the language of the app
       String language = getPrimaryLanguage();
        SharedPreferenceStorage sharedPreferenceStorage = new SharedPreferenceStorage(this);
        if (sharedPreferenceStorage.getLanguage().isEmpty()) {
           sharedPreferenceStorage.setLanguage(language);
       }
    }
    public String getPrimaryLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = Resources.getSystem().getConfiguration().locale;
        }
        return locale.getDisplayLanguage();
    }
}