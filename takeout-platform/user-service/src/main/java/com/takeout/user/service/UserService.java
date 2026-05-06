package com.takeout.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.user.entity.User;

public interface UserService extends IService<User> {
    User getByUsername(String username);
}
