package com.hemaya.mssdemo.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hemaya.mssdemo.R;
import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.presenter.home.HomePresenter;
import com.hemaya.mssdemo.presenter.home.HomePresenterInterface;
import com.hemaya.mssdemo.view.home.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

public class UsersBottomSheet extends BottomSheetDialogFragment implements UsersAdapter.OnUserClickListener {
    Button confirmBtn;
    RecyclerView usersRecyclerView;
    UsersAdapter usersAdapter;
    HomePresenterInterface homePresenter;
    UserViewModel userViewModel;
    public UsersBottomSheet(HomePresenterInterface homePresenter,UserViewModel userViewModel) {
        this.homePresenter = homePresenter;
        this.userViewModel = userViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bottom_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

    }

    private void init() {
        confirmBtn = getView().findViewById(R.id.confirmBtn);
        usersRecyclerView = getView().findViewById(R.id.usersRecyclerView);

        assign();
    }

    private void assign() {

        usersAdapter = new UsersAdapter(homePresenter.getUsers(), UsersBottomSheet.this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        usersRecyclerView.setAdapter(usersAdapter);

        onClick();
    }

    private void onClick() {
        confirmBtn.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.Theme_MyApp_BottomSheet;

    }

    @Override
    public void onUserClick(User user) {
        userViewModel.setUser(user);
        homePresenter.setUser(user);
        dismiss();
    }
}