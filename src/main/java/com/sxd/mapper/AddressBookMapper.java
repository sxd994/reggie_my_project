package com.sxd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sxd.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
