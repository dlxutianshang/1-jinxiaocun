package com.inventory.service;

import com.inventory.model.Post;
import com.inventory.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    private static final Pattern POST_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

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

    public Post getById(Integer id) {
        return postRepository.findById(id);
    }

    public int getNextSort() {
        return postRepository.getMaxSort() + 1;
    }

    public Map<String, Object> create(Post post) {
        Map<String, Object> result = new HashMap<>();

        if (post.getPostCode() == null || post.getPostCode().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "岗位编码不能为空");
            return result;
        }

        String postCode = post.getPostCode().trim();
        if (!POST_CODE_PATTERN.matcher(postCode).matches()) {
            result.put("success", false);
            result.put("message", "岗位编码仅支持字母、数字、下划线");
            return result;
        }

        if (post.getPostName() == null || post.getPostName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "岗位名称不能为空");
            return result;
        }

        if (postRepository.findByPostCode(postCode) != null) {
            result.put("success", false);
            result.put("message", "岗位编码已存在");
            return result;
        }

        post.setPostCode(postCode);
        post.setPostName(post.getPostName().trim());
        if (post.getSort() == null) {
            post.setSort(getNextSort());
        }

        postRepository.save(post);
        result.put("success", true);
        result.put("message", "新增成功");
        return result;
    }

    public Map<String, Object> update(Post post) {
        Map<String, Object> result = new HashMap<>();

        if (post.getId() == null) {
            result.put("success", false);
            result.put("message", "岗位ID不能为空");
            return result;
        }

        Post existPost = postRepository.findById(post.getId());
        if (existPost == null) {
            result.put("success", false);
            result.put("message", "岗位不存在");
            return result;
        }

        if (post.getPostName() == null || post.getPostName().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "岗位名称不能为空");
            return result;
        }

        post.setPostName(post.getPostName().trim());
        post.setPostCode(existPost.getPostCode());

        int userCount = postRepository.countByPostId(post.getId());
        boolean statusChanged = post.getStatus() != null && !post.getStatus().equals(existPost.getStatus());

        postRepository.update(post);
        result.put("success", true);
        result.put("message", "修改成功");
        result.put("hasUsers", userCount > 0);
        result.put("statusChanged", statusChanged);
        if (userCount > 0 && statusChanged) {
            result.put("warnMsg", "该岗位已关联用户，状态变更将影响用户权限");
        }
        return result;
    }

    public Map<String, Object> updateStatus(Integer id, String status) {
        Map<String, Object> result = new HashMap<>();

        Post existPost = postRepository.findById(id);
        if (existPost == null) {
            result.put("success", false);
            result.put("message", "岗位不存在");
            return result;
        }

        postRepository.updateStatus(id, status);
        int userCount = postRepository.countByPostId(id);
        result.put("success", true);
        result.put("message", "状态更新成功");
        result.put("hasUsers", userCount > 0);
        if (userCount > 0 && "1".equals(status)) {
            result.put("warnMsg", "该岗位已关联用户，禁用将影响用户权限");
        }
        return result;
    }

    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new HashMap<>();

        Post post = postRepository.findById(id);
        if (post == null) {
            result.put("success", false);
            result.put("message", "岗位不存在");
            return result;
        }

        int userCount = postRepository.countByPostId(id);
        if (userCount > 0) {
            result.put("success", false);
            result.put("message", "该岗位已关联用户，无法删除");
            return result;
        }

        if ("普通员工".equals(post.getPostName())) {
            result.put("success", false);
            result.put("message", "系统内置核心岗位不可删除");
            return result;
        }

        postRepository.delete(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    public Map<String, Object> deleteBatch(List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();

        if (ids == null || ids.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的岗位");
            return result;
        }

        StringBuilder errorMsg = new StringBuilder();
        for (Integer id : ids) {
            Post post = postRepository.findById(id);
            if (post == null) continue;

            int userCount = postRepository.countByPostId(id);
            if (userCount > 0) {
                if (errorMsg.length() > 0) errorMsg.append("；");
                errorMsg.append("岗位[").append(post.getPostName()).append("]已关联用户，无法删除");
                continue;
            }

            if ("普通员工".equals(post.getPostName())) {
                if (errorMsg.length() > 0) errorMsg.append("；");
                errorMsg.append("岗位[").append(post.getPostName()).append("]为系统内置岗位，不可删除");
                continue;
            }

            postRepository.delete(id);
        }

        if (errorMsg.length() > 0) {
            result.put("success", false);
            result.put("message", errorMsg.toString());
        } else {
            result.put("success", true);
            result.put("message", "删除成功");
        }
        return result;
    }
}
