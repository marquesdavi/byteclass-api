-- marquesdavi:1.0
CREATE TABLE tb_course (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    title varchar(50) NOT NULL,
    description varchar(255) NOT NULL,
    instructor_id bigint(20) NOT NULL,
    status enum('BUILDING', 'PUBLISHED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'BUILDING',
    published_at datetime DEFAULT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_author FOREIGN KEY (instructor_id) REFERENCES tb_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;