package com.sxd.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.mapper.ShoppingCartMapper;
import com.sxd.pojo.ShoppingCart;
import com.sxd.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
