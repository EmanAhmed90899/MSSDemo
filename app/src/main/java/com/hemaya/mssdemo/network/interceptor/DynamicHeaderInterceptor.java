package com.hemaya.mssdemo.network.interceptor;

import android.content.Context;

import com.hemaya.mssdemo.utils.storage.SharedPreferenceStorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DynamicHeaderInterceptor implements Interceptor {
    private String headerValue;
    private SharedPreferenceStorage sharedPreferenceStorage;
    ;

    public DynamicHeaderInterceptor(String initialHeaderValue, Context context) {
        this.headerValue = initialHeaderValue;
        this.sharedPreferenceStorage = new SharedPreferenceStorage(context);
    }

    public void updateHeaderValue(String newHeaderValue) {
        this.headerValue = newHeaderValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (headerValue != null) {
            builder.header("X-Signature", headerValue);
            builder.header("X-Locale", sharedPreferenceStorage.getLanguage());
        }

        Request modifiedRequest = builder.build();
        return chain.proceed(modifiedRequest);
    }
}
