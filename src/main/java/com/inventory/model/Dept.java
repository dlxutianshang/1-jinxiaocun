package com.inventory.model;

import java.util.List;

public class Dept {
    private Integer id;
    private Integer parentId;
    private String name;
    private Integer orderNum;
    private String status;
    private String createTime;
    private List<Dept> children;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public List<Dept> getChildren() { return children; }
    public void setChildren(List<Dept> children) { this.children = children; }
}
