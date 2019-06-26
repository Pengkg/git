package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: pinyougou_parent
 * @BelongsPackage: com.pinyougou.manager
 * @Author: pky
 * @CreateTime: 2019-05-25 18:57
 * @Description: ${Description}
 */
@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping("info")
    public Map<String,Object> info(){
        Map<String, Object> map = new HashMap<>();
        //获取登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName", userName);
        return map;
    }
}
