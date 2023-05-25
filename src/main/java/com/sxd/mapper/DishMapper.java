package com.sxd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxd.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
