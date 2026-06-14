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
        d.setId(rs.getInt("id"));
        d.setParentId(rs.getInt("parent_id"));
        d.setName(rs.getString("name"));
        d.setOrderNum(rs.getInt("order_num"));
        d.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("create_time");
        d.setCreateTime(ts != null ? sdf.format(ts) : "");
        return d;
    };

    public List<Dept> findAll() {
        return jdbc.query("SELECT * FROM t_dept ORDER BY order_num ASC, id ASC", rowMapper);
    }

    public List<Dept> findByCondition(String name, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM t_dept WHERE 1=1");
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND name LIKE '%").append(name.trim()).append("%'");
        }
        if (status != null && !status.trim().isEmpty() && !"all".equals(status)) {
            sql.append(" AND status = '").append(status).append("'");
        }
        sql.append(" ORDER BY order_num ASC, id ASC");
        return jdbc.query(sql.toString(), rowMapper);
    }

    public Dept findById(Integer id) {
        List<Dept> list = jdbc.query("SELECT * FROM t_dept WHERE id = ?", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Dept dept) {
        return jdbc.update("INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (?, ?, ?, ?)",
                dept.getParentId() == null ? 0 : dept.getParentId(),
                dept.getName(),
                dept.getOrderNum() == null ? 0 : dept.getOrderNum(),
                dept.getStatus() == null ? "0" : dept.getStatus());
    }

    public int update(Dept dept) {
        return jdbc.update("UPDATE t_dept SET parent_id = ?, name = ?, order_num = ?, status = ? WHERE id = ?",
                dept.getParentId() == null ? 0 : dept.getParentId(),
                dept.getName(),
                dept.getOrderNum() == null ? 0 : dept.getOrderNum(),
                dept.getStatus() == null ? "0" : dept.getStatus(),
                dept.getId());
    }

    public int updateOrderNum(Integer id, Integer orderNum) {
        return jdbc.update("UPDATE t_dept SET order_num = ? WHERE id = ?", orderNum, id);
    }

    public int delete(Integer id) {
        return jdbc.update("DELETE FROM t_dept WHERE id = ?", id);
    }

    public int countByParentId(Integer parentId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM t_dept WHERE parent_id = ?", Integer.class, parentId);
    }
}
