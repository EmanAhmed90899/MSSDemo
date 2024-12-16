package com.hemaya.mssdemo.view.otp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.presenter.otp.OtpPresenter;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.identification.CountTimer;

public class generateOtpView extends BaseActivity implements OtpViewInterface, CountTimer.TimerListener {

    LinearLayout dynamicContainer, enter_pin_layout, copyLayout;
    StringBuilder pinBuilder;
    RelativeLayout show_otp;
    OtpPresenter otpPresenter;
    TextView otp_text,counterTxt;
    Button regenerateOtp;
    ImageView backImg,homeImg;
    CountTimer countTimer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_otp_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        homeImg = findViewById(R.id.homeImg);
        backImg = findViewById(R.id.backImg);
        dynamicContainer = findViewById(R.id.dynamic_container);
        pinBuilder = new StringBuilder();
        enter_pin_layout = findViewById(R.id.enter_pin_layout);
        show_otp = findViewById(R.id.show_otp);
        otp_text = findViewById(R.id.otpText);
        copyLayout = findViewById(R.id.copyLayout);
        counterTxt = findViewById(R.id.counterTxt);
        otpPresenter = new OtpPresenter(this);
        regenerateOtp = findViewById(R.id.regenerateOtp);
        assign();
        onClick();
    }

    private void assign() {
        for (int i = 0; i < 6; i++) {
            View viewCircle = new View(this);
            ViewGroup.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(35, 35);
            viewCircle.setLayoutParams(layoutParams);
            viewCircle.setBackgroundResource(R.drawable.circle_background_gray);
            layoutParams.setMargins(5, 0, 5, 0);  // 50px left & right, 30px top & bottom
            dynamicContainer.addView(viewCircle);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClick() {
        backImg.setOnClickListener(v -> finish());
        homeImg.setOnClickListener(v -> finish());

        copyLayout.setOnClickListener(v -> {
            otpPresenter.copyOtp(otp_text.getText().toString());
        });

        regenerateOtp.setOnClickListener(v -> {
            otpPresenter.generateOtp(pinBuilder.toString());

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onNumberClick(View view) {
        if (pinBuilder.length() <= 6) {
            View viewCircle = new View(this);
            pinBuilder.append(((TextView) view).getText().toString());
            ViewGroup.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(35, 35);
            viewCircle.setLayoutParams(layoutParams);
            viewCircle.setBackgroundResource(R.drawable.circle_background_app_color);
            layoutParams.setMargins(10, 0, 10, 0);  // 50px left & right, 30px top & bottom
            dynamicContainer.removeViewAt(pinBuilder.length() - 1);
            dynamicContainer.addView(viewCircle, pinBuilder.length() - 1);

            if (pinBuilder.length() >= 6) {
                otpPresenter.generateOtp(pinBuilder.toString());
            }

        }
    }

    @Override
    public void onOtpGenerated(String otp) {
        if (otp.equals("Error")) {
            for (int i = 0; i < 6; i++) {
                View viewCircle = new View(this);
                ViewGroup.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(35, 35);
                viewCircle.setLayoutParams(layoutParams);
                viewCircle.setBackgroundResource(R.drawable.circle_background_gray);
                layoutParams.setMargins(5, 0, 5, 0);  // 50px left & right, 30px top & bottom
                dynamicContainer.removeViewAt(i);
                dynamicContainer.addView(viewCircle, i);
            }
            pinBuilder = new StringBuilder();
        } else {
            enter_pin_layout.setVisibility(View.GONE);
            show_otp.setVisibility(View.VISIBLE);
            otp_text.setText(otp);
            countTimer = new CountTimer(counterTxt,  this);
            countTimer.startOTPTimer();
        }
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSetOtp() {

    }

    @Override
    public void onTimerFinish() {

    }
}