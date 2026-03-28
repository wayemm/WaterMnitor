-- 创建数据库
CREATE DATABASE IF NOT EXISTS water_monitor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE water_monitor;

-- 监测站表
CREATE TABLE IF NOT EXISTS station (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL COMMENT '站点名称',
    location    VARCHAR(128) COMMENT '地理位置描述',
    warn_level  DECIMAL(6,2) NOT NULL COMMENT '警戒水位(m)',
    danger_level DECIMAL(6,2) NOT NULL COMMENT '危险水位(m)',
    status      TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=正常 0=停用',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 水位记录表
CREATE TABLE IF NOT EXISTS water_record (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id  BIGINT NOT NULL COMMENT '监测站ID',
    water_level DECIMAL(6,2) NOT NULL COMMENT '当前水位(m)',
    recorded_at DATETIME NOT NULL COMMENT '采集时间',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_station_id (station_id),
    INDEX idx_recorded_at (recorded_at)
);

-- 警报记录表
CREATE TABLE IF NOT EXISTS alert_record (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id  BIGINT NOT NULL COMMENT '监测站ID',
    record_id   BIGINT NOT NULL COMMENT '触发的水位记录ID',
    alert_type  TINYINT NOT NULL COMMENT '警报类型: 1=警戒 2=危险',
    water_level DECIMAL(6,2) NOT NULL COMMENT '触发时水位',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0=未处理 1=已处理',
    handled_at  DATETIME COMMENT '处理时间',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_station_id (station_id),
    INDEX idx_status (status)
);

-- 插入测试数据
INSERT INTO station (name, location, warn_level, danger_level, status) VALUES
('一号监测站', '城东河道A段', 3.50, 5.00, 1),
('二号监测站', '城南河道B段', 4.00, 6.00, 1),
('三号监测站', '城西河道C段', 2.50, 4.00, 1);
