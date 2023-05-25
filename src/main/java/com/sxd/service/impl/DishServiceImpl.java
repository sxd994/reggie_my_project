package com.sxd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.common.R;
import com.sxd.dto.DishDto;
import com.sxd.mapper.DishMapper;
import com.sxd.pojo.Dish;
import com.sxd.pojo.DishFlavor;
import com.sxd.service.DishFlavorService;
import com.sxd.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 涉及到多表查询 需要开启事务
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);
        //获取菜品id
        Long dishId = dishDto.getCategoryId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
//        flavors.forEach(dishFlavor -> {
//            dishFlavor.setDishId(dishId);
//        });
        flavors.stream().map(item -> {
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }


    public R<DishDto> getDishAndFlavor(Long id){
        //先查询菜品表
        Dish dish = this.getById(id);
        //进行对象的复制
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getCategoryId());
        //通过id相同来进行口味查询
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        //将list传入dishDto中
        dishDto.setFlavors(list);
        return R.success(dishDto);
    }

    @Override
    public void updateDishAndFlavor(DishDto dishDto) {
        //首先修改菜品表信息
        this.updateById(dishDto);
        //在修改菜品口味表信息
        //先将菜品口味表中的数据全部删除，在添加新的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(lambdaQueryWrapper);
        //添加新的数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
