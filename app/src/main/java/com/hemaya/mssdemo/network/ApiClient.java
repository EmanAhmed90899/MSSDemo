package com.hemaya.mssdemo.network;

import android.content.Context;

import com.hemaya.mssdemo.network.domain.Domain;
import com.hemaya.mssdemo.network.interceptor.DynamicHeaderInterceptor;
import com.hemaya.mssdemo.network.interceptor.ErrorHandlingInterceptor;
import com.hemaya.mssdemo.network.interceptor.LoggingInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String initialHeaderValue, Context context) {
        // Dynamic Header Interceptor
        // Create an HttpLoggingInterceptor for logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS); // Log headers and body

        // Build OkHttpClient with interceptors
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add logging interceptor
                .addInterceptor(new DynamicHeaderInterceptor(initialHeaderValue,context)) // Add dynamic header interceptor
                .addInterceptor(new ErrorHandlingInterceptor()) // Add error handling interceptor
                .build();

//        // Build Retrofit instance
//        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Domain.baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//        }

        return retrofit;
    }
}
