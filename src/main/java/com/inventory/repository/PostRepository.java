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
        Timestamp ts = rs.getTimestamp("create_time");
        p.setCreateTime(ts != null ? sdf.format(ts) : "");
        return p;
    };

    private List<Object> buildConditionParams(String postCode, String postName, String status, List<Object> params) {
        if (postCode != null && !postCode.trim().isEmpty()) {
            params.add("%" + postCode + "%");
        }
        if (postName != null && !postName.trim().isEmpty()) {
            params.add("%" + postName + "%");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            params.add(status);
        }
        return params;
    }

    private String buildConditionSql(String postCode, String postName, String status) {
        StringBuilder sql = new StringBuilder(" WHERE 1=1");
        if (postCode != null && !postCode.trim().isEmpty()) {
            sql.append(" AND post_code LIKE ?");
        }
        if (postName != null && !postName.trim().isEmpty()) {
            sql.append(" AND post_name LIKE ?");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = ?");
        }
        return sql.toString();
    }

    public int countByCondition(String postCode, String postName, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_post");
        sql.append(buildConditionSql(postCode, postName, status));
        List<Object> params = buildConditionParams(postCode, postName, status, new ArrayList<>());
        return jdbc.queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    public List<Post> findByCondition(String postCode, String postName, String status, int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_post");
        sql.append(buildConditionSql(postCode, postName, status));
        sql.append(" ORDER BY sort ASC, id ASC LIMIT ?, ?");
        List<Object> params = buildConditionParams(postCode, postName, status, new ArrayList<>());
        params.add((pageNum - 1) * pageSize);
        params.add(pageSize);
        return jdbc.query(sql.toString(), rowMapper, params.toArray());
    }
}
