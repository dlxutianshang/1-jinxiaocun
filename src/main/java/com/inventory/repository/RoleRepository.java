package com.inventory.repository;

import com.inventory.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final RowMapper<Role> rowMapper = (rs, rowNum) -> {
        Role r = new Role();
        r.setId(rs.getInt("id"));
        r.setName(rs.getString("name"));
        r.setRoleKey(rs.getString("role_key"));
        r.setSort(rs.getInt("sort"));
        r.setStatus(rs.getString("status"));
        r.setRemark(rs.getString("remark"));
        Timestamp ts = rs.getTimestamp("create_time");
        r.setCreateTime(ts != null ? sdf.format(ts) : "");
        return r;
    };

    public List<Role> findAll() {
        return jdbc.query("SELECT * FROM t_role ORDER BY sort ASC, id ASC", rowMapper);
    }

    public List<Role> findByCondition(String roleName, String roleKey, String status, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_role WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (roleName != null && !roleName.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + roleName + "%");
        }
        if (roleKey != null && !roleKey.trim().isEmpty()) {
            sql.append(" AND role_key LIKE ?");
            params.add("%" + roleKey + "%");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (startTime != null && !startTime.trim().isEmpty()) {
            sql.append(" AND create_time >= ?");
            params.add(startTime + " 00:00:00");
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            sql.append(" AND create_time <= ?");
            params.add(endTime + " 23:59:59");
        }
        sql.append(" ORDER BY sort ASC, id ASC");

        return jdbc.query(sql.toString(), rowMapper, params.toArray());
    }

    public Role findById(Integer id) {
        List<Role> list = jdbc.query("SELECT * FROM t_role WHERE id = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Role findByName(String name) {
        List<Role> list = jdbc.query("SELECT * FROM t_role WHERE name = ?", rowMapper, name);
        return list.isEmpty() ? null : list.get(0);
    }

    public Role findByRoleKey(String roleKey) {
        List<Role> list = jdbc.query("SELECT * FROM t_role WHERE role_key = ?", rowMapper, roleKey);
        return list.isEmpty() ? null : list.get(0);
    }

    public Role findByRoleKeyExcludeId(String roleKey, Integer excludeId) {
        List<Role> list = jdbc.query("SELECT * FROM t_role WHERE role_key = ? AND id != ?", rowMapper, roleKey, excludeId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Role role) {
        return jdbc.update("INSERT INTO t_role (name, role_key, sort, status, remark) VALUES (?, ?, ?, ?, ?)",
                role.getName(),
                role.getRoleKey(),
                role.getSort() == null ? 0 : role.getSort(),
                role.getStatus() == null ? "0" : role.getStatus(),
                role.getRemark());
    }

    public int update(Role role) {
        return jdbc.update("UPDATE t_role SET name = ?, role_key = ?, sort = ?, status = ?, remark = ? WHERE id = ?",
                role.getName(),
                role.getRoleKey(),
                role.getSort() == null ? 0 : role.getSort(),
                role.getStatus() == null ? "0" : role.getStatus(),
                role.getRemark(),
                role.getId());
    }

    public int updateStatus(Integer id, String status) {
        return jdbc.update("UPDATE t_role SET status = ? WHERE id = ?", status, id);
    }

    public int delete(Integer id) {
        return jdbc.update("DELETE FROM t_role WHERE id = ?", id);
    }

    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        return jdbc.update("DELETE FROM t_role WHERE id IN (" + placeholders + ")", ids.toArray());
    }

    public boolean isSuperAdmin(Integer id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM t_role WHERE id = ? AND role_key = 'ADMIN'", Integer.class, id);
        return count != null && count > 0;
    }

    public int countByRoleKey(String roleKey) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM t_user WHERE role = ?", Integer.class, roleKey);
    }
}
