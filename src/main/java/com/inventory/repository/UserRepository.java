package com.inventory.repository;

import com.inventory.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
        return u;
    };

    public int save(User user) {
        return jdbc.update("INSERT INTO t_user (username, password, email) VALUES (?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getEmail());
    }

    public User findByUsername(String username) {
        List<User> list = jdbc.query("SELECT * FROM t_user WHERE username = ?", rowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAll() {
        return jdbc.query("SELECT * FROM t_user", rowMapper);
    }
}
