package com.archive.app;



import com.archive.app.dto.LoginRequest;
import com.archive.app.dto.ScanRequest;
import com.archive.app.dto.ScanResponse;
import com.archive.app.dto.TransactionLogRequest;
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.archive.app.model.Users;

import java.util.List; // Import List

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query; // Import Query

public interface ApiService {

    // --- User Authentication ---
    @POST("users/login")
    Call<Users> login(@Body LoginRequest loginRequest);

    // --- Inventory & Product Info ---
    @GET("inventory/batch/{batchCode}")
    Call<ScanResponse> getInventoryByBatchCode(@Path("batchCode") String batchCode); // Assuming ScanResponse includes Inventory and Product

    // Endpoint to search inventory by SKU (returns a list as multiple batches might exist)
    @GET("inventory/sku/{sku}")
    Call<List<Inventory>> getInventoryByProductSku(@Path("sku") String sku); // Returns potentially multiple batches

    @GET("products/{id}")
    Call<Products> getProductById(@Path("id") int productId); // To get product details if needed separately


    // --- Scanning Operations (Assuming backend handles inventory update + logging) ---
    @POST("scan/inbound")
    Call<Void> scanInbound(@Body ScanRequest scanRequest); // Returns Void or a simple success/error response

    @POST("scan/outbound")
    Call<Void> scanOutbound(@Body ScanRequest scanRequest); // Returns Void or a simple success/error response


    // --- (Alternative) Manual Log Creation (Less Recommended for Scan Operations) ---
    @POST("transaction-logs")
    Call<Boolean> createTransactionLog(@Body TransactionLogRequest logRequest);

    // --- Get User Details (Example) ---
    @GET("users/{id}")
    Call<Users> getUserById(@Path("id") int userId);

}
