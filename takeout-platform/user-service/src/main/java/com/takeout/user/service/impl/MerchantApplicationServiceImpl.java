package com.takeout.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.user.entity.MerchantApplication;
import com.takeout.user.mapper.MerchantApplicationMapper;
import com.takeout.user.service.MerchantApplicationService;
import org.springframework.stereotype.Service;

@Service
public class MerchantApplicationServiceImpl extends ServiceImpl<MerchantApplicationMapper, MerchantApplication>
        implements MerchantApplicationService {

    @Override
    public MerchantApplication getLatestByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<MerchantApplication>()
                .eq(MerchantApplication::getUserId, userId)
                .orderByDesc(MerchantApplication::getCreatedAt)
                .last("LIMIT 1"));
    }
}
