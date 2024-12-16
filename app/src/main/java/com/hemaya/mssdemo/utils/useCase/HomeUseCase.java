package com.hemaya.mssdemo.utils.useCase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        userDatabaseHelper.deleteUser(user.getId());
        sharedPreferenceStorage.clearData();

        Intent intent = new Intent(context, IdentificationView.class);
        intent.putExtra("isAddNewUser",true);
        context.startActivity(intent);

    }
}
