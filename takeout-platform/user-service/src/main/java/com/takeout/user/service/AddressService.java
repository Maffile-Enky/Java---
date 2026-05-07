package com.takeout.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.user.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> listByUserId(Long userId);
    void setDefault(Long userId, Long addressId);
}
