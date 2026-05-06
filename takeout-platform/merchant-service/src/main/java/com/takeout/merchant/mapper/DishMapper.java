package com.takeout.merchant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.merchant.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}