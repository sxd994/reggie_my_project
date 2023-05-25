package com.sxd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sxd.common.R;
import com.sxd.dto.DishDto;
import com.sxd.pojo.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);
    public R<DishDto> getDishAndFlavor(Long id);

    public void updateDishAndFlavor(DishDto dishDto);
}
