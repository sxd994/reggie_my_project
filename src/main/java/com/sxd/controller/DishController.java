package com.sxd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.sxd.common.R;
import com.sxd.dto.DishDto;
import com.sxd.pojo.Category;
import com.sxd.pojo.Dish;
import com.sxd.pojo.DishFlavor;
import com.sxd.service.CategoryService;
import com.sxd.service.DishFlavorService;
import com.sxd.service.DishService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品口味管理
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key = "Dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //创建一个分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        //创建一个条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getSort);
        //分页查询
        dishService.page(pageInfo, lambdaQueryWrapper);
        //进行对象的复制
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        //单独对records进行处理 加上菜品分类
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list =  records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            Long categoryId = item.getCategoryId();
            //通过id查询分类
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //进行对象的复制
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
//        records.forEach(record ->{
//            DishDto dishDto = new DishDto();
//
//            Long categoryId = record.getCategoryId();//分类id
//            //通过分类id查询到分类名称
//            Category category = categoryService.getById(categoryId);
//            String categoryName = category.getName();
//            //对象的复制
//            BeanUtils.copyProperties(record, dishDto);
//            dishDto.setCategoryName(categoryName);
//            list.add(dishDto);
//        });

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        R<DishDto> dishAndFlavor = dishService.getDishAndFlavor(id);

        return dishAndFlavor;
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateDishAndFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对象
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //动态生成key
        String key = "Dish_" + dish.getCategoryId() + "_" + dish.getStatus();// Dish_1234123_1
        //先查询redis中是否有数据,如果有直接返回数据
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtos != null){
            return R.success(dishDtos);
        }

        //构造查询条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //只查询状态等于1的菜品
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        //进行排序
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        dishDtos =list.stream().map((item) -> {
            //进行一个对象的复制
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //获取菜品口味信息
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(DishFlavor::getDishId, id);
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper1);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        //如果没有就查询数据库，再将数据存入redis中
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);

        return R.success(dishDtos);
    }
}
