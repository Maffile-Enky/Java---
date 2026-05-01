package com.takeout.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.common.core.domain.Result;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping("/list")
    public Result<List<Dish>> list() {
        List<Dish> list = dishService.list();
        return Result.success(list);
    }

    @GetMapping("/list/{merchantId}")
    public Result<List<Dish>> listByMerchant(@PathVariable Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId);
        wrapper.eq(Dish::getStatus, 1);
        List<Dish> list = dishService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id) {
        Dish dish = dishService.getById(id);
        return Result.success(dish);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Dish dish) {
        boolean result = dishService.save(dish);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Dish dish) {
        boolean result = dishService.updateById(dish);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = dishService.removeById(id);
        return Result.success(result);
    }

    @PutMapping("/stock/{id}")
    public Result<Boolean> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStock(stock);
        boolean result = dishService.updateById(dish);
        return Result.success(result);
    }
}