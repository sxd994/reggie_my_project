package com.sxd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.mapper.DishFlavorMapper;
import com.sxd.pojo.DishFlavor;
import com.sxd.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
