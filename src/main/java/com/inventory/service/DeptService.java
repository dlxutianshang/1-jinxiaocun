package com.inventory.service;

import com.inventory.model.Dept;
import com.inventory.repository.DeptRepository;
import com.inventory.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public List<Dept> findTree(String deptName, String status, Integer companyId) {
        List<Dept> all;
        if ((deptName == null || deptName.trim().isEmpty()) && (status == null || status.trim().isEmpty() || "all".equals(status))) {
            all = deptRepository.findAll();
        } else {
            all = deptRepository.findByCondition(deptName, status);
        }
        return buildTree(all, 0);
    }

    public Map<String, Object> findByPage(String deptName, String status, int pageNo, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<Dept> list = deptRepository.findByPage(deptName, status, pageNo, pageSize);
        int total = deptRepository.countByCondition(deptName, status);
        result.put("list", list);
        result.put("total", total);
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        return result;
    }

    public List<Dept> findAllFlat() {
        return deptRepository.findAll();
    }

    private List<Dept> buildTree(List<Dept> list, int parentId) {
        List<Dept> result = new ArrayList<>();
        for (Dept dept : list) {
            if (dept.getParentId() == parentId) {
                dept.setChildren(buildTree(list, dept.getDeptId()));
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
        if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "部门名称不能为空");
            return result;
        }
        if (dept.getParentId() == null) {
            dept.setParentId(0);
        }
        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        if (dept.getStatus() == null) {
            dept.setStatus("0");
        }
        deptRepository.save(dept);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Map<String, Object> update(Dept dept) {
        Map<String, Object> result = new HashMap<>();
        if (dept.getDeptId() == null) {
            result.put("success", false);
            result.put("message", "部门ID不能为空");
            return result;
        }
        if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "部门名称不能为空");
            return result;
        }
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getDeptId())) {
            result.put("success", false);
            result.put("message", "父级部门不能是自己");
            return result;
        }
        deptRepository.update(dept);
        result.put("success", true);
        result.put("message", "修改成功");
        return result;
    }

    public Map<String, Object> delete(Integer deptId) {
        Map<String, Object> result = new HashMap<>();
        int childCount = deptRepository.countByParentId(deptId);
        if (childCount > 0) {
            result.put("success", false);
            result.put("message", "存在子部门，不允许删除");
            return result;
        }
        int userCount = userRepository.countByDeptId(deptId);
        if (userCount > 0) {
            result.put("success", false);
            result.put("message", "该部门下存在用户，不允许删除");
            return result;
        }
        deptRepository.delete(deptId);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> saveSort(Integer deptId, Integer sort) {
        Map<String, Object> result = new HashMap<>();
        deptRepository.updateSort(deptId, sort);
        result.put("success", true);
        result.put("message", "排序保存成功");
        return result;
    }

    public Map<String, Object> changeStatus(Integer deptId, String status) {
        Map<String, Object> result = new HashMap<>();
        deptRepository.updateStatus(deptId, status);
        result.put("success", true);
        result.put("message", "状态更新成功");
        return result;
    }
}
