package com.archive.app;

import com.archive.app.dto.LoginRequest;
import com.archive.app.dto.ScanRequest;
import com.archive.app.dto.ScanResponse;
import com.archive.app.dto.TransactionLogRequest;
import com.archive.app.model.Departments;
import com.archive.app.model.InboundOrders;
import com.archive.app.model.Inventory;
import com.archive.app.model.Products;
import com.archive.app.model.Roles;
import com.archive.app.model.TransactionLogs;
import com.archive.app.model.Users;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit API Service 接口
 * 定义了所有与后端服务器通信的端点
 */
public interface ApiService {

    //=========================== 1. 部门 (Departments) ===========================
    @POST("departments")
    Call<Departments> createDepartment(@Body Departments department);

    @GET("departments/{id}")
    Call<Departments> getDepartmentById(@Path("id") int id);

    @GET("departments/list")
    Call<List<Departments>> listDepartments();

    @PUT("departments")
    Call<Departments> updateDepartment(@Body Departments department);

    @DELETE("departments/{id}")
    Call<Void> deleteDepartment(@Path("id") int id);


    //=========================== 2. 角色 (Roles) ===========================
    @POST("roles")
    Call<Roles> createRole(@Body Roles role);

    @GET("roles/{id}")
    Call<Roles> getRoleById(@Path("id") int id);

    @GET("roles/list")
    Call<List<Roles>> listRoles();

    @PUT("roles")
    Call<Roles> updateRole(@Body Roles role);

    @DELETE("roles/{id}")
    Call<Void> deleteRole(@Path("id") int id);


    //=========================== 3. 用户 (Users) ===========================
    @POST("users/login")
    Call<Users> login(@Body LoginRequest loginRequest);

    @POST("users")
    Call<Boolean> createUser(@Body Users user);

    @GET("users/{id}")
    Call<Users> getUserById(@Path("id") int id);

    @GET("users/list")
    Call<List<Users>> listUsers();

    @PUT("users")
    Call<Users> updateUser(@Body Users user);

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int id);


    //=========================== 4. 产品 (Products) ===========================
    @POST("products")
    Call<Products> createProduct(@Body Products product);

    @GET("products/{id}")
    Call<Products> getProductById(@Path("id") int id);

    @GET("products/list")
    Call<List<Products>> listProducts();

    @PUT("products")
    Call<Products> updateProduct(@Body Products product);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") int id);


    //=========================== 5. 入库单 (Inbound Orders) ===========================
    @POST("inbound-orders")
    Call<InboundOrders> createInboundOrder(@Body InboundOrders inboundOrder);

    @GET("inbound-orders/{id}")
    Call<InboundOrders> getInboundOrderById(@Path("id") int id);

    //=========================== 5. 入库单 (Inbound Orders) ===========================
    @GET("inbound-orders/list")
    Single<List<InboundOrders>> listInboundOrders(); // 返回值改为 Single

    @PUT("inbound-orders")
    Call<InboundOrders> updateInboundOrder(@Body InboundOrders inboundOrder);

    @DELETE("inbound-orders/{id}")
    Call<Void> deleteInboundOrder(@Path("id") int id);

    /**
     * 部分更新入库单信息 (例如只更新状态)
     * @param id 要更新的入库单ID
     * @param data 包含要更新字段的 Map, 例如 {"status": "已完成"}
     */
    @PATCH("inbound-orders/{id}")
    Call<Void> updateInboundOrderStatus(@Path("id") int id, @Body Map<String, Object> data);


    //=========================== 6. 库存 (Inventory) ===========================
    @POST("inventory")
    Call<Inventory> createInventory(@Body Inventory inventory);

    @GET("inventory/{id}")
    Call<Inventory> getInventoryById(@Path("id") int id);

    @GET("inventory/list")
    Call<List<Inventory>> listInventory();

    @PUT("inventory")
    Call<Inventory> updateInventory(@Body Inventory inventory);

    @DELETE("inventory/{id}")
    Call<Void> deleteInventory(@Path("id") int id);

    // --- 特定查询接口 (已存在) ---
    @GET("inventory/batch/{batchCode}")
    Call<ScanResponse> getInventoryByBatchCode(@Path("batchCode") String batchCode);

    @GET("inventory/sku/{sku}")
    Call<List<Inventory>> getInventoryByProductSku(@Path("sku") String sku);



    //=========================== 6. 库存 (Inventory) ===========================
    /**
     * 根据您提供的接口名进行修改
     * 根据入库单ID查询其下所有的库存批次
     * @param inboundId 入库单的ID
     * @return 属于该入库单的库存批次列表
     */
    @GET("inventory/inboundId/{inboundId}")
    Single<List<Inventory>> getInventoryByInboundId(@Path("inboundId") int inboundId); // 返回值改为 Single

    //=========================== 7. 异动日志 (Transaction Logs) ===========================
    // 通常日志是只增不改不删的，但根据您的要求提供完整接口
    @POST("transaction-logs")
    Call<TransactionLogs> createTransactionLog(@Body TransactionLogRequest logRequest);

    @GET("transaction-logs/{id}")
    Call<TransactionLogs> getTransactionLogById(@Path("id") long id);

    @GET("transaction-logs/list")
    Call<List<TransactionLogs>> listTransactionLogs();

    @GET("transaction-logs/inventory/{inventoryId}")
    Call<List<TransactionLogs>> listLogsByInventoryId(@Path("inventoryId") int inventoryId);

    // 注意: 更新和删除日志在实际业务中非常罕见，可能需要严格的权限控制
    @PUT("transaction-logs")
    Call<TransactionLogs> updateTransactionLog(@Body TransactionLogs transactionLog);

    @DELETE("transaction-logs/{id}")
    Call<Void> deleteTransactionLog(@Path("id") long id);


    /**
     * 根据入库单ID查询其下所有的库存批次
     * @param orderId 入库单的ID
     * @return 属于该入库单的库存批次列表
     */
    @GET("inventory/by-order/{orderId}") // 假设后端提供此接口
    Call<List<Inventory>> listInventoryByOrderId(@Path("orderId") int orderId);


    //=========================== 8. 扫码操作 (Scan Operations) ===========================
    // 这些是复合操作，后端会处理库存更新和日志记录
    @POST("scan/inbound")
    Call<Void> scanInbound(@Body ScanRequest scanRequest);

    @POST("scan/outbound")
    Call<Void> scanOutbound(@Body ScanRequest scanRequest);

}