package com.hemaya.mssdemo.view.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.presenter.home.HomePresenterInterface;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;
import com.hemaya.mssdemo.view.about.AboutView;
import com.hemaya.mssdemo.view.change_language.ChangeLanguage;
import com.hemaya.mssdemo.view.change_pin.ChangePinView;
import com.hemaya.mssdemo.view.details.UserDetails;
import com.hemaya.mssdemo.view.faqs.FAQSView;

public class MenuBottomSheet extends BottomSheetDialogFragment {

    private HomePresenterInterface homePresenter;
    private LinearLayout faqLayout,aboutLayout,deleteLayout,resetTokenLayout,changePinLayout,addTokenLayout, userDetailsLayout, languageLayout,renameUserLayout;
    private Switch biometricSwitch;
    private HomeUseCase homeUseCase;
    private UserViewModel userViewModel;

    public MenuBottomSheet(HomePresenterInterface homePresenter, HomeUseCase homeUseCase, UserViewModel userViewModel) {
        this.homePresenter = homePresenter;
        this.homeUseCase = homeUseCase;
        this.userViewModel = userViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_layout, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        biometricSwitch = getView().findViewById(R.id.biometricSwitch);
        addTokenLayout = getView().findViewById(R.id.addTokenLayout);
        userDetailsLayout = getView().findViewById(R.id.userDetailsLayout);
        languageLayout = getView().findViewById(R.id.languageLayout);
        renameUserLayout = getView().findViewById(R.id.renameUserLayout);
        changePinLayout = getView().findViewById(R.id.changePinLayout);
        resetTokenLayout = getView().findViewById(R.id.resetTokenLayout);
       deleteLayout = getView().findViewById(R.id.deleteUserLayout);
        aboutLayout = getView().findViewById(R.id.aboutLayout);
        faqLayout = getView().findViewById(R.id.faqLayout);
        assign();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void assign() {

        onClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onClick() {
        userDetailsLayout.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getContext(), UserDetails.class);
            getContext().startActivity(intent);
        });
        addTokenLayout.setOnClickListener(v -> {
            dismiss();
            homePresenter.addToken();
        });
        biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dismiss();
            if (isChecked) {

            } else {
                // The switch is disabled
            }
        });

        changePinLayout.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getContext(), ChangePinView.class);
            getContext().startActivity(intent);
        });
        languageLayout.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getContext(), ChangeLanguage.class);
            getContext().startActivity(intent);
        });

        renameUserLayout.setOnClickListener(v -> {
            dismiss();
            homePresenter.showUserRename(homeUseCase, userViewModel);
        });

        resetTokenLayout.setOnClickListener(v -> {
            dismiss();
            homePresenter.resetToken();
        });

        deleteLayout.setOnClickListener(v -> {
           homePresenter.deleteToken(homeUseCase);
        });

        aboutLayout.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getContext(), AboutView.class);
            getContext().startActivity(intent);
        });

        faqLayout.setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(getContext(), FAQSView.class);
            getContext().startActivity(intent);
        });
    }


    @Override
    public int getTheme() {
        return R.style.Theme_MyApp_BottomSheet;

    }

}