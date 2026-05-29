package com.takeout.merchant.service;

import com.takeout.merchant.dto.InventoryDTO;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.impl.DishServiceImpl;
import com.takeout.merchant.service.impl.InventoryServiceImpl;
import com.takeout.merchant.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryServiceTest {

    @Autowired
    private InventoryServiceImpl inventoryService;

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
    private ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

    private void setUpRedisMock() {
        // Mock Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null); // 默认返回null，表示未处理过
        when(valueOperations.setIfAbsent(anyString(), anyString())).thenReturn(true); // 默认允许设置
    }

    @Test
    void testDeductStock() {
        setUpRedisMock();
        
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("库存测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存100）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("扣减测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(100);
        dish.setStatus(1);
        dishService.save(dish);

        // 扣减库存
        InventoryDTO dto = new InventoryDTO();
        dto.setDishId(dish.getId());
        dto.setQuantity(10);
        dto.setOrderId("ORDER_TEST_001");

        boolean success = inventoryService.deductStock(dto);
        assertTrue(success);

        // 验证库存
        Dish updatedDish = dishService.getById(dish.getId());
        assertEquals(90, updatedDish.getStock());
        assertEquals(1, updatedDish.getVersion());
    }

    @Test
    void testDeductStockInsufficient() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("库存不足测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存5）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("库存不足测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(5);
        dish.setStatus(1);
        dishService.save(dish);

        // 尝试扣减超过库存的数量
        InventoryDTO dto = new InventoryDTO();
        dto.setDishId(dish.getId());
        dto.setQuantity(10);
        dto.setOrderId("ORDER_TEST_002");

        assertThrows(RuntimeException.class, () -> {
            inventoryService.deductStock(dto);
        });
    }

    @Test
    void testDeductStockIdempotent() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("幂等测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存100）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("幂等测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(100);
        dish.setStatus(1);
        dishService.save(dish);

        String orderId = "ORDER_IDEMPOTENT_001";
        
        // 第一次扣减
        InventoryDTO dto = new InventoryDTO();
        dto.setDishId(dish.getId());
        dto.setQuantity(10);
        dto.setOrderId(orderId);
        
        boolean success1 = inventoryService.deductStock(dto);
        assertTrue(success1);
        
        // 验证库存
        Dish dishAfterFirst = dishService.getById(dish.getId());
        assertEquals(90, dishAfterFirst.getStock());

        // 第二次扣减（相同订单ID）
        boolean success2 = inventoryService.deductStock(dto);
        assertTrue(success2); // 应该成功（幂等返回）
        
        // 验证库存未再减少
        Dish dishAfterSecond = dishService.getById(dish.getId());
        assertEquals(90, dishAfterSecond.getStock()); // 库存仍为90

        // 清理Redis缓存
        redisTemplate.delete("inventory:deducted:" + orderId);
    }

    @Test
    void testRevertStock() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("返还测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存100）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("返还测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(90);
        dish.setStatus(1);
        dishService.save(dish);

        // 返还库存
        InventoryDTO dto = new InventoryDTO();
        dto.setDishId(dish.getId());
        dto.setQuantity(10);
        dto.setOrderId("ORDER_REVERT_001");

        boolean success = inventoryService.revertStock(dto);
        assertTrue(success);

        // 验证库存
        Dish updatedDish = dishService.getById(dish.getId());
        assertEquals(100, updatedDish.getStock());
    }

    @Test
    void testCheckStockSufficient() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("检查库存商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存50）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("检查库存菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(50);
        dish.setStatus(1);
        dishService.save(dish);

        // 检查库存充足
        boolean sufficient = inventoryService.checkStock(dish.getId(), 30);
        assertTrue(sufficient);
    }

    @Test
    void testCheckStockInsufficient() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("检查库存不足商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品（库存10）
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("检查库存不足菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(10);
        dish.setStatus(1);
        dishService.save(dish);

        // 检查库存不足
        boolean sufficient = inventoryService.checkStock(dish.getId(), 20);
        assertFalse(sufficient);
    }
}