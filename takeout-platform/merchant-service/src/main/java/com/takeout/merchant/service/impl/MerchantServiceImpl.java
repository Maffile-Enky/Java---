package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.merchant.entity.Merchant;
import com.takeout.merchant.mapper.MerchantMapper;
import com.takeout.merchant.service.MerchantService;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Merchant merchant = getById(id);
        if (merchant == null) {
            return false;
        }
        merchant.setStatus(status);
        return updateById(merchant);
    }
}