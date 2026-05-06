package com.takeout.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.common.core.domain.Result;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("merchant-service OK");
    }

    @GetMapping("/list")
    public Result<List<Merchant>> list() {
        List<Merchant> list = merchantService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        return Result.success(merchant);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Merchant merchant) {
        boolean result = merchantService.save(merchant);
        return Result.success(result);
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Merchant merchant) {
        boolean result = merchantService.updateById(merchant);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = merchantService.removeById(id);
        return Result.success(result);
    }

    @PutMapping("/status/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setStatus(status);
        boolean result = merchantService.updateById(merchant);
        return Result.success(result);
    }
}