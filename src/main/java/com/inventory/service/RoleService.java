package com.inventory.service;

import com.inventory.model.Role;
import com.inventory.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Map<String, Object> create(Role role) {
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
        if (roleRepository.findByName(role.getName().trim()) != null) {
            result.put("success", false);
            result.put("message", "角色名称已存在");
            return result;
        }
        if (roleRepository.findByRoleKey(role.getRoleKey().trim()) != null) {
            result.put("success", false);
            result.put("message", "权限字符已存在");
            return result;
        }
        role.setName(role.getName().trim());
        role.setRoleKey(role.getRoleKey().trim());
        if (role.getRemark() != null) {
            role.setRemark(role.getRemark().trim());
        }
        roleRepository.save(role);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Role findById(Integer id) {
        return roleRepository.findById(id);
    }

    public Map<String, Object> update(Role role, boolean isSuperAdmin) {
        Map<String, Object> result = new HashMap<>();
        if (role.getId() == null) {
            result.put("success", false);
            result.put("message", "角色ID不能为空");
            return result;
        }
        Role existRole = roleRepository.findById(role.getId());
        if (existRole == null) {
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
        Role roleWithSameName = roleRepository.findByName(role.getName().trim());
        if (roleWithSameName != null && !roleWithSameName.getId().equals(role.getId())) {
            result.put("success", false);
            result.put("message", "角色名称已存在");
            return result;
        }
        Role roleWithSameKey = roleRepository.findByRoleKeyExcludeId(role.getRoleKey().trim(), role.getId());
        if (roleWithSameKey != null) {
            result.put("success", false);
            result.put("message", "权限字符已存在");
            return result;
        }
        if (roleRepository.isSuperAdmin(role.getId()) && !isSuperAdmin) {
            result.put("success", false);
            result.put("message", "超级管理员角色不可被普通管理员修改");
            return result;
        }
        existRole.setName(role.getName().trim());
        existRole.setRoleKey(role.getRoleKey().trim());
        existRole.setSort(role.getSort());
        existRole.setStatus(role.getStatus());
        if (role.getRemark() != null) {
            existRole.setRemark(role.getRemark().trim());
        }
        roleRepository.update(existRole);
        result.put("success", true);
        result.put("message", "修改成功");
        return result;
    }
}
