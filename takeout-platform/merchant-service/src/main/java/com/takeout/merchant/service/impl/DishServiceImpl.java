package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.mapper.DishMapper;
import com.takeout.merchant.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Override
    public List<Dish> listByMerchant(Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId);
        wrapper.eq(Dish::getStatus, 1);
        return list(wrapper);
    }

    @Override
    public boolean updateStock(Long id, Integer stock) {
        Dish dish = getById(id);
        if (dish == null) {
            return false;
        }
        dish.setStock(stock);
        return updateById(dish);
    }
}