package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    private User requireLogin(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(required = false) String postCode,
            @RequestParam(required = false) String postCodeMatch,
            @RequestParam(required = false) String postName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        Map<String, Object> pageData = postService.findByPage(postCode, postCodeMatch, postName, status, pageNum, pageSize);
        result.put("success", true);
        result.put("data", pageData);
        return result;
    }
}
