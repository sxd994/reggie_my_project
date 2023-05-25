package com.sxd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxd.common.R;
import com.sxd.dto.SetmealDto;
import com.sxd.pojo.Category;
import com.sxd.pojo.Setmeal;
import com.sxd.pojo.SetmealDish;
import com.sxd.service.CategoryService;
import com.sxd.service.SetmealDishService;
import com.sxd.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.awt.print.PageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);

        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //创建一个分页查询界面
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> pageDto = new Page();
        //创建一个条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //进行排序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询
        setmealService.page(pageInfo, lambdaQueryWrapper);
        //进行对象的拷贝
        BeanUtils.copyProperties(pageInfo, pageDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //遍历records集合 将categoryName加入其中
        List<SetmealDto> list = records.stream().map((item) ->{
            //新创建的对象 里面只有categoryName 所以需要填充其他的值
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category= categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);

            }
            return setmealDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);


        return R.success(pageDto);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.deleteWithDish(ids);

        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> get(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId, setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getStatus, setmeal.getStatus());

        List<Setmeal> setmeals = setmealService.list(lambdaQueryWrapper);
        return R.success(setmeals);
    }

}
