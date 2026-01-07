package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.mapper.CartMapper;
import server.mapper.RecipeMapper;
import server.model.Cart;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private RecipeMapper recipeMapper;

    // 获取用户购物车列表
    @GetMapping("/user")
    public List<Cart> getUserCart(@RequestAttribute Integer userId) {
        return cartMapper.findByUserId(userId);
    }

    // 添加到购物车
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestAttribute Integer userId, @RequestBody Cart cart) {
        cart.setUserId(userId);
        
        // 检查是否已存在
        Cart existingCart = cartMapper.findByUserIdAndRecipeId(userId, cart.getRecipeId());
        if (existingCart != null) {
            // 更新数量
            existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
            cartMapper.update(existingCart);
            return ResponseEntity.ok(existingCart);
        }

        // 新增
        cartMapper.insert(cart);
        return ResponseEntity.ok(cart);
    }

    // 更新购物车商品数量
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(
            @RequestAttribute Integer userId,
            @PathVariable Integer id,
            @RequestBody Cart cart) {
        // 验证权限
        Cart existingCart = cartMapper.findById(id);
        if (existingCart == null || !existingCart.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("无权限修改");
        }

        cart.setId(id);
        cart.setUserId(userId);
        cartMapper.update(cart);
        return ResponseEntity.ok(cart);
    }

    // 删除购物车商品
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(
            @RequestAttribute Integer userId,
            @PathVariable Integer id) {
        // 验证权限
        Cart existingCart = cartMapper.findById(id);
        if (existingCart == null || !existingCart.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("无权限删除");
        }

        cartMapper.delete(id);
        return ResponseEntity.ok().build();
    }

    // 清空购物车
    @DeleteMapping("/user")
    public ResponseEntity<?> clearCart(@RequestAttribute Integer userId) {
        cartMapper.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
} 