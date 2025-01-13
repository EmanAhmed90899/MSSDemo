package com.hemaya.mssdemo.presenter.home;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hemaya.mssdemo.model.UserModel.User;
import com.hemaya.mssdemo.model.UserModel.UserViewModel;
import com.hemaya.mssdemo.model.synchronizeModel.SynchronizeResponse;
import com.hemaya.mssdemo.network.ApiClient;
import com.hemaya.mssdemo.network.ApiService;
import com.hemaya.mssdemo.network.domain.AuthData;
import com.hemaya.mssdemo.network.interceptor.DynamicHeaderInterceptor;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;
import com.hemaya.mssdemo.utils.views.DeleteUserDialog;
import com.hemaya.mssdemo.utils.views.DialogRename;
import com.hemaya.mssdemo.view.home.HomeView;
import com.hemaya.mssdemo.view.home.HomeViewInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenter implements HomePresenterInterface {
    private HomeViewInterface view;
    private Context context;
    private UserDatabaseHelper userDatabaseHelper;
    private SharedPreferenceStorage sharedPreferenceStorage;

    public HomePresenter(HomeViewInterface view) {
        this.view = view;
        this.context = (HomeView) view;
        this.userDatabaseHelper = new UserDatabaseHelper((HomeView) view);
        this.sharedPreferenceStorage = new SharedPreferenceStorage(context);

    }

    @Override
    public void getUser() {
        view.setData(userDatabaseHelper.getOneUsedUser());
        if (userDatabaseHelper.usersCount() > 1) {
            view.setMultipleUsers();
        }
    }

    @Override
    public List<User> getUsers() {
        return userDatabaseHelper.getAllUsers();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void setUser(User user) {
        User selectedUser = userDatabaseHelper.updateIsUsed(user.getId() + "", true);
        sharedPreferenceStorage.setUserId(selectedUser.getId() + "");
        sharedPreferenceStorage.setStorageName(selectedUser.getStorageName());
        getUser();
    }

    @Override
    public void addToken() {
        view.goToActivation();
    }

    @Override
    public void showUserRename(HomeUseCase homeUseCase, UserViewModel userViewModel) {
        new DialogRename(context, homeUseCase, userViewModel);

    }

    @Override
    public void resetToken() {
        Call<SynchronizeResponse> callSynchronize = getRetrofit(AuthData.generateHmac("[]")).synchronize();
        callSynchronize.enqueue(new Callback<SynchronizeResponse>() {
            @Override
            public void onResponse(Call<SynchronizeResponse> call, Response<SynchronizeResponse> response) {
                if (response.isSuccessful()) {
                    SynchronizeResponse synchronizeResponse = response.body();
                    if (synchronizeResponse != null) {
                        if (synchronizeResponse.getResultCodes().getStatusCode() == 0) {
                            convertEpochToDate(synchronizeResponse.getResult().getServerTime());

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SynchronizeResponse> call, Throwable t) {
                Log.e("** error", t.getMessage());
            }
        });
    }

    @Override
    public void deleteToken(HomeUseCase homeUseCase) {
        new DeleteUserDialog(context, homeUseCase);
    }

    @Override
    public void recreate() {
        view.restart();
    }

    public void convertEpochToDate(long epochSeconds) {
        long currentEpochMillis = System.currentTimeMillis(); // Current time in milliseconds
        long currentEpochSeconds = currentEpochMillis / 1000; // Convert to seconds if needed

        // Calculate the time shift (difference)
        long timeShiftMillis = currentEpochSeconds - epochSeconds;
        sharedPreferenceStorage.setTimeShift(timeShiftMillis);
    }

    ApiService getRetrofit(String header) {
        DynamicHeaderInterceptor headerInterceptor = new DynamicHeaderInterceptor(header,context);

        Retrofit retrofit = ApiClient.getClient(header, context);
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }
}
