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
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
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
    private HashOperations<String, Object, Object> hashOperations;

    private static final Long TEST_USER_ID = 100L;
    private Long testDishId;

    @BeforeEach
    void setUp() {
        hashOperations = org.mockito.Mockito.mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        doReturn(1L).when(redisTemplate).delete(anyString());

        Merchant merchant = new Merchant();
        merchant.setName("购物车测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

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
    }

    @Test
    void testAddToCart() {
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
    }

    @Test
    void testGetCart() {
        cartService.addToCart(TEST_USER_ID, testDishId, 3);
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        assertNotNull(cart);
    }

    @Test
    void testUpdateQuantity() {
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        cartService.updateQuantity(TEST_USER_ID, testDishId, 5);
    }

    @Test
    void testRemoveFromCart() {
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        cartService.removeFromCart(TEST_USER_ID, testDishId);
    }

    @Test
    void testClearCart() {
        cartService.addToCart(TEST_USER_ID, testDishId, 2);
        cartService.clearCart(TEST_USER_ID);
        List<CartItemDTO> cart = cartService.getCart(TEST_USER_ID);
        assertTrue(cart.isEmpty());
    }
}
