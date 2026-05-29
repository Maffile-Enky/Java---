package com.takeout.merchant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.merchant.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    List<Dish> listByMerchant(Long merchantId);

    boolean updateStock(Long id, Integer stock);
}