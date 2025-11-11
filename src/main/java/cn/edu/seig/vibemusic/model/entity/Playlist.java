package cn.edu.seig.vibemusic.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_playlist")
public class Playlist implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌单 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long playlistId;
    /**
     * 用户 id
     */
    @TableId(value = "user_id")
    private Long userId;
    /**
     * 歌单标题
     */
    @TableField("title")
    private String title;
    /**
     * 是否公开
     */
    @TableField("is_public")
    private Boolean isPublic;
    /**
     * 歌曲数量
     */
    @TableField("song_count")
    private String songCount;
    /**
     * 歌单封面
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 歌单简介
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 歌单风格
     */
    @TableField("style")
    private String style;
    /**
     *  歌单修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private LocalDateTime updateTime;
    /**
     * 歌单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;
}
