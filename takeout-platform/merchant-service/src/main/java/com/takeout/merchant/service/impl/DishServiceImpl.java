package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.merchant.mapper.DishMapper;
import com.takeout.merchant.service.DishService;
import com.takeout.merchant.entity.Dish;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}