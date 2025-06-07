CREATE TABLE tb_user(
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    role enum('ADMIN','STUDENT', 'INSTRUCTOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'STUDENT',
    password varchar(20) NOT NULL,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT UC_Email UNIQUE (email)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX ux_users_email ON user(email);
