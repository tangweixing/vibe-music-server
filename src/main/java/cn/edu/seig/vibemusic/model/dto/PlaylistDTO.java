package cn.edu.seig.vibemusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * 歌单响应DTO
 */
@Data
@Schema(description = "歌单响应数据")
public class PlaylistDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    @NotNull
    private Integer pageNum;

    /**
     * 每页数量
     */
    @NotNull
    private Integer pageSize;
    /**
     * 歌单ID
     */
    @Schema(description = "歌单ID", example = "1001")
    private Long id;
/**
     * 创建者用户ID
     */
    @Schema(description = "创建者用户ID", example = "148")
    private Long userId;
/**
     * 创建者用户名
     */
    @Schema(description = "创建者用户名", example = "张三")
    private String username; // 从用户表关联查询

    /**
     * 歌单标题
     */
//    @NotBlank(message = "歌单名称不能为空")
    @Size(max = 255, message = "歌单名称不能超过255个字符")
    @Schema(description = "歌单名称", example = "我的私人歌单")
    private String title;
    /**
     * 歌单介绍
     */
    @Schema(description = "歌单介绍（可选）", example = "收藏我喜欢的歌曲")
    private String introduction;
    /**
     * 歌单风格
     */
    @Schema(description = "歌单风格（可选）", example = "流行")
    private String style;
    /**
     * 歌单封面URL
     */
    @Schema(description = "歌单封面URL（可选）", example = "https://xxx.com/cover.jpg")
    private String coverUrl;
/**
     * 是否公开（0-私有，1-公开，默认1）
     */
    @Schema(description = "是否公开（0-私有，1-公开，默认1）", example = "1")
    private Integer isPublic = 1;
    /**
     * 歌曲数量
     */
    @Schema(description = "歌曲数量", example = "20")
    private Integer songCount;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-11-11 10:00:00")
    private LocalDateTime createTime;
}
