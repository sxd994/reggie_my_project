package com.sxd.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxd.common.R;
import com.sxd.pojo.Category;
import com.sxd.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info(category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize);
        //创建一个分页构造器
        Page pageInfo = new Page<>(page, pageSize);
        //添加一个条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加按照sort进行排序的条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }

    /**
     * 根据Id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long ids){

//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> updateById(@RequestBody Category category){
        categoryService.updateById(category);

        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category){
        //封装查询条件
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }
}
