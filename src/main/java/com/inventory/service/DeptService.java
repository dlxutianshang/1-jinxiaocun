package com.inventory.service;

import com.inventory.model.Dept;
import com.inventory.repository.DeptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeptService {

    @Autowired
    private DeptRepository deptRepository;

    public List<Dept> findTree(String name, String status) {
        List<Dept> all;
        if ((name == null || name.trim().isEmpty()) && (status == null || status.trim().isEmpty() || "all".equals(status))) {
            all = deptRepository.findAll();
        } else {
            all = deptRepository.findByCondition(name, status);
        }
        return buildTree(all, 0);
    }

    public List<Dept> findAllFlat() {
        return deptRepository.findAll();
    }

    private List<Dept> buildTree(List<Dept> list, int parentId) {
        List<Dept> result = new ArrayList<>();
        for (Dept dept : list) {
            if (dept.getParentId() == parentId) {
                dept.setChildren(buildTree(list, dept.getId()));
                result.add(dept);
            }
        }
        return result;
    }

    public Dept findById(Integer id) {
        return deptRepository.findById(id);
    }

    public Map<String, Object> save(Dept dept) {
        Map<String, Object> result = new HashMap<>();
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "部门名称不能为空");
            return result;
        }
        deptRepository.save(dept);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Map<String, Object> update(Dept dept) {
        Map<String, Object> result = new HashMap<>();
        if (dept.getId() == null) {
            result.put("success", false);
            result.put("message", "部门ID不能为空");
            return result;
        }
        if (dept.getName() == null || dept.getName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "部门名称不能为空");
            return result;
        }
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            result.put("success", false);
            result.put("message", "父级部门不能是自己");
            return result;
        }
        deptRepository.update(dept);
        result.put("success", true);
        result.put("message", "修改成功");
        return result;
    }

    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new HashMap<>();
        int childCount = deptRepository.countByParentId(id);
        if (childCount > 0) {
            result.put("success", false);
            result.put("message", "存在子部门，不允许删除");
            return result;
        }
        deptRepository.delete(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> saveOrder(List<Dept> list) {
        Map<String, Object> result = new HashMap<>();
        for (Dept dept : list) {
            if (dept.getId() != null && dept.getOrderNum() != null) {
                deptRepository.updateOrderNum(dept.getId(), dept.getOrderNum());
            }
        }
        result.put("success", true);
        result.put("message", "排序保存成功");
        return result;
    }
}
