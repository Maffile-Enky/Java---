package com.takeout.user.controller;

import com.takeout.common.core.domain.Result;
import com.takeout.common.core.exception.BusinessException;
import com.takeout.user.entity.Address;
import com.takeout.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(addressService.listByUserId(userId));
    }

    @GetMapping("/{id}")
    public Result<Address> getById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        Address address = addressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        return Result.success(address);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Address address, @RequestHeader("X-User-Id") Long userId) {
        address.setId(null);
        address.setUserId(userId);
        // If first address, set as default
        if (addressService.listByUserId(userId).isEmpty()) {
            address.setIsDefault(1);
        }
        addressService.save(address);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Address address,
                               @RequestHeader("X-User-Id") Long userId) {
        Address existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        address.setId(id);
        address.setUserId(userId);
        addressService.updateById(address);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        Address existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        addressService.removeById(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        Address existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        addressService.setDefault(userId, id);
        return Result.success(null);
    }
}
