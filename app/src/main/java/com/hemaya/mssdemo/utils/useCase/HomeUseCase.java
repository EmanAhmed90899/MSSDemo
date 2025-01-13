package com.hemaya.mssdemo.utils.useCase;

import android.content.Context;
import android.content.Intent;

import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.view.home.HomeView;
import com.hemaya.mssdemo.view.identification.IdentificationView;

public class HomeUseCase {
    Context context;
    UserDatabaseHelper userDatabaseHelper;
    User user;
    SharedPreferenceStorage sharedPreferenceStorage;

    public HomeUseCase(Context context) {
        this.context = context;
        userDatabaseHelper = new UserDatabaseHelper(context);
        sharedPreferenceStorage = new SharedPreferenceStorage(context);
        init();
    }
    public void init() {
        user = userDatabaseHelper.getOneUsedUser();
    }

    public User getUser() {
        user = userDatabaseHelper.getOneUsedUser();
        return user;
    }

    public void updateUserName(String name,int id) {
        user.setName(name);
        userDatabaseHelper.updateUserName(user.getName(),id+"");
    }

    public void deleteUser() {
        user = userDatabaseHelper.getOneUsedUser();
        userDatabaseHelper.deleteUser(user.getId());
        sharedPreferenceStorage.clearData();
        int id = userDatabaseHelper.selectAnotherUser();
        if (id == -1) {

            Intent intent = new Intent(context, IdentificationView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("isDeleteAll", true);
            context.startActivity(intent);

        } else {
            user = userDatabaseHelper.getUser(id);
            sharedPreferenceStorage.setUserId(id + "");
            sharedPreferenceStorage.setStorageName(user.getStorageName());

            Intent intent = new Intent(context, HomeView.class);
            context.startActivity(intent);

        }

    }
}
