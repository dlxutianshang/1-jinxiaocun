package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.UserService;
import com.inventory.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    private boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.register(user);
        result.put("success", success);
        result.put("message", success ? "注册成功" : "用户名已存在");
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        result.put("success", loginUser != null);
        result.put("message", loginUser != null ? "登录成功" : "用户名或密码错误");
        if (loginUser != null) {
            session.setAttribute("currentUser", loginUser);
            result.put("data", loginUser);
        }
        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        session.invalidate();
        result.put("success", true);
        result.put("message", "退出成功");
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        java.util.List<User> list = userService.findByPermission(currentUser.getId(), currentUser.getRole());
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        String username = (String) params.get("username");
        String phone = (String) params.get("phone");
        String status = (String) params.get("status");
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        Map<String, Object> pageData = userService.findByCondition(username, phone, status, startTime, endTime, currentUser.getId(), currentUser.getRole());
        result.put("success", true);
        result.put("data", pageData);
        return result;
    }

    @PostMapping("/changeStatus")
    public Map<String, Object> changeStatus(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
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
    public Map<String, Object> getById(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session) && !id.equals(currentUser.getId())) {
            result.put("success", false);
            result.put("message", "无权限查看");
            return result;
        }
        User user = userService.findDetailById(id);
        result.put("success", user != null);
        result.put("data", user);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        return userService.createUser(user);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        return userService.updateUser(user, currentUser.getId());
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        Integer userId = params.get("userId") != null ? Integer.valueOf(params.get("userId").toString()) : null;
        if (userId == null) {
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }
        return userService.deleteUser(userId, currentUser.getId());
    }

    @PostMapping("/deleteBatch")
    public Map<String, Object> deleteBatch(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        if (ids == null || ids.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的用户");
            return result;
        }
        return userService.deleteUsersBatch(ids, currentUser.getId());
    }

    @GetMapping("/checkUsername")
    public Map<String, Object> checkUsername(@RequestParam String username, @RequestParam(required = false) Integer excludeId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = getCurrentUser(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return userService.checkUsername(username, excludeId);
    }
}
