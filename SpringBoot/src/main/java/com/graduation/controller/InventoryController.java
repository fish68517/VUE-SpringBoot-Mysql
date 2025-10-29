package com.graduation.controller;

import com.graduation.dto.ScanResponse;
import com.graduation.entity.Inventory;
import com.graduation.entity.Products;
import com.graduation.repository.InventoryRepository;
import com.graduation.repository.ProductsRepository;
import com.graduation.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.graduation.common.BaseController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 库存及批次表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController<InventoryService, Inventory> {

    @Autowired
    private InventoryRepository inventoryRepository;


    // 为了组合数据，我们直接注入 Repository 会更方便

    @Autowired
    private ProductsRepository productsRepository;



    /**
     * 根据入库单ID查询其下所有的库存批次。
     *
     * 这个接口与您在 Android ApiService 中定义的
     * @GET("inventory/inboundId/{inboundId}")
     * 完全匹配。
     *
     * @param inboundId URL路径中的入库单ID
     * @return 返回一个包含批次详情的列表和 HTTP 状态码 200 OK
     */
    @GetMapping("/inboundId/{inboundId}")
    public ResponseEntity<List<Inventory>> getInventoryByInboundId(@PathVariable("inboundId") Integer inboundId) {
        // 调用 service 层的方法来执行业务逻辑
        List<Inventory> inventoryList = inventoryRepository.findByInboundOrderId(inboundId);;

        // 使用 ResponseEntity.ok() 来包装返回的数据，它会自动设置 HTTP 200 状态码
        return ResponseEntity.ok(inventoryList);
    }


    /**
     * 根据批次号查询库存及关联的产品信息。
     * 对应安卓接口: @GET("inventory/batch/{batchCode}") Call<ScanResponse> getInventoryByBatchCode(...)
     *
     * @param batchCode 批次唯一编码
     * @return 如果找到，返回包含 Inventory 和 Product 的 ScanResponse；否则返回 404 Not Found。
     */
    @GetMapping("/batch/{batchCode}")
    public ResponseEntity<ScanResponse> getInventoryByBatchCode(@PathVariable String batchCode) {
        // 1. 根据 batchCode 查询库存
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBatchCode(batchCode);

        // 2. 如果库存不存在，直接返回 404
        if (inventoryOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Inventory inventory = inventoryOpt.get();

        // 3. 根据库存中的 productId 查询关联的产品信息
        Optional<Products> productOpt = productsRepository.findById(inventory.getProductId());

        // 4. 如果产品不存在（数据不一致的情况），可以返回库存信息但产品为 null，或返回错误
        if (productOpt.isEmpty()) {
            // 或者可以返回一个自定义的错误响应
            return ResponseEntity.status(500).body(null); // 表示服务器内部数据错误
        }
        Products product = productOpt.get();

        // 5. 组合成 ScanResponse 并返回
        ScanResponse scanResponse = new ScanResponse(inventory, product);
        return ResponseEntity.ok(scanResponse);
    }

    /**
     * 根据产品 SKU 查询所有相关的库存批次。
     * 对应安卓接口: @GET("inventory/sku/{sku}") Call<List<Inventory>> getInventoryByProductSku(...)
     *
     * @param sku 产品的 SKU
     * @return 返回一个库存批次列表，如果产品不存在则返回空列表。
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<List<Inventory>> getInventoryByProductSku(@PathVariable String sku) {
        // 1. 首先根据 SKU 找到产品
        Optional<Products> productOpt = productsRepository.findBySku(sku);

        // 2. 如果产品不存在，直接返回一个空的列表
        if (productOpt.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        Products product = productOpt.get();

        // 3. 根据产品的 ID 查询所有关联的库存记录
        List<Inventory> inventoryList = inventoryRepository.findByProductId(product.getId());

        // 4. 返回查询到的列表
        return ResponseEntity.ok(inventoryList);
    }

}
