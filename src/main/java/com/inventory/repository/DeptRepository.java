package com.inventory.repository;

import com.inventory.model.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class DeptRepository {

    @Autowired
    private JdbcTemplate jdbc;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final RowMapper<Dept> rowMapper = (rs, rowNum) -> {
        Dept d = new Dept();
        d.setDeptId(rs.getInt("id"));
        d.setParentId(rs.getInt("parent_id"));
        d.setDeptName(rs.getString("name"));
        d.setLeader(rs.getString("leader"));
        d.setPhone(rs.getString("phone"));
        d.setEmail(rs.getString("email"));
        d.setSort(rs.getInt("order_num"));
        d.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("create_time");
        d.setCreateTime(ts != null ? sdf.format(ts) : "");
        return d;
    };

    public List<Dept> findAll() {
        return jdbc.query("SELECT * FROM t_dept ORDER BY order_num ASC, id ASC", rowMapper);
    }

    public List<Dept> findByCondition(String deptName, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_dept WHERE 1=1");
        if (deptName != null && !deptName.trim().isEmpty()) {
            sql.append(" AND name LIKE '%").append(deptName.trim()).append("%'");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = '").append(status).append("'");
        }
        sql.append(" ORDER BY order_num ASC, id ASC");
        return jdbc.query(sql.toString(), rowMapper);
    }

    public List<Dept> findByPage(String deptName, String status, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_dept WHERE 1=1");
        if (deptName != null && !deptName.trim().isEmpty()) {
            sql.append(" AND name LIKE '%").append(deptName.trim()).append("%'");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = '").append(status).append("'");
        }
        sql.append(" ORDER BY order_num ASC, id ASC LIMIT ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
        return jdbc.query(sql.toString(), rowMapper);
    }

    public int countByCondition(String deptName, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM t_dept WHERE 1=1");
        if (deptName != null && !deptName.trim().isEmpty()) {
            sql.append(" AND name LIKE '%").append(deptName.trim()).append("%'");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = '").append(status).append("'");
        }
        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public Dept findById(Integer id) {
        List<Dept> list = jdbc.query("SELECT * FROM t_dept WHERE id = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Dept dept) {
        return jdbc.update("INSERT INTO t_dept (parent_id, name, leader, phone, email, order_num, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
                dept.getParentId() == null ? 0 : dept.getParentId(),
                dept.getDeptName(),
                dept.getLeader(),
                dept.getPhone(),
                dept.getEmail(),
                dept.getSort() == null ? 0 : dept.getSort(),
                dept.getStatus() == null ? "0" : dept.getStatus());
    }

    public int update(Dept dept) {
        return jdbc.update("UPDATE t_dept SET parent_id = ?, name = ?, leader = ?, phone = ?, email = ?, order_num = ?, status = ? WHERE id = ?",
                dept.getParentId() == null ? 0 : dept.getParentId(),
                dept.getDeptName(),
                dept.getLeader(),
                dept.getPhone(),
                dept.getEmail(),
                dept.getSort() == null ? 0 : dept.getSort(),
                dept.getStatus() == null ? "0" : dept.getStatus(),
                dept.getDeptId());
    }

    public int updateStatus(Integer id, String status) {
        return jdbc.update("UPDATE t_dept SET status = ? WHERE id = ?", status, id);
    }

    public int updateSort(Integer id, Integer sort) {
        return jdbc.update("UPDATE t_dept SET order_num = ? WHERE id = ?", sort, id);
    }

    public int delete(Integer id) {
        return jdbc.update("DELETE FROM t_dept WHERE id = ?", id);
    }

    public int countByParentId(Integer parentId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM t_dept WHERE parent_id = ?", Integer.class, parentId);
    }
}
