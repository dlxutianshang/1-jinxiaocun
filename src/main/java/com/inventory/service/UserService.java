package com.inventory.service;

import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
