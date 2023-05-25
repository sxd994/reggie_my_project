package com.sxd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.mapper.AddressBookMapper;
import com.sxd.pojo.AddressBook;
import com.sxd.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
