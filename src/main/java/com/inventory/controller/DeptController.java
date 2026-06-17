package com.inventory.controller;

import com.inventory.model.Dept;
import com.inventory.model.User;
import com.inventory.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/dept")
@CrossOrigin
public class DeptController {

    @Autowired
    private DeptService deptService;

    private User requireLogin(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    @PostMapping("/tree")
    public Map<String, Object> tree(@RequestBody(required = false) Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        String deptName = null;
        String status = null;
        Integer companyId = null;
        if (params != null) {
            deptName = (String) params.get("deptName");
            status = (String) params.get("status");
            if (params.get("companyId") != null) {
                companyId = Integer.valueOf(params.get("companyId").toString());
            }
        }
        List<Dept> tree = deptService.findTree(deptName, status, companyId);
        result.put("success", true);
        result.put("data", tree);
        return result;
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
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        String deptName = (String) params.get("deptName");
        String status = (String) params.get("status");
        int pageNo = params.get("pageNo") != null ? Integer.parseInt(params.get("pageNo").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        Map<String, Object> pageData = deptService.findByPage(deptName, status, pageNo, pageSize);
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
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        List<Dept> list = deptService.findAllFlat();
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
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            result.put("success", false);
            result.put("message", "无权限操作");
            return result;
        }
        Dept dept = deptService.findById(id);
        result.put("success", dept != null);
        result.put("data", dept);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Dept dept, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        return deptService.save(dept);
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Dept dept, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        return deptService.update(dept);
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Integer> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        Integer deptId = params.get("deptId");
        return deptService.delete(deptId);
    }

    @PostMapping("/saveSort")
    public Map<String, Object> saveSort(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        Integer deptId = params.get("deptId") != null ? Integer.valueOf(params.get("deptId").toString()) : null;
        Integer sort = params.get("sort") != null ? Integer.valueOf(params.get("sort").toString()) : null;
        return deptService.saveSort(deptId, sort);
    }

    @PostMapping("/saveSortBatch")
    public Map<String, Object> saveSortBatch(@RequestBody List<Map<String, Object>> list, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        for (Map<String, Object> item : list) {
            Integer deptId = item.get("deptId") != null ? Integer.valueOf(item.get("deptId").toString()) : null;
            Integer sort = item.get("sort") != null ? Integer.valueOf(item.get("sort").toString()) : null;
            if (deptId != null && sort != null) {
                deptService.saveSort(deptId, sort);
            }
        }
        result.put("success", true);
        result.put("message", "排序保存成功");
        return result;
    }

    @PostMapping("/changeStatus")
    public Map<String, Object> changeStatus(@RequestBody Map<String, Object> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
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
        Integer deptId = params.get("deptId") != null ? Integer.valueOf(params.get("deptId").toString()) : null;
        String status = (String) params.get("status");
        return deptService.changeStatus(deptId, status);
    }
}
