package cn.edu.seig.vibemusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
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
     * 歌单标题
     */
    @NotBlank(message = "歌单名称不能为空")
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
}
