package com.takeout.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.user.entity.User;

public interface UserService extends IService<User> {
    User getByUsername(String username);
    User getByPhone(String phone);
    User getByOpenid(String openid);
    IPage<User> listUsers(Page<User> page, String keyword);
}
