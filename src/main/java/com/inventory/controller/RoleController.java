package com.inventory.controller;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/role")
@CrossOrigin
public class RoleController {

    @Autowired
    private RoleService roleService;

    private User requireLogin(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    private boolean isAdmin(HttpSession session) {
        User user = requireLogin(session);
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    @PostMapping("/query")
    public Map<String, Object> query(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        String roleName = (String) params.get("roleName");
        String roleKey = (String) params.get("roleKey");
        String status = (String) params.get("status");
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        Map<String, Object> pageData = roleService.findByCondition(roleName, roleKey, status, startTime, endTime);
        result.put("success", true);
        result.put("data", pageData);
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        List<Role> list = roleService.findAll();
        result.put("success", true);
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        Role role = roleService.findById(id);
        result.put("success", role != null);
        result.put("data", role);
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
        Integer roleId = params.get("roleId") != null ? Integer.valueOf(params.get("roleId").toString()) : null;
        String status = (String) params.get("status");
        if (roleId == null || status == null) {
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }
        return roleService.changeStatus(roleId, status);
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Role role, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        return roleService.createRole(role);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Role role, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        return roleService.updateRole(role);
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        Integer roleId = params.get("roleId") != null ? Integer.valueOf(params.get("roleId").toString()) : null;
        if (roleId == null) {
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }
        return roleService.deleteRole(roleId);
    }

    @PostMapping("/deleteBatch")
    public Map<String, Object> deleteBatch(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        if (!isAdmin(session)) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        if (ids == null || ids.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的角色");
            return result;
        }
        return roleService.deleteRolesBatch(ids);
    }

    @GetMapping("/checkRoleKey")
    public Map<String, Object> checkRoleKey(@RequestParam String roleKey, @RequestParam(required = false) Integer excludeId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return roleService.checkRoleKey(roleKey, excludeId);
    }
}
