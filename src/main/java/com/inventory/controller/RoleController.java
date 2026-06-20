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

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Role role, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        Role newRole = new Role();
        newRole.setName(role.getName());
        newRole.setRoleKey(role.getRoleKey());
        newRole.setSort(role.getSort());
        newRole.setStatus(role.getStatus());
        newRole.setRemark(role.getRemark());
        return roleService.create(newRole);
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
        Role role = roleService.findById(id);
        if (role == null) {
            result.put("success", false);
            result.put("message", "角色不存在");
            return result;
        }
        result.put("success", true);
        result.put("data", role);
        return result;
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
        boolean isSuperAdmin = "ADMIN".equals(currentUser.getRole());
        return roleService.update(role, isSuperAdmin);
    }
}
