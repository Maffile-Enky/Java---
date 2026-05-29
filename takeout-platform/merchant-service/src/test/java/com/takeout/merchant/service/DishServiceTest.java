package com.takeout.merchant.service;

import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.impl.DishServiceImpl;
import com.takeout.merchant.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DishServiceTest {

    @Autowired
    private DishServiceImpl dishService;

    @Autowired
    private MerchantServiceImpl merchantService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testCreateDish() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("菜品测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("测试菜品");
        dish.setPrice(new BigDecimal("19.99"));
        dish.setDescription("测试描述");
        dish.setStock(100);
        dish.setStatus(1);

        boolean saved = dishService.save(dish);
        assertTrue(saved);
        assertNotNull(dish.getId());
        assertEquals(0, dish.getVersion()); // 初始版本为0
    }

    @Test
    void testGetDishById() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("查询测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("查询测试菜品");
        dish.setPrice(new BigDecimal("29.99"));
        dish.setStock(50);
        dish.setStatus(1);
        dishService.save(dish);

        // 查询菜品
        Dish found = dishService.getById(dish.getId());
        assertNotNull(found);
        assertEquals("查询测试菜品", found.getName());
        assertEquals(new BigDecimal("29.99"), found.getPrice());
    }

    @Test
    void testListDishesByMerchant() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("列表测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建多个菜品
        for (int i = 1; i <= 3; i++) {
            Dish dish = new Dish();
            dish.setMerchantId(merchant.getId());
            dish.setName("菜品" + i);
            dish.setPrice(new BigDecimal(String.valueOf(i * 10)));
            dish.setStock(100);
            dish.setStatus(1);
            dishService.save(dish);
        }

        // 查询商家菜品
        List<Dish> dishes = dishService.listByMerchant(merchant.getId());
        assertEquals(3, dishes.size());
    }

    @Test
    void testUpdateDishStock() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("库存测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("库存测试菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(100);
        dish.setStatus(1);
        dishService.save(dish);

        // 更新库存
        boolean updated = dishService.updateStock(dish.getId(), 80);
        assertTrue(updated);

        // 验证库存
        Dish updatedDish = dishService.getById(dish.getId());
        assertEquals(80, updatedDish.getStock());
        assertEquals(1, updatedDish.getVersion()); // 版本号增加
    }

    @Test
    void testDeleteDish() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("删除测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 创建菜品
        Dish dish = new Dish();
        dish.setMerchantId(merchant.getId());
        dish.setName("待删除菜品");
        dish.setPrice(new BigDecimal("10.00"));
        dish.setStock(10);
        dish.setStatus(1);
        dishService.save(dish);

        Long id = dish.getId();

        // 删除菜品
        boolean deleted = dishService.removeById(id);
        assertTrue(deleted);

        // 验证删除
        Dish found = dishService.getById(id);
        assertNull(found);
    }
}