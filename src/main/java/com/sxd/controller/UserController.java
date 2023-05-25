package com.sxd.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sxd.common.R;
import com.sxd.pojo.User;
import com.sxd.service.UserService;
import com.sxd.utils.MyAliyunUtils;
import com.sxd.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.lang.LambdaExpressionNestedState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;



    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) throws ExecutionException, InterruptedException {
        //先获取手机号
        String phone = user.getPhone();
        //判断手机号是否为空,不为空则继续
        if(StringUtils.isNotEmpty(phone)){
            //生成四位的验证码
           String code = ValidateCodeUtils.generateValidateCode(4).toString();
           log.info(code);
            //发送验证码到手机
//            MyAliyunUtils.sentMsg(phone, code);
            //将生成的验证码保存在Session中
//            session.setAttribute(phone, code);
            //将生成的验证码存入redis中

            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码发送成功");
        }
        return R.error("短信验证码发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //进行验证码的对比，从Session中拿出之前存入的code
//        String attribute = session.getAttribute(phone).toString();
        //进行验证码对比，从redis中取出存入的code
        String code1 = (String) redisTemplate.opsForValue().get(phone);

        if(code != null && code1.equals(code)){
            //登录成功
            // 判断是否是第一次登录，如果是加入数据库 完成注册
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }


}
