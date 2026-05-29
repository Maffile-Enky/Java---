package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.mapper.DishMapper;
import com.takeout.merchant.service.DishSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishSearchServiceImpl implements DishSearchService {

    private final DishMapper dishMapper;

    @Override
    public List<Dish> searchByKeyword(String keyword) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Dish::getName, keyword)
               .or()
               .like(Dish::getDescription, keyword)
               .eq(Dish::getStatus, 1);
        return dishMapper.selectList(wrapper);
    }

    @Override
    public List<Dish> searchByMerchant(Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId)
               .eq(Dish::getStatus, 1);
        return dishMapper.selectList(wrapper);
    }

    @Override
    public List<Dish> search(String keyword, Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(Dish::getMerchantId, merchantId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Dish::getName, keyword)
                             .or()
                             .like(Dish::getDescription, keyword));
        }
        
        wrapper.eq(Dish::getStatus, 1);
        return dishMapper.selectList(wrapper);
    }
}