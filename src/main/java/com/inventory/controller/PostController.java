package com.inventory.controller;

import com.inventory.model.Post;
import com.inventory.model.User;
import com.inventory.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
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

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        Post post = postService.getById(id);
        if (post == null) {
            result.put("success", false);
            result.put("message", "岗位不存在");
        } else {
            result.put("success", true);
            result.put("data", post);
        }
        return result;
    }

    @GetMapping("/nextSort")
    public Map<String, Object> getNextSort(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        result.put("success", true);
        result.put("data", postService.getNextSort());
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Post post, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return postService.create(post);
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Post post, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return postService.update(post);
    }

    @PutMapping("/status")
    public Map<String, Object> updateStatus(@RequestParam Integer id, @RequestParam String status, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return postService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return postService.delete(id);
    }

    @DeleteMapping("/batch")
    public Map<String, Object> deleteBatch(@RequestBody List<Integer> ids, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = requireLogin(session);
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        return postService.deleteBatch(ids);
    }
}
