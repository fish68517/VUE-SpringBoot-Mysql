package com.archive.app;

import com.archive.app.ApiService; // 导入您的ApiService接口
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 使用您自己的服务器IP地址
    private static final String BASE_URL = "http://192.168.222.145:8080/";

    // volatile 关键字确保多线程环境下的可见性
    private static volatile ApiService apiService = null;

    /**
     * 获取 ApiService 的单例
     * @return ApiService 实例
     */
    public static ApiService getApiService() {
        // Double-Checked Locking for thread safety
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {

                    // 1. 创建 HttpLoggingInterceptor 用于打印日志
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    // 设置日志级别为 BODY，会打印所有请求和响应的详细信息
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // 2. 创建 OkHttpClient 并添加拦截器
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .build();

                    // 3. 配置 Gson，使其能够处理可能不规范的 JSON 格式
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    // 4. 创建 Retrofit 实例
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient) // 设置自定义的 OkHttpClient
                            .addConverterFactory(GsonConverterFactory.create(gson)) // 使用配置好的 Gson
                            // 关键：添加 RxJava3 调用适配器工厂
                            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                            .build();

                    // 5. 创建 ApiService 的实现，这是最关键的一步
                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }
}