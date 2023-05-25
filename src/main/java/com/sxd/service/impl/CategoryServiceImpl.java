package com.sxd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.common.CustomException;
import com.sxd.mapper.CategoryMapper;
import com.sxd.pojo.Category;
import com.sxd.pojo.Dish;
import com.sxd.pojo.Setmeal;
import com.sxd.service.CategoryService;
import com.sxd.service.DishService;
import com.sxd.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dish;

    @Autowired
    private SetmealService setmealService;


    /**
     * 判断删除分类时 分类中是否有关联的菜品
     */
    @Override
    public void remove(Long id) {
        //添加查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        //开始查询
        long count = dish.count(dishLambdaQueryWrapper);
        //判断分类中是否存在菜品
        if(count > 0){
            throw new CustomException("当前分类下关联了餐品 不能删除");
        }
        //判断分类中是否存在套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        //开始查询
        long count1 = setmealService.count(setmealLambdaQueryWrapper);

        //判断分类中是否存在套餐
        if(count1 > 0){
            //抛出异常
            throw new CustomException("当前分类下关联了套餐 不能删除");
        }

        super.removeById(id);
    }
}
