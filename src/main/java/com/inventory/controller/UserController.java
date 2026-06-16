package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        java.util.List<User> list = userService.findByPermission(userId, userRole);
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = params.get("userId") != null ? Integer.valueOf(params.get("userId").toString()) : null;
        String userRole = (String) params.get("userRole");
        if (userId == null || userRole == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        String username = (String) params.get("username");
        String phone = (String) params.get("phone");
        String status = (String) params.get("status");
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        Map<String, Object> pageData = userService.findByCondition(username, phone, status, startTime, endTime, userId, userRole);
        result.put("success", true);
        result.put("data", pageData);
        return result;
    }

    @PostMapping("/changeStatus")
    public Map<String, Object> changeStatus(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = params.get("userId") != null ? Integer.valueOf(params.get("userId").toString()) : null;
        String status = (String) params.get("status");
        if (userId == null || status == null) {
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }
        return userService.changeStatus(userId, status);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.findDetailById(id);
        result.put("success", user != null);
        result.put("data", user);
        return result;
    }
}
