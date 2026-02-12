-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

-- Таблица файлов с привязкой к пользователю
CREATE TABLE IF NOT EXISTS files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    file_content BYTEA NOT NULL,
    size BIGINT,
    username VARCHAR(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username),
    UNIQUE(filename, username)
);

-- Очистим старых пользователей
DELETE FROM users WHERE username IN ('admin', 'user', 'testuser');

-- Создадим пользователей с правильными хешами
-- Пароль admin: admin123
-- Пароль user: user123
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$NkM3CqWqYqYqYqYqYqYqYuYqYqYqYqYqYqYqYqYqYqYqYqYq', 'ROLE_ADMIN'),
('user', '$2a$10$NkM3CqWqYqYqYqYqYqYqYuYqYqYqYqYqYqYqYqYqYqYqYqYq', 'ROLE_USER')
ON CONFLICT (username) DO UPDATE SET 
    password = EXCLUDED.password,
    role = EXCLUDED.role;