CREATE TABLE IF NOT EXISTS t_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    dept_id INT DEFAULT 0,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    nickname VARCHAR(50),
    real_name VARCHAR(50),
    phone VARCHAR(20),
    status VARCHAR(10) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role_key VARCHAR(50) NOT NULL UNIQUE,
    sort INT DEFAULT 0,
    status VARCHAR(10) DEFAULT '0',
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO t_role (name, role_key, sort, status, remark) VALUES ('超级管理员', 'ADMIN', 1, '0', '拥有全部权限');
INSERT INTO t_role (name, role_key, sort, status, remark) VALUES ('普通用户', 'USER', 2, '0', '基础访问权限');

INSERT INTO t_user (username, password, email, role, nickname, real_name, phone, status) VALUES ('admin', '$2a$10$DozlqC8vM/s8N8t30Ji4UuHCI/GLTb9KuF.PKbJfw/cF5/e9uxk9q', 'admin@inventory.com', 'ADMIN', '超级管理员', '系统管理员', '13800138000', '0');

CREATE TABLE IF NOT EXISTS t_dept (
    id INT AUTO_INCREMENT PRIMARY KEY,
    parent_id INT DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    leader VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    order_num INT DEFAULT 0,
    status VARCHAR(10) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (0, '若依科技', 1, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (1, '深圳总公司', 1, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (1, '长沙分公司', 2, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (2, '研发部门', 1, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (2, '市场部门', 2, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (2, '测试部门', 3, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (2, '财务部门', 4, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (2, '运维部门', 5, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (3, '市场部门', 1, '0');
INSERT INTO t_dept (parent_id, name, order_num, status) VALUES (3, '财务部门', 2, '0');
