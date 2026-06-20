package com.inventory.service;

import com.inventory.model.Role;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Map<String, Object> findByCondition(String roleName, String roleKey, String status, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        List<Role> list = roleRepository.findByCondition(roleName, roleKey, status, startTime, endTime);
        result.put("list", list);
        result.put("total", list.size());
        return result;
    }

    public Role findById(Integer id) {
        return roleRepository.findById(id);
    }

    public Map<String, Object> changeStatus(Integer roleId, String status) {
        Map<String, Object> result = new HashMap<>();
        if (roleRepository.isSuperAdmin(roleId)) {
            result.put("success", false);
            result.put("message", "超级管理员角色状态不可修改");
            return result;
        }
        roleRepository.updateStatus(roleId, status);
        result.put("success", true);
        result.put("message", "状态更新成功");
        return result;
    }

    public Map<String, Object> createRole(Role role) {
        Map<String, Object> result = new HashMap<>();
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "角色名称不能为空");
            return result;
        }
        if (role.getRoleKey() == null || role.getRoleKey().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "权限字符不能为空");
            return result;
        }
        if (roleRepository.findByRoleKey(role.getRoleKey()) != null) {
            result.put("success", false);
            result.put("message", "权限字符已存在");
            return result;
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }
        if (role.getStatus() == null || role.getStatus().trim().isEmpty()) {
            role.setStatus("0");
        }
        roleRepository.save(role);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Map<String, Object> updateRole(Role role) {
        Map<String, Object> result = new HashMap<>();
        if (role.getId() == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        Role existing = roleRepository.findById(role.getId());
        if (existing == null) {
            result.put("success", false);
            result.put("message", "角色不存在");
            return result;
        }
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "角色名称不能为空");
            return result;
        }
        if (role.getRoleKey() == null || role.getRoleKey().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "权限字符不能为空");
            return result;
        }
        if (roleRepository.findByRoleKeyExcludeId(role.getRoleKey(), role.getId()) != null) {
            result.put("success", false);
            result.put("message", "权限字符已存在");
            return result;
        }
        if (roleRepository.isSuperAdmin(role.getId())) {
            if (!"0".equals(role.getStatus())) {
                result.put("success", false);
                result.put("message", "超级管理员角色不可禁用");
                return result;
            }
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }
        roleRepository.update(role);
        result.put("success", true);
        result.put("message", "修改成功");
        return result;
    }

    public Map<String, Object> deleteRole(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Role role = roleRepository.findById(id);
        if (role == null) {
            result.put("success", false);
            result.put("message", "角色不存在");
            return result;
        }
        if (roleRepository.isSuperAdmin(id)) {
            result.put("success", false);
            result.put("message", "超级管理员角色不可删除");
            return result;
        }
        int userCount = roleRepository.countByRoleKey(role.getRoleKey());
        if (userCount > 0) {
            result.put("success", false);
            result.put("message", "该角色下存在用户，不允许删除");
            return result;
        }
        roleRepository.delete(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> deleteRolesBatch(List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> toDelete = new ArrayList<>();
        for (Integer id : ids) {
            if (roleRepository.isSuperAdmin(id)) {
                result.put("success", false);
                result.put("message", "超级管理员角色不可删除");
                return result;
            }
            Role role = roleRepository.findById(id);
            if (role != null) {
                int userCount = roleRepository.countByRoleKey(role.getRoleKey());
                if (userCount > 0) {
                    result.put("success", false);
                    result.put("message", "角色【" + role.getName() + "】下存在用户，不允许删除");
                    return result;
                }
            }
            toDelete.add(id);
        }
        if (!toDelete.isEmpty()) {
            roleRepository.deleteBatch(toDelete);
        }
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> checkRoleKey(String roleKey, Integer excludeId) {
        Map<String, Object> result = new HashMap<>();
        boolean exists;
        if (excludeId != null) {
            exists = roleRepository.findByRoleKeyExcludeId(roleKey, excludeId) != null;
        } else {
            exists = roleRepository.findByRoleKey(roleKey) != null;
        }
        result.put("exists", exists);
        result.put("success", true);
        return result;
    }
}
