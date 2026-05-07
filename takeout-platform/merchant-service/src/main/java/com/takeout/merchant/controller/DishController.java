package com.takeout.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.RoleEnum;
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
        return Result.success(dishService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @PostMapping
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> save(@RequestBody Dish dish) {
        return Result.success(dishService.save(dish));
    }

    @PutMapping
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> update(@RequestBody Dish dish) {
        return Result.success(dishService.updateById(dish));
    }

    @DeleteMapping("/{id}")
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(dishService.removeById(id));
    }

    @PutMapping("/stock/{id}")
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStock(stock);
        return Result.success(dishService.updateById(dish));
    }
}
