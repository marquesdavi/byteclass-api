-- marquesdavi:1.0
CREATE TABLE tb_choice (
     id BIGINT(20) AUTO_INCREMENT,
     content VARCHAR(255) NOT NULL,
     is_correct  BOOLEAN NOT NULL,
     task_id BIGINT,
     is_active BOOLEAN NOT NULL DEFAULT true,
     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     PRIMARY KEY (id),
     CONSTRAINT fk_choice_task FOREIGN KEY (task_id) REFERENCES tb_task (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;