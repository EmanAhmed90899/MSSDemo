package com.hemaya.mssdemo.view.details;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.presenter.details.DetailsPresenter;
import com.hemaya.mssdemo.presenter.details.DetailsPresenterInterface;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.view.BaseActivity;

public class UserDetails extends BaseActivity implements UserDetailsInterface {

    TextView serialNumber, timeShiftTxt, userName;
    DetailsPresenterInterface  detailsPresenterInterface;
    ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    private void init() {
        serialNumber = findViewById(R.id.serialNo);
        timeShiftTxt = findViewById(R.id.timeShift);
        userName = findViewById(R.id.alias);
        backImg = findViewById(R.id.backImg);
        detailsPresenterInterface = new DetailsPresenter(this, this);
        detailsPresenterInterface.getUser();
        onClick();
    }
    private void onClick() {
        backImg.setOnClickListener(v -> finish());
    }

    @Override
    public void showUserDetails(long timeShift,User user) {
        serialNumber.setText(user.getSerialNumber());
        timeShiftTxt.setText(String.valueOf(timeShift));
        userName.setText(user.getName().isEmpty() ? user.getSerialNumber() : user.getName());
    }
}