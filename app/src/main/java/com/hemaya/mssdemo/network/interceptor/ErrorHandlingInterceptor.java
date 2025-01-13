package com.hemaya.mssdemo.network.interceptor;

import android.util.Log;

import com.google.gson.Gson;
import com.hemaya.mssdemo.model.error.ErrorResponse;
import com.hemaya.mssdemo.model.error.validationError.ValidationResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ErrorHandlingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (!response.isSuccessful()) {
            // Log or handle specific HTTP errors
            int statusCode = response.code();
            String errorBody = response.body() != null ? response.body().string() : "No error body";
          Log.e("ErrorHandlingInterceptor", "Error: " + errorBody);
            if (statusCode == 422) {
                ValidationResponse validationResponse = new Gson().fromJson(errorBody, ValidationResponse.class);
                throw new IOException(validationResponse.getMessage());
            } else if (statusCode == 400) {
                ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);
                throw new IOException(errorResponse.getMessage());
            } else {
                throw new IOException("Failed to connect");
            }
        }

        return response;
    }
}
