package com.hemaya.mssdemo.view.change_pin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.presenter.change_pin.ChangePinPresenter;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.useCase.ChangePinUseCase;
import com.hemaya.mssdemo.view.home.HomeView;

public class ChangePinView extends AppCompatActivity implements ChangePinViewInterface, ChangePinUseCase.ChangePinUseCaseInterface {
    private EditText oldPin, newPin, newRepeatedPin;
    private String oldPinStr, newPinStr, newRepeatedPinStr;
    private Button changePinBtn;
    private TextView errorPin;
    private ChangePinPresenter presenter;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pin_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        oldPin = findViewById(R.id.et_old_pin);
        newPin = findViewById(R.id.et_new_pin);
        newRepeatedPin = findViewById(R.id.et_new_pin_repeat);
        changePinBtn = findViewById(R.id.changePin);
        backImg = findViewById(R.id.backImg);
        presenter = new ChangePinPresenter(this, this, new ChangePinUseCase(this, new ViewModelProvider(this).get(UserViewModel.class), this));
        errorPin = findViewById(R.id.errorPin);
        if (new SharedPreferenceStorage(this).getLanguage().equals("ar")) {
            oldPin.setGravity(Gravity.END);
            newPin.setGravity(Gravity.END);
            newRepeatedPin.setGravity(Gravity.END);
        }
        onClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClick() {
        backImg.setOnClickListener(v -> finish());

        changePinBtn.setOnClickListener(v -> {
            oldPinStr = oldPin.getText().toString();
            newPinStr = newPin.getText().toString();
            newRepeatedPinStr = newRepeatedPin.getText().toString();
            presenter.changePin(oldPinStr, newPinStr, newRepeatedPinStr);
        });
    }

    @Override
    public void pinResult() {
        Intent intent = new Intent(this, HomeView.class);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        errorPin.setText(message);
    }


    @Override
    public void hideProgress() {

    }

    @Override
    public void showProgress() {

    }


    @Override
    public void showErrorMessage(String message) {
        errorPin.setText(message);
    }
}