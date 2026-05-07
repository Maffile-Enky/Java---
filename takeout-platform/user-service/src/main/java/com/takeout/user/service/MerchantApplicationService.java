package com.takeout.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.user.entity.MerchantApplication;

public interface MerchantApplicationService extends IService<MerchantApplication> {
    MerchantApplication getLatestByUserId(Long userId);
}
