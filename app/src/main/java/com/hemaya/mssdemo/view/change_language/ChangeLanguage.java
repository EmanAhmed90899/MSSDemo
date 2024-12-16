package com.hemaya.mssdemo.view.change_language;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.utils.langauge.LocaleHelper;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.home.HomeView;

public class ChangeLanguage extends BaseActivity {

    LinearLayout english, arabic;
    SharedPreferenceStorage sharedPreferenceStorage;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_change_language);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

    }

    private void init() {
        english = findViewById(R.id.englishLayout);
        arabic = findViewById(R.id.arabicLayout);
        sharedPreferenceStorage = new SharedPreferenceStorage(this);
        back = findViewById(R.id.backImg);
        onClick();
    }

    private void onClick() {
        back.setOnClickListener(v -> {
                    Intent intent = new Intent(this, HomeView.class);
                    startActivity(intent);
                }
        );
        english.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "en");
            sharedPreferenceStorage.setLanguage("en");
            this.recreate();

        });
        arabic.setOnClickListener(v -> {
            LocaleHelper.setLocale(this, "ar");
            sharedPreferenceStorage.setLanguage("ar");
            this.recreate();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
        super.attachBaseContext(LocaleHelper.getLanguageAwareContext(newBase));
    }
}