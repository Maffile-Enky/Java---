package com.takeout.merchant.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.service.DishSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish/search")
@RequiredArgsConstructor
public class DishSearchController {

    private final DishSearchService dishSearchService;

    @GetMapping("/keyword")
    public Result<List<Dish>> searchByKeyword(@RequestParam String keyword) {
        List<Dish> dishes = dishSearchService.searchByKeyword(keyword);
        return Result.success(dishes);
    }

    @GetMapping("/merchant/{merchantId}")
    public Result<List<Dish>> searchByMerchant(@PathVariable Long merchantId) {
        List<Dish> dishes = dishSearchService.searchByMerchant(merchantId);
        return Result.success(dishes);
    }

    @GetMapping("/combined")
    public Result<List<Dish>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long merchantId) {
        List<Dish> dishes = dishSearchService.search(keyword, merchantId);
        return Result.success(dishes);
    }
}