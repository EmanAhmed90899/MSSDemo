package com.hemaya.mssdemo.view.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.presenter.home.HomePresenter;
import com.hemaya.mssdemo.presenter.home.HomePresenterInterface;
import com.hemaya.mssdemo.utils.langauge.LocaleHelper;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.useCase.ActivationUseCase;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;
import com.hemaya.mssdemo.utils.views.ShowProgressBar;
import com.hemaya.mssdemo.view.BaseActivity;
import com.hemaya.mssdemo.view.identification.IdentificationView;
import com.hemaya.mssdemo.view.otp.generateOtpView;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDK;
import com.vasco.digipass.sdk.utils.biometricsensor.BiometricSensorSDKException;

public class HomeView extends BaseActivity implements HomeViewInterface {
    ImageView showAllUsers, menuIcon;
    LinearLayout otpLayout, multipleUserLayout;
    private ShowProgressBar showProgressBar;
    private TextView userName, userNameMulti, serialNumber;
    private HomePresenterInterface homePresenter;
    private User user;
    HomeUseCase homeUseCase;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        init();

    }

    private void init() {
        showAllUsers = findViewById(R.id.arrow_down);
        otpLayout = findViewById(R.id.otpLayout);
        menuIcon = findViewById(R.id.menu_logo);
        showProgressBar = new ShowProgressBar(this);
        userName = findViewById(R.id.userName);
        userNameMulti = findViewById(R.id.userNameMulti);
        serialNumber = findViewById(R.id.serialNumber);
        multipleUserLayout = findViewById(R.id.multipleUserLayout);
        homePresenter = new HomePresenter(this);
        homeUseCase = new HomeUseCase(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        assign();
        onClick();
    }

    private void assign() {
        homePresenter.getUser();

    }

    private void onClick() {
        showAllUsers.setOnClickListener(v -> {
            UsersBottomSheet usersBottomSheet = new UsersBottomSheet(homePresenter, userViewModel);

            usersBottomSheet.show(getSupportFragmentManager(), "usersBottomSheet");
        });
        otpLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeView.this, generateOtpView.class);
            startActivity(intent);
        });

        menuIcon.setOnClickListener(v -> {
            MenuBottomSheet menuBottomSheet = new MenuBottomSheet(homePresenter, homeUseCase, userViewModel);
            menuBottomSheet.show(getSupportFragmentManager(), "menuBottomSheet");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setData(User user) {
        this.user = user;
        userViewModel.setUser(user);
        userViewModel.getUser().observe(this, userModel -> {
            if (userModel != null) {
                userName.setText(userModel.getName());
            }
        });

    }



    @Override
    public void setMultipleUsers() {
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                multipleUserLayout.setVisibility(LinearLayout.VISIBLE);
                userNameMulti.setText(user.getName());
                serialNumber.setText(user.getSerialNumber());
            }
        });

    }

    @Override
    public void goToActivation() {
        Intent intent = new Intent(HomeView.this, IdentificationView.class);
        intent.putExtra("isAddNewUser", true);
        startActivity(intent);
    }

    @Override
    public void UserView(boolean isUserView) {
        if (isUserView) {
            userName.setVisibility(LinearLayout.GONE);
            userNameMulti.setText("User ID");
            serialNumber.setVisibility(LinearLayout.GONE);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}