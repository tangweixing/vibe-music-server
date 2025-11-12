
package cn.edu.seig.vibemusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "更新歌单请求参数")
public class PlaylistUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 歌单 id
     */
    @NotNull(message = "歌单ID不能为空")
    @Schema(description = "歌单ID", example = "1001")
    private Long playlistId;

    /**
     * 歌单标题
     */
    @NotBlank(message = "歌单标题不能为空")
    @Size(max = 255, message = "歌单标题不能超过255个字符")
    @Schema(description = "歌单标题", example = "我的流行歌单")
    private String title;

    /**
     * 歌单简介
     */
    @Size(max = 1000, message = "歌单简介不能超过1000个字符")
    @Schema(description = "歌单简介", example = "收集各种流行歌曲")
    private String introduction;

    /**
     * 歌单风格
     */
    @Schema(description = "歌单风格", example = "流行")
    private String style;

    /**
     * 是否公开
     */
    @NotNull(message = "公开状态不能为空")
    @Schema(description = "是否公开", example = "true")
    private Boolean isPublic;
}