package com.inventory.repository;

import com.inventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setDeptId(rs.getInt("dept_id"));
        u.setRole(rs.getString("role"));
        u.setNickname(rs.getString("nickname"));
        u.setRealName(rs.getString("real_name"));
        u.setPhone(rs.getString("phone"));
        u.setStatus(rs.getString("status"));
        u.setCreateTime(rs.getString("create_time"));
        try {
            u.setDeptName(rs.getString("dept_name"));
        } catch (Exception e) {
        }
        return u;
    };

    public int save(User user) {
        return jdbc.update("INSERT INTO t_user (username, password, email, role, nickname, real_name, phone, status) VALUES (?, ?, ?, 'USER', ?, ?, ?, '0')",
                user.getUsername(), user.getPassword(), user.getEmail(),
                user.getNickname(), user.getRealName(), user.getPhone());
    }

    public User findByUsername(String username) {
        List<User> list = jdbc.query("SELECT * FROM t_user WHERE username = ?", rowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAll() {
        String sql = "SELECT u.*, d.name as dept_name FROM t_user u LEFT JOIN t_dept d ON u.dept_id = d.id ORDER BY u.id DESC";
        return jdbc.query(sql, rowMapper);
    }

    public User findById(Integer id) {
        String sql = "SELECT u.*, d.name as dept_name FROM t_user u LEFT JOIN t_dept d ON u.dept_id = d.id WHERE u.id = ?";
        List<User> list = jdbc.query(sql, rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int countByDeptId(Integer deptId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM t_user WHERE dept_id = ?", Integer.class, deptId);
    }

    public List<User> findByCondition(String username, String phone, String status, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT u.*, d.name as dept_name FROM t_user u LEFT JOIN t_dept d ON u.dept_id = d.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.trim().isEmpty()) {
            sql.append(" AND u.username LIKE ?");
            params.add("%" + username + "%");
        }
        if (phone != null && !phone.trim().isEmpty()) {
            sql.append(" AND u.phone LIKE ?");
            params.add("%" + phone + "%");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND u.status = ?");
            params.add(status);
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND u.create_time >= ?");
            params.add(startTime + " 00:00:00");
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND u.create_time <= ?");
            params.add(endTime + " 23:59:59");
        }
        sql.append(" ORDER BY u.id DESC");

        return jdbc.query(sql.toString(), rowMapper, params.toArray());
    }

    public int updateStatus(Integer userId, String status) {
        return jdbc.update("UPDATE t_user SET status = ? WHERE id = ?", status, userId);
    }
}
