package com.inventory.service;

import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
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
}
