package com.takeout.merchant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.merchant.mapper.MerchantMapper;
import com.takeout.merchant.service.MerchantService;
import com.takeout.merchant.entity.Merchant;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {
}