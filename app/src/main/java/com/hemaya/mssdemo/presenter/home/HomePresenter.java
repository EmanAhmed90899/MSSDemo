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
import com.hemaya.mssdemo.utils.storage.SaveInLocalStorage;
import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;
import com.hemaya.mssdemo.utils.storage.UserDatabaseHelper;
import com.hemaya.mssdemo.utils.useCase.HomeUseCase;
import com.hemaya.mssdemo.utils.views.DeleteUserDialog;
import com.hemaya.mssdemo.utils.views.DialogRename;
import com.hemaya.mssdemo.view.home.HomeView;
import com.hemaya.mssdemo.view.home.HomeViewInterface;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter implements HomePresenterInterface {
    private HomeViewInterface view;
    private Context context;
    private UserDatabaseHelper userDatabaseHelper;
    private SharedPreferenceStorage sharedPreferenceStorage;
    private ApiService apiService;

    public HomePresenter(HomeViewInterface view) {
        this.view = view;
        this.context = (HomeView) view;
        this.userDatabaseHelper = new UserDatabaseHelper((HomeView) view);
        this.sharedPreferenceStorage = new SharedPreferenceStorage(context);
        apiService = ApiClient.getClient().create(ApiService.class);

    }

    @Override
    public void getUser() {
        view.setData(userDatabaseHelper.getOneUsedUser());
        if (userDatabaseHelper.usersCount() > 1) {
            view.setMultipleUsers();
        }
        Log.e("** name", "SecureStorage" + userDatabaseHelper.getAllUsers().toString());
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
        sharedPreferenceStorage.setPlatformFingerPrint(selectedUser.getPlatformFingerPrint());
        sharedPreferenceStorage.setStorageName(selectedUser.getStorageName());
        SaveInLocalStorage saveInLocalStorage = new SaveInLocalStorage(context, user.getStorageName(), "");
        Log.e("** name", user.getStorageName());
        Log.e("** name", Arrays.toString(saveInLocalStorage.getByteData("staticVector")));


        getUser();
    }

    @Override
    public void addToken() {
        userDatabaseHelper.updateIsUsed(sharedPreferenceStorage.getUserId(), false);
        sharedPreferenceStorage.setUserId(null);
        sharedPreferenceStorage.setPlatformFingerPrint(null);
        sharedPreferenceStorage.setStorageName(null);
        view.goToActivation();
    }

    @Override
    public void showUserRename(HomeUseCase homeUseCase, UserViewModel userViewModel) {
        new DialogRename(context, homeUseCase, userViewModel);

    }

    @Override
    public void resetToken() {
        Call<SynchronizeResponse> callSynchronize = apiService.synchronize();
        callSynchronize.enqueue(new Callback<SynchronizeResponse>() {
            @Override
            public void onResponse(Call<SynchronizeResponse> call, Response<SynchronizeResponse> response) {
                if (response.isSuccessful()) {
                    SynchronizeResponse synchronizeResponse = response.body();
                    if (synchronizeResponse != null) {
                        if (synchronizeResponse.getResultCodes().getStatusCode() == 0) {
                            Log.e("** serverTimeShift", synchronizeResponse.getResult().getServerTime() + "");
                            long currentTimeMillis = System.currentTimeMillis();
                            convertEpochToDate(currentTimeMillis, synchronizeResponse.getResult().getServerTime());

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

    public void convertEpochToDate(long currentTimeMillis, long epochSeconds) {
        // Convert seconds to milliseconds
        long timestampMillis = epochSeconds * 1000;

        // Convert to Date object
        Date date = new Date(timestampMillis);

        // Format the Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone if needed
        String formattedDate = sdf.format(date);

        // Display the formatted date
        System.out.println("Formatted Date: " + formattedDate);


        // Calculate the time shift (difference)
        long timeShiftMillis = currentTimeMillis - timestampMillis;
        sharedPreferenceStorage.setTimeShift(timeShiftMillis);
    }

}
