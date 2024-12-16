package com.hemaya.mssdemo.network;

import com.hemaya.mssdemo.network.domain.Domain;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor())
            .build();
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Domain.domain).client(UnsafeOkHttpClient.getUnsafeOkHttpClient()) // Base URL
                    .addConverterFactory(GsonConverterFactory.create()) // Convert JSON response to Java objects
                    .build();
        }
        return retrofit;
    }
}
