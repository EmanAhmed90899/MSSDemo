package com.hemaya.mssdemo.network.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Log the request URL
        System.out.println("Request URL: " + request.url());

        // Log the headers
        System.out.println("Request Headers: " + request.headers());

        // Log the body (if present)
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            System.out.println("Request Body: " + buffer.readUtf8());
        }

        return chain.proceed(request);
    }
}
