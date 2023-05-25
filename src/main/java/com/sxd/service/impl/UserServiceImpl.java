package com.sxd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.mapper.UserMapper;
import com.sxd.pojo.User;
import com.sxd.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
