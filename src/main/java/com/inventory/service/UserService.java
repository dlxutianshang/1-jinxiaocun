package com.inventory.service;

import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$");

    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByPermission(Integer currentUserId, String currentRole) {
        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            return userRepository.findAll();
        } else {
            List<User> list = new ArrayList<>();
            User me = userRepository.findById(currentUserId);
            if (me != null) list.add(me);
            return list;
        }
    }

    public Map<String, Object> findByCondition(String username, String phone, String status, String startTime, String endTime, Integer currentUserId, String currentRole) {
        Map<String, Object> result = new HashMap<>();
        List<User> list;
        if ("ADMIN".equalsIgnoreCase(currentRole)) {
            list = userRepository.findByCondition(username, phone, status, startTime, endTime);
        } else {
            list = new ArrayList<>();
            User me = userRepository.findById(currentUserId);
            if (me != null) list.add(me);
        }
        result.put("list", list);
        result.put("total", list.size());
        return result;
    }

    public Map<String, Object> changeStatus(Integer userId, String status) {
        Map<String, Object> result = new HashMap<>();
        userRepository.updateStatus(userId, status);
        result.put("success", true);
        result.put("message", "状态更新成功");
        return result;
    }

    public User findDetailById(Integer id) {
        return userRepository.findById(id);
    }

    public Map<String, Object> createUser(User user) {
        Map<String, Object> result = new HashMap<>();
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名称不能为空");
            return result;
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            result.put("success", false);
            result.put("message", "用户名称已存在");
            return result;
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "密码不能为空");
            return result;
        }
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            result.put("success", false);
            result.put("message", "密码长度需8-20位，且包含大小写字母和数字");
            return result;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }
        if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
            user.setStatus("0");
        }
        if (user.getDeptId() == null) {
            user.setDeptId(0);
        }
        userRepository.save(user);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Map<String, Object> updateUser(User user, Integer currentUserId) {
        Map<String, Object> result = new HashMap<>();
        if (user.getId() == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }
        User existing = userRepository.findById(user.getId());
        if (existing == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
        if (userRepository.isSuperAdmin(user.getId()) && !user.getId().equals(currentUserId)) {
            result.put("success", false);
            result.put("message", "超级管理员信息不可被修改");
            return result;
        }
        user.setUsername(existing.getUsername());
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
                result.put("success", false);
                result.put("message", "密码长度需8-20位，且包含大小写字母和数字");
                return result;
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        if (user.getRole() == null) {
            user.setRole(existing.getRole());
        }
        if (user.getDeptId() == null) {
            user.setDeptId(existing.getDeptId());
        }
        userRepository.update(user);
        result.put("success", true);
        result.put("message", "修改成功");
        return result;
    }

    public Map<String, Object> deleteUser(Integer id, Integer currentUserId) {
        Map<String, Object> result = new HashMap<>();
        if (userRepository.isSuperAdmin(id)) {
            result.put("success", false);
            result.put("message", "超级管理员用户不可删除");
            return result;
        }
        if (id.equals(currentUserId)) {
            result.put("success", false);
            result.put("message", "不能删除当前登录用户");
            return result;
        }
        userRepository.delete(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> deleteUsersBatch(List<Integer> ids, Integer currentUserId) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> toDelete = new ArrayList<>();
        for (Integer id : ids) {
            if (userRepository.isSuperAdmin(id)) {
                result.put("success", false);
                result.put("message", "超级管理员用户不可删除，已跳过");
                return result;
            }
            if (id.equals(currentUserId)) {
                result.put("success", false);
                result.put("message", "不能删除当前登录用户，已跳过");
                return result;
            }
            toDelete.add(id);
        }
        if (!toDelete.isEmpty()) {
            userRepository.deleteBatch(toDelete);
        }
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> checkUsername(String username, Integer excludeId) {
        Map<String, Object> result = new HashMap<>();
        boolean exists;
        if (excludeId != null) {
            exists = userRepository.findByUsernameExcludeId(username, excludeId) != null;
        } else {
            exists = userRepository.findByUsername(username) != null;
        }
        result.put("exists", exists);
        result.put("success", true);
        return result;
    }
}
