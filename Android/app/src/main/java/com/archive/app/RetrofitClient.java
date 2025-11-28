package com.archive.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // IMPORTANT: Replace with your actual backend server IP address or domain
    private static final String BASE_URL = "http://192.168.2.185:8080/"; // EXAMPLE IP - CHANGE THIS

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Add logging interceptor for debugging network requests/responses
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response lines and their respective headers and bodies (if present).

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
                    .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                    .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
                    .build();

            // Configure Gson to handle potential date formats if necessary
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss") // Adjust date format if needed
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Set the custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
