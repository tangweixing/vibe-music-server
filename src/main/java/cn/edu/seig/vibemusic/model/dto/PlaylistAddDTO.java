package cn.edu.seig.vibemusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * 创建歌单请求DTO
 */
@Data
@Schema(description = "创建歌单请求参数")
public class PlaylistAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌单标题
     */
    @NotBlank(message = "歌单标题不能为空")
    private String title;

    /**
     * 是否公开
     */
    @NotNull(message = "公开状态不能为空")
    private Boolean isPublic;

    /**
     * 歌单风格
     */
    private String style;

    /**
     * 歌单简介
     */
    private String introduction;

    /**
     * 封面图片URL（可选）
     */
    private String coverUrl;

}
