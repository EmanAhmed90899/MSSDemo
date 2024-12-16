package com.hemaya.mssdemo.presenter.details;

import android.content.Context;

import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.view.details.UserDetailsInterface;

public class DetailsPresenter implements DetailsPresenterInterface {

    UserDetailsInterface userDetailsInterface;
    UserDatabaseHelper userDatabaseHelper;
    SharedPreferenceStorage sharedPreferenceStorage;

    public DetailsPresenter(UserDetailsInterface userDetailsInterface, Context context) {
        this.userDetailsInterface = userDetailsInterface;
        userDatabaseHelper = new UserDatabaseHelper(context);
        sharedPreferenceStorage = new SharedPreferenceStorage(context);
    }

    @Override
    public void getUser() {
       userDetailsInterface.showUserDetails(sharedPreferenceStorage.getTimeShift(),userDatabaseHelper.getOneUsedUser());
    }
}
