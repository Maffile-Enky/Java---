package com.takeout.merchant.service;

import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MerchantServiceTest {

    @Autowired
    private MerchantServiceImpl merchantService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void testCreateAndGetMerchant() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("测试商家");
        merchant.setAddress("测试地址");
        merchant.setPhone("13800138000");
        merchant.setStatus(1);
        
        boolean saved = merchantService.save(merchant);
        assertTrue(saved);
        assertNotNull(merchant.getId());

        // 查询商家
        Merchant found = merchantService.getById(merchant.getId());
        assertNotNull(found);
        assertEquals("测试商家", found.getName());
        assertEquals("测试地址", found.getAddress());
    }

    @Test
    void testUpdateMerchant() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("原商家名称");
        merchant.setAddress("原地址");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 更新商家
        merchant.setName("更新后的名称");
        merchant.setAddress("更新后的地址");
        boolean updated = merchantService.updateById(merchant);
        assertTrue(updated);

        // 验证更新
        Merchant updatedMerchant = merchantService.getById(merchant.getId());
        assertEquals("更新后的名称", updatedMerchant.getName());
        assertEquals("更新后的地址", updatedMerchant.getAddress());
    }

    @Test
    void testDeleteMerchant() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("待删除商家");
        merchant.setStatus(0);
        merchantService.save(merchant);

        Long id = merchant.getId();
        
        // 删除商家
        boolean deleted = merchantService.removeById(id);
        assertTrue(deleted);

        // 验证删除
        Merchant found = merchantService.getById(id);
        assertNull(found);
    }

    @Test
    void testUpdateStatus() {
        // 创建商家
        Merchant merchant = new Merchant();
        merchant.setName("状态测试商家");
        merchant.setStatus(1);
        merchantService.save(merchant);

        // 更新状态
        boolean updated = merchantService.updateStatus(merchant.getId(), 0);
        assertTrue(updated);

        // 验证状态
        Merchant updatedMerchant = merchantService.getById(merchant.getId());
        assertEquals(0, updatedMerchant.getStatus());
    }
}