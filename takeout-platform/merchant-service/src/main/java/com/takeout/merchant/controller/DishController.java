package com.takeout.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.merchant.entity.Dish;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.DishService;
import com.takeout.merchant.service.MerchantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;
    private final MerchantService merchantService;

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
    public Result<Boolean> save(HttpServletRequest request, @RequestBody Dish dish) {
        Long userId = SecurityUtil.getUserId(request);
        Merchant merchant = getMerchantByUserId(userId);
        dish.setMerchantId(merchant.getId());
        if (dish.getStatus() == null) dish.setStatus(1);
        if (dish.getStock() == null) dish.setStock(0);
        return Result.success(dishService.save(dish));
    }

    @PutMapping
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> update(HttpServletRequest request, @RequestBody Dish dish) {
        Long userId = SecurityUtil.getUserId(request);
        Merchant merchant = getMerchantByUserId(userId);
        Dish existing = dishService.getById(dish.getId());
        if (existing == null || !existing.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException(403, "无权操作此菜品");
        }
        dish.setMerchantId(merchant.getId());
        return Result.success(dishService.updateById(dish));
    }

    @DeleteMapping("/{id}")
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = SecurityUtil.getUserId(request);
        Merchant merchant = getMerchantByUserId(userId);
        Dish existing = dishService.getById(id);
        if (existing == null || !existing.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException(403, "无权操作此菜品");
        }
        return Result.success(dishService.removeById(id));
    }

    @PutMapping("/stock/{id}")
    @RequireRole({RoleEnum.MERCHANT, RoleEnum.ADMIN})
    public Result<Boolean> updateStock(HttpServletRequest request, @PathVariable Long id, @RequestParam Integer stock) {
        Long userId = SecurityUtil.getUserId(request);
        Merchant merchant = getMerchantByUserId(userId);
        Dish existing = dishService.getById(id);
        if (existing == null || !existing.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException(403, "无权操作此菜品");
        }
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStock(stock);
        return Result.success(dishService.updateById(dish));
    }

    private Merchant getMerchantByUserId(Long userId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        Merchant merchant = merchantService.getOne(wrapper);
        if (merchant == null) {
            throw new BusinessException(404, "商家信息不存在");
        }
        return merchant;
    }
}
