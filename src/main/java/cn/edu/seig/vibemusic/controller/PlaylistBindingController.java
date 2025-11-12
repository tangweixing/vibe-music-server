
package cn.edu.seig.vibemusic.controller;

import cn.edu.seig.vibemusic.model.dto.PlaylistSongDTO;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IPlaylistBindingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 歌曲与歌单绑定相关接口
 *
 * @author TANG
 * @since 2025-11-12
 */

@RestController
@RequestMapping("/playlist-binding")
@Tag(name = "歌单歌曲绑定相关接口", description = "歌单歌曲绑定相关接口")
public class PlaylistBindingController {

    @Autowired
    private IPlaylistBindingService playlistBindingService;

    /**
     * 添加歌曲到歌单
     */
    @PostMapping("/add-songs")
    @Operation(summary = "添加歌曲到歌单", description = "批量添加歌曲到指定歌单")
    public Result addSongsToPlaylist(
            @RequestBody @Valid @Parameter(description = "歌单ID和歌曲ID列表", required = true) PlaylistSongDTO dto) {
        return playlistBindingService.addSongsToPlaylist(dto);
    }

    /**
     * 从歌单删除歌曲
     */
    @PostMapping("/remove-songs")
    @Operation(summary = "从歌单删除歌曲", description = "批量从指定歌单删除歌曲")
    public Result removeSongsFromPlaylist(
            @RequestBody @Valid @Parameter(description = "歌单ID和歌曲ID列表", required = true) PlaylistSongDTO dto) {
        return playlistBindingService.removeSongsFromPlaylist(dto);
    }

    /**
     * 清空歌单所有歌曲
     */
    @DeleteMapping("/clear/{playlistId}")
    @Operation(summary = "清空歌单", description = "删除指定歌单中的所有歌曲")
    public Result clearPlaylistSongs(
            @PathVariable @Parameter(description = "歌单ID", required = true) Long playlistId) {
        return playlistBindingService.clearPlaylistSongs(playlistId);
    }

}