package com.hemaya.mssdemo.network;

import androidx.annotation.NonNull;

import com.hemaya.mssdemo.network.domain.AuthData;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {
    private final String credentials;

    public BasicAuthInterceptor() {
        this.credentials = Credentials.basic(AuthData.userName, "");
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials)
                .build();
        return chain.proceed(authenticatedRequest);
    }
}