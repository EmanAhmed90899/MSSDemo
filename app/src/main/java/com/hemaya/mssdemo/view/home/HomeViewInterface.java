package com.hemaya.mssdemo.view.home;

import com.hemaya.mssdemo.model.UserModel.User;

public interface HomeViewInterface {
    void setData(User user);
    void setMultipleUsers();
    void goToActivation();
    void UserView(boolean isUserView);
}
