package com.takeout.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.common.core.annotation.RequireRole;
import com.takeout.common.core.domain.Result;
import com.takeout.common.core.enums.ErrorCode;
import com.takeout.common.core.enums.RoleEnum;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.common.web.util.SecurityUtil;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1)
               .orderByDesc(Merchant::getSortWeight)
               .orderByDesc(Merchant::getCreatedAt);
        return Result.success(merchantService.list(wrapper));
    }

    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    // ==================== Merchant Self-Management ====================

    @GetMapping("/my")
    @RequireRole(RoleEnum.MERCHANT)
    public Result<Merchant> getMyMerchant(HttpServletRequest request) {
        Long userId = SecurityUtil.getUserId(request);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUserId, userId);
        return Result.success(merchantService.getOne(wrapper));
    }

    @PostMapping("/my")
    @RequireRole(RoleEnum.MERCHANT)
    public Result<Boolean> createMyMerchant(HttpServletRequest request, @RequestBody Merchant merchant) {
        Long userId = SecurityUtil.getUserId(request);
        LambdaQueryWrapper<Merchant> check = new LambdaQueryWrapper<>();
        check.eq(Merchant::getUserId, userId);
        if (merchantService.count(check) > 0) {
            throw new BusinessException(400, "您已创建过商家");
        }
        merchant.setUserId(userId);
        merchant.setStatus(1);
        merchant.setSortWeight(0);
        return Result.success(merchantService.save(merchant));
    }

    @PutMapping("/my")
    @RequireRole(RoleEnum.MERCHANT)
    public Result<Boolean> updateMyMerchant(HttpServletRequest request, @RequestBody Merchant merchant) {
        Long userId = SecurityUtil.getUserId(request);
        Merchant existing = merchantService.getById(merchant.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        merchant.setUserId(userId);
        return Result.success(merchantService.updateById(merchant));
    }

    // ==================== Admin Management ====================

    @GetMapping("/admin/list")
    @RequireRole(RoleEnum.ADMIN)
    public Result<List<Merchant>> adminList() {
        return Result.success(merchantService.list());
    }

    @PostMapping
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> save(@RequestBody Merchant merchant) {
        return Result.success(merchantService.save(merchant));
    }

    @PutMapping
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> update(@RequestBody Merchant merchant) {
        return Result.success(merchantService.updateById(merchant));
    }

    @DeleteMapping("/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(merchantService.removeById(id));
    }

    @PutMapping("/status/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setStatus(status);
        return Result.success(merchantService.updateById(merchant));
    }

    @PutMapping("/admin/status/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> adminUpdateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setStatus(status);
        return Result.success(merchantService.updateById(merchant));
    }

    @PutMapping("/admin/sort-weight/{id}")
    @RequireRole(RoleEnum.ADMIN)
    public Result<Boolean> adminUpdateSortWeight(@PathVariable Long id, @RequestParam Integer sortWeight) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setSortWeight(sortWeight);
        return Result.success(merchantService.updateById(merchant));
    }
}
