package com.sxd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sxd.dto.SetmealDto;
import com.sxd.pojo.Setmeal;
import com.sxd.pojo.SetmealDish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    public void saveWithDish(SetmealDto dto);

    public void deleteWithDish(List<Long> ids);
}
