package com.hemaya.mssdemo.presenter.home;

import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;

import java.util.List;

public interface HomePresenterInterface {
    void getUser();
    List<User> getUsers();
    void setUser(User user);
    void addToken();
    void showUserRename(HomeUseCase homeUseCase, UserViewModel userViewModel);
    void resetToken();
    void deleteToken(HomeUseCase homeUseCase);
    void recreate();
}
