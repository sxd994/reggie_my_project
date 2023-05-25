package com.sxd.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxd.common.R;
import com.sxd.pojo.Employee;
import com.sxd.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //如果没有查询到 则返回登录失败的结果
        if(emp == null){
            return R.error("找不到用户名");
        }

        //进行密码比对若是密码不一致，返回登录失败的结果
        if(!emp.getPassword().equals(password)){
            return R.error("密码不正确");
        }

        //查询状态是否禁用
        if(emp.getStatus() == 0){
            return R.error("该账号已禁用");
        }
        //6.登录成功，将员工id存入Session中并返回登录成功的结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        log.info(employee.toString());

        //设置初始密码.需要进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        //设置创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //设置修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置修改人,就是当前登录的人,之前已经将当前登陆人的信息传入Session中,所以现在我们将取出来
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //再将整个员工信息上传到数据库
        employeeService.save(employee);
        //返回上传成功的信息
        return R.success("上传成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //创建一个分页构造器
        Page pageInfo = new Page(page, pageSize);
        //创建一个条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加查询条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 启用禁用员工账号
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        //获取线程id
        long id = Thread.currentThread().getId();
        log.info("线程id为{}", id);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        Employee employee = employeeService.getById(id);

        return R.success(employee);
    }
}
