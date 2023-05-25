package com.sxd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sxd.mapper.EmployeeMapper;
import com.sxd.pojo.Employee;
import com.sxd.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
