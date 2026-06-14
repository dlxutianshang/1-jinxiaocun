CREATE TABLE IF NOT EXISTS t_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS t_role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO t_role (name) VALUES ('ADMIN');
INSERT INTO t_role (name) VALUES ('USER');

INSERT INTO t_user (username, password, email, role) VALUES ('admin', '123456', 'admin@inventory.com', 'ADMIN');
