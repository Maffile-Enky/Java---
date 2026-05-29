package com.takeout.merchant.service;

import com.takeout.merchant.dto.CartItemDTO;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.impl.CartServiceImpl;
import com.takeout.merchant.service.impl.DishServiceImpl;
import com.takeout.merchant.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartServiceTest {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private DishServiceImpl dishService;

    @Autowired
    private MerchantServiceImpl merchantService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @SuppressWarnings("unchecked")
    private HashOperations<String, Object, Object> hashOperations = Mockito.mock(HashOperations.class);

    private static final Long TEST_USER_ID = 100L;
    private Long testDishId;

    @BeforeEach
    void setUp() {
        // Mock Redis操作
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.delete(anyString())).thenReturn(1L);

        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("购物车测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("购物车测试菜品");
        dish.setPrice(new BigDecimal("19.99"));
        dish.setStock(100);
        dish.setStatus(1);
        dishService.save(dish);
        
        testDishId = dish.getId();
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        redisTemplate.delete("cart:" + TEST_USER_ID);
    }

    @Test
    void testAddToCart() {
        // 添加商品到购物车
        cartService.addToCart(TEST_USER_ID, testDishId, 2);

        // 获取购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(1, cart.size());
        assertEquals(testDishId, cart.get(0).getDishId());
        assertEquals(2, cart.get(0).getQuantity());
        assertEquals(new BigDecimal("39.98"), cart.get(0).getSubtotal());
    }

    @Test
    void testAddToCartMultipleTimes() {
        // 第一次添加
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        
        // 第二次添加（相同商品）
        cartService.addToCart(TEST_USER_ID, testDishId, 3);

        // 获取购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(1, cart.size());
        assertEquals(5, cart.get(0).getQuantity()); // 2 + 3 = 5
    }

    @Test
    void testRemoveFromCart() {
        // 添加商品
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        
        // 移除商品
        cartService.removeFromCart(TEST_USER_ID, testDishId);

        // 获取购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(0, cart.size());
    }

    @Test
    void testUpdateQuantity() {
        // 添加商品
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        
        // 更新数量
        cartService.updateQuantity(TEST_USER_ID, testDishId, 5);

        // 获取购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(1, cart.size());
        assertEquals(5, cart.get(0).getQuantity());
        assertEquals(new BigDecimal("99.95"), cart.get(0).getSubtotal());
    }

    @Test
    void testClearCart() {
        // 添加多个商品
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        
        // 清空购物车
        cartService.clearCart(TEST_USER_ID);

        // 获取购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(0, cart.size());
    }

    @Test
    void testGetCartWithEmptyCart() {
        // 直接获取空购物车
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        
        assertEquals(0, cart.size());
    }

    @Test
    void testAddToCartWithInsufficientStock() {
        // 更新库存为1
        dishService.updateStock(testDishId, 1);

        // 尝试添加超过库存的数量
        assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(TEST_USER_ID, testDishId, 5);
        });
    }
}