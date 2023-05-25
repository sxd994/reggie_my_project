package com.sxd.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sxd.common.BaseContext;
import com.sxd.common.R;
import com.sxd.pojo.ShoppingCart;
import com.sxd.service.ShoppingCartService;
import com.sxd.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //首先获取到user_id
        Long user_id = BaseContext.getCurrentId();
        shoppingCart.setUserId(user_id);
        //判断传进来的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, user_id);
        if(shoppingCart.getDishId() != null){
            //传进来的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else {
            //传进来的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询数据库中是否存在这个套餐或者是菜品 select * from shoppingCart where user_id = ? and DishId = ?
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);

        if(one != null){
            //说明不是第一次存入 则在原来的基础上加一
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
        }else {
            //第一次存入
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart.setCreateTime(LocalDateTime.now());
            one = shoppingCart;
        }
        return R.success(one);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //首先获取到user_id
        Long user_id = BaseContext.getCurrentId();
        shoppingCart.setUserId(user_id);
        //判断传进来的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, user_id);
        if(shoppingCart.getDishId() != null){
            //传进来的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else {
            //传进来的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询数据库中是否存在这个套餐或者是菜品 select * from shoppingCart where user_id = ? and DishId = ?
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        if(one != null){
            //说明不是第一次存入 则在原来的基础上加一
            //判断数量是多少 >1 则数量减1 小于1则直接移除
            if(one.getNumber() > 1){
                one.setNumber(one.getNumber() - 1);
                shoppingCartService.updateById(one);
            }else {
                shoppingCartService.removeById(one);
            }
        }
        return R.success(one);
    }

    @GetMapping("list")
    public R<List<ShoppingCart>> get(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //设置用户id
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //通过时间来排序
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        //进行查询
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);
    }
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //直接删除
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }
}
