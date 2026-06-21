package com.inventory.repository;

import com.inventory.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final RowMapper<Post> rowMapper = (rs, rowNum) -> {
        Post p = new Post();
        p.setId(rs.getInt("id"));
        p.setPostCode(rs.getString("post_code"));
        p.setPostName(rs.getString("post_name"));
        p.setSort(rs.getInt("sort"));
        p.setStatus(rs.getString("status"));
        p.setRemark(rs.getString("remark"));
        Timestamp ts = rs.getTimestamp("create_time");
        p.setCreateTime(ts != null ? sdf.format(ts) : "");
        return p;
    };

    private List<Object> buildConditionParams(String postCode, String postCodeMatch, String postName, String status, List<Object> params) {
        if (postCode != null && !postCode.trim().isEmpty()) {
            if ("exact".equals(postCodeMatch)) {
                params.add(postCode.trim());
            } else {
                params.add("%" + postCode.trim() + "%");
            }
        }
        if (postName != null && !postName.trim().isEmpty()) {
            params.add("%" + postName.trim() + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            params.add(status);
        }
        return params;
    }

    private String buildConditionSql(String postCode, String postCodeMatch, String postName, String status) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1");
        if (postCode != null && !postCode.trim().isEmpty()) {
            if ("exact".equals(postCodeMatch)) {
                sql.append(" AND post_code = ?");
            } else {
                sql.append(" AND post_code LIKE ?");
            }
        }
        if (postName != null && !postName.trim().isEmpty()) {
            sql.append(" AND post_name LIKE ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }
        return sql.toString();
    }

    public int countByCondition(String postCode, String postCodeMatch, String postName, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_post");
        sql.append(buildConditionSql(postCode, postCodeMatch, postName, status));
        List<Object> params = buildConditionParams(postCode, postCodeMatch, postName, status, new ArrayList<>());
        return jdbc.queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    public List<Post> findByCondition(String postCode, String postCodeMatch, String postName, String status, int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_post");
        sql.append(buildConditionSql(postCode, postCodeMatch, postName, status));
        sql.append(" ORDER BY sort ASC, id ASC LIMIT ?, ?");
        List<Object> params = buildConditionParams(postCode, postCodeMatch, postName, status, new ArrayList<>());
        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);
        return jdbc.query(sql.toString(), rowMapper, params.toArray());
    }

    public Post findById(Integer id) {
        List<Post> list = jdbc.query("SELECT * FROM t_post WHERE id = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Post findByPostCode(String postCode) {
        List<Post> list = jdbc.query("SELECT * FROM t_post WHERE post_code = ?", rowMapper, postCode);
        return list.isEmpty() ? null : list.get(0);
    }

    public Post findByPostCodeExcludeId(String postCode, Integer excludeId) {
        List<Post> list = jdbc.query("SELECT * FROM t_post WHERE post_code = ? AND id != ?", rowMapper, postCode, excludeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Post post) {
        return jdbc.update("INSERT INTO t_post (post_code, post_name, sort, status, remark) VALUES (?, ?, ?, ?, ?)",
                post.getPostCode(),
                post.getPostName(),
                post.getSort() == null ? 0 : post.getSort(),
                post.getStatus() == null ? "0" : post.getStatus(),
                post.getRemark());
    }

    public int update(Post post) {
        return jdbc.update("UPDATE t_post SET post_name = ?, sort = ?, status = ?, remark = ? WHERE id = ?",
                post.getPostName(),
                post.getSort() == null ? 0 : post.getSort(),
                post.getStatus() == null ? "0" : post.getStatus(),
                post.getRemark(),
                post.getId());
    }

    public int updateStatus(Integer id, String status) {
        return jdbc.update("UPDATE t_post SET status = ? WHERE id = ?", status, id);
    }

    public int delete(Integer id) {
        return jdbc.update("DELETE FROM t_post WHERE id = ?", id);
    }

    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        return jdbc.update("DELETE FROM t_post WHERE id IN (" + placeholders + ")", ids.toArray());
    }

    public int countByPostId(Integer postId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM t_user WHERE post_id = ?", Integer.class, postId);
        return count != null ? count : 0;
    }

    public int getMaxSort() {
        try {
            Integer maxSort = jdbc.queryForObject("SELECT MAX(sort) FROM t_post", Integer.class);
            return maxSort != null ? maxSort : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
