package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.register(user);
        result.put("success", success);
        result.put("message", success ? "注册成功" : "用户名已存在");
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        result.put("success", loginUser != null);
        result.put("message", loginUser != null ? "登录成功" : "用户名或密码错误");
        result.put("data", loginUser);
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) Integer userId,
                                    @RequestParam(required = false) String userRole) {
        Map<String, Object> result = new HashMap<>();
        if (userId == null || userRole == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        List<User> list = userService.findByPermission(userId, userRole);
        result.put("success", true);
        result.put("data", list);
        return result;
    }
}
