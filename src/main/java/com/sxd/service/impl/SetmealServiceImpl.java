package com.sxd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.common.CustomException;
import com.sxd.dto.SetmealDto;
import com.sxd.mapper.SetmealMapper;
import com.sxd.pojo.Setmeal;
import com.sxd.pojo.SetmealDish;
import com.sxd.service.SetmealDishService;
import com.sxd.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    @Transactional
    @Override
    public void saveWithDish(SetmealDto dto) {
        log.info("套餐信息：{}" ,dto);
        //先存储setmeal的信息， 执行insert操作
        this.save(dto);
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        //为setmealDishes 附上setmeal id 的值
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList());
        //在对setmealDish进行操作
        setmealDishService.saveBatch(setmealDishes);
    }


    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //首先判断能不能删除  select count(*) from setmeal where id in (1,2,3) and status = 1;
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        //进行查询
        long count = this.count(lambdaQueryWrapper);
        if(count > 0){
            //不能删除
            throw new CustomException("正在售卖中，不能删除");
        }
        //能删除
        //先删除Setmeal中的数据
        this.removeByIds(ids);
        //接在在删除SetmealDish中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(SetmealDish::getSetmealId, ids);

        setmealDishService.removeByIds(ids);
    }


}
