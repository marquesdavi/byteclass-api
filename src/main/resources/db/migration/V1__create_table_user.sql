-- marquesdavi:1.0
CREATE TABLE tb_user(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(70) NOT NULL,
    email varchar(50) NOT NULL,
    role enum('ADMIN','STUDENT', 'INSTRUCTOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'STUDENT',
    password varchar(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT UC_Email UNIQUE (email)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- marquesdavi:1.1
CREATE UNIQUE INDEX ux_users_email ON tb_user(email);
