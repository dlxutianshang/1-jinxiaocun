package com.inventory.service;

import com.inventory.model.Post;
import com.inventory.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Map<String, Object> findByPage(String postCode, String postCodeMatch, String postName, String status, int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        int total = postRepository.countByCondition(postCode, postCodeMatch, postName, status);
        List<Post> list = postRepository.findByCondition(postCode, postCodeMatch, postName, status, pageNum, pageSize);
        result.put("total", total);
        result.put("list", list);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }
}
