-- marquesdavi:1.0
CREATE TABLE tb_task (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    course_id BIGINT(20) NOT NULL,
    statement VARCHAR(80) NOT NULL,
    task_order INT(11) NOT NULL,
    task_type VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT UNQ_STATEMENT UNIQUE (statement),
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES tb_course (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- marquesdavi:1.1
CREATE INDEX idx_task_course ON tb_task(course_id);
CREATE INDEX idx_task_course_order ON tb_task(course_id, task_order);