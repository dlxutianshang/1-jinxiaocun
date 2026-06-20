package com.inventory.model;

public class Role {
    private Integer id;
    private String name;
    private String roleKey;
    private Integer sort;
    private String status;
    private String createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoleKey() { return roleKey; }
    public void setRoleKey(String roleKey) { this.roleKey = roleKey; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
