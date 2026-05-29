package com.takeout.merchant.service;

import com.takeout.merchant.entity.Dish;

import java.util.List;

public interface DishSearchService {

    /**
     * 根据关键词搜索菜品
     * @param keyword 关键词
     * @return 菜品列表
     */
    List<Dish> searchByKeyword(String keyword);

    /**
     * 根据商家ID搜索菜品
     * @param merchantId 商家ID
     * @return 菜品列表
     */
    List<Dish> searchByMerchant(Long merchantId);

    /**
     * 综合搜索
     * @param keyword 关键词（可选）
     * @param merchantId 商家ID（可选）
     * @return 菜品列表
     */
    List<Dish> search(String keyword, Long merchantId);
}