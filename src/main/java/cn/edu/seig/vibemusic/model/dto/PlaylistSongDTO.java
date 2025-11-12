package cn.edu.seig.vibemusic.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlaylistSongDTO {
    @NotNull(message = "歌单ID不能为空")
    private Long playlistId;
    
    @NotNull(message = "歌曲ID列表不能为空")
    private List<Long> songIds;
}
