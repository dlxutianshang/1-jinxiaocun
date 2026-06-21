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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO t_role (name, role_key, sort, status) VALUES ('超级管理员', 'ADMIN', 1, '0');
INSERT INTO t_role (name, role_key, sort, status) VALUES ('普通用户', 'USER', 2, '0');

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

CREATE TABLE IF NOT EXISTS t_post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_code VARCHAR(50) NOT NULL UNIQUE,
    post_name VARCHAR(100) NOT NULL,
    sort INT DEFAULT 0,
    status VARCHAR(10) DEFAULT '0',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('ceo', '董事长', 1, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('president', '总经理', 2, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('vp', '副总经理', 3, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('hr_director', '人力资源总监', 4, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('hr_manager', '人力资源经理', 5, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('hr_supervisor', '人力资源主管', 6, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('hr_specialist', '人事专员', 7, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('hr_assistant', '人事助理', 8, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('tech_director', '技术总监', 9, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('tech_manager', '技术经理', 10, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('project_manager', '项目经理', 11, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('senior_developer', '高级开发工程师', 12, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('developer', '开发工程师', 13, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('junior_developer', '初级开发工程师', 14, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('test_manager', '测试经理', 15, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('tester', '测试工程师', 16, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('ops_manager', '运维经理', 17, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('ops_engineer', '运维工程师', 18, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('finance_manager', '财务经理', 19, '0');
INSERT INTO t_post (post_code, post_name, sort, status) VALUES ('accountant', '会计', 20, '1');
