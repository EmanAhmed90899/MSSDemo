package com.hemaya.mssdemo.model.UserModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {

    // MutableLiveData to hold the User object
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    // Getter to expose LiveData (read-only)
    public LiveData<User> getUser() {
        return userLiveData;
    }

    // Method to set or update the User data
    public void setUser(User user) {

        userLiveData.setValue(user);  // For main thread updates
    }

    // Update user asynchronously (useful in background threads)
    public void updateUserAsync(User user) {
        userLiveData.postValue(user);  // For background thread updates
    }
}
