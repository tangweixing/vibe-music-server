-- 为原歌单表新增字段
ALTER TABLE `tb_playlist`
ADD COLUMN `user_id` bigint NOT NULL COMMENT '创建者用户ID' AFTER `id`,
ADD COLUMN `is_public` tinyint NOT NULL DEFAULT 1 COMMENT '是否公开（0-私有，1-公开）' AFTER `cover_url`,
ADD COLUMN `song_count` int NOT NULL DEFAULT 0 COMMENT '歌曲数量' AFTER `is_public`,
ADD COLUMN `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `song_count`,
ADD COLUMN `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
-- 新增用户ID索引（优化查询性能）
ADD INDEX `idx_user_id` (`user_id`);

-- 计算每个歌单的歌曲数量，并更新到歌单表的 song_count 字段
UPDATE tb_playlist p
SET p.song_count = (
    SELECT COUNT(*) 
    FROM tb_playlist_binding tpb 
    WHERE tpb.playlist_id = p.id
)
WHERE 1 = 1; -- 可根据需要添加歌单ID条件，如 `WHERE p.id IN (1,2,3)`

-- 将歌单表中所有记录的 user_id 统一设置为 148
UPDATE tb_playlist 
SET user_id = 148;