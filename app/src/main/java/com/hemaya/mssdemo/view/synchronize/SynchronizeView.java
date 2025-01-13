package com.hemaya.mssdemo.view.synchronize;

import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.presenter.synchronize.SynchronizePresenter;
import com.hemaya.mssdemo.presenter.synchronize.SynchronizePresenterInterface;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.views.GenericPopUp;
import com.hemaya.mssdemo.view.BaseActivity;

public class SynchronizeView extends BaseActivity implements SynchronizeViewInterface {

    ImageView syncImg, backImg;
    TextView timeShift;
    SynchronizePresenterInterface presenter;
    FrameLayout loading_view;
    SharedPreferenceStorage sharedPreferenceStorage;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_synchronize_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        backImg = findViewById(R.id.backImg);
        syncImg = findViewById(R.id.syncImg);
        timeShift = findViewById(R.id.timeShift);
        loading_view = findViewById(R.id.loading_view);
        progressBar = findViewById(R.id.progressBar);
        presenter = new SynchronizePresenter(this);
        sharedPreferenceStorage = new SharedPreferenceStorage(this);

        assign();
        onClick();
    }

    private void assign() {
        timeShift.setText(sharedPreferenceStorage.getTimeShift() == -1 ? "--" : sharedPreferenceStorage.getTimeShift() + "");
    }

    private void onClick() {
        backImg.setOnClickListener(v -> {
            finish();
        });

        syncImg.setOnClickListener(v -> {
            showProgress();
            presenter.synchronize();
        });
    }


    @Override
    public void showProgress() {
        syncImg.setVisibility(android.view.View.GONE);
        progressBar.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        syncImg.setVisibility(android.view.View.VISIBLE);
        progressBar.setVisibility(android.view.View.GONE);
    }

    @Override
    public void showSuccess(String message) {
        timeShift.setText(message);
    }

    @Override
    public void showError(String message) {
        new GenericPopUp(this, message).showCustomPopup();
    }
}