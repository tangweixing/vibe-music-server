package cn.edu.seig.vibemusic.controller;


import cn.edu.seig.vibemusic.model.dto.PlaylistAddDTO;
import cn.edu.seig.vibemusic.model.dto.PlaylistDTO;
import cn.edu.seig.vibemusic.model.dto.PlaylistUpdateDTO;
import cn.edu.seig.vibemusic.model.entity.Playlist;
import cn.edu.seig.vibemusic.model.vo.PlaylistDetailVO;
import cn.edu.seig.vibemusic.model.vo.PlaylistVO;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IPlaylistService;
import cn.edu.seig.vibemusic.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@RestController
@RequestMapping("/playlist")
@Tag(name = "歌单相关接口", description = "歌单相关接口")
public class PlaylistController {

    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private MinioService minioService;
    /**
     * 创建歌单
     * @param playlistAddDTO 歌单信息
     * @return 创建结果
     */
    @PostMapping("/create")
    @Operation(summary = "创建歌单", description = "用户创建新的歌单")
    public Result createPlaylist(@RequestBody @Valid PlaylistAddDTO playlistAddDTO) {
        return playlistService.createPlaylist(playlistAddDTO);
    }
    /**
     * 获取所有歌单
     *
     * @param playlistDTO playlistDTO
     * @return 歌单列表
     */
    @PostMapping("/getAllPlaylists")
    @Operation(summary = "获取所有歌单", description = "获取所有歌单，支持分页和筛选")
    public Result<PageResult<PlaylistVO>> getAllPlaylists(@RequestBody @Valid PlaylistDTO playlistDTO) {
        return playlistService.getAllPlaylists(playlistDTO);
    }

    /**
     * 获取推荐歌单
     *
     * @param request request
     * @return 推荐歌单列表
     */
    @GetMapping("/getRecommendedPlaylists")
    @Operation(summary = "获取推荐歌单", description = "获取推荐歌单，基于用户收藏的歌单风格进行推荐")
    public Result<List<PlaylistVO>> getRandomPlaylists(HttpServletRequest request) {
        return playlistService.getRecommendedPlaylists(request);
    }

    /**
     * 获取歌单详情
     *
     * @param playlistId 歌单id
     * @return 歌单详情
     */
    @GetMapping("/getPlaylistDetail/{id}")
    @Operation(summary = "获取歌单详情", description = "根据歌单ID获取歌单详情，包括歌曲列表等信息")
    public Result<PlaylistDetailVO> getPlaylistDetail(@PathVariable("id") Long playlistId, HttpServletRequest request) {
        return playlistService.getPlaylistDetail(playlistId, request);
    }


    /**
     * 获取所有歌单数量
     *
     * @param style 歌单风格（可选）
     * @return 歌单数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取歌单总数", description = "统计系统中歌单数量，支持按风格筛选")
    public Result<Long> getAllPlaylistsCount(
            @RequestParam(required = false) @Parameter(description = "歌单风格（可选）", example = "流行") String style) {
        return playlistService.getAllPlaylistsCount(style);
    }

    /**
     * 分页查询歌单列表
     *
     * @param playlistDTO 歌单搜索条件（含分页参数）
     * @return 分页歌单列表
     */
    @PostMapping("/list")
    @Operation(summary = "分页查询歌单", description = "根据条件分页查询歌单信息，支持多条件筛选")
    public Result<PageResult<Playlist>> getAllPlaylistsInfo(
            @RequestBody @Parameter(description = "歌单搜索及分页参数", required = true) PlaylistDTO playlistDTO) {
        return playlistService.getAllPlaylistsInfo(playlistDTO);
    }

    /**
     * 新增歌单
     *
     * @param playlistAddDTO 歌单新增信息
     * @return 新增结果
     */
    @PostMapping("/add")
    @Operation(summary = "新增歌单", description = "添加新的歌单信息到系统")
    public Result addPlaylist(
            @RequestBody @Parameter(description = "歌单新增参数", required = true) PlaylistAddDTO playlistAddDTO) {
        return playlistService.addPlaylist(playlistAddDTO);
    }

    /**
     * 更新歌单信息
     *
     * @param playlistUpdateDTO 歌单更新信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @Operation(summary = "更新歌单信息", description = "修改歌单的基本信息（不含封面）")
    public Result updatePlaylist(
            @RequestBody @Parameter(description = "歌单更新参数", required = true) PlaylistUpdateDTO playlistUpdateDTO) {
        return playlistService.updatePlaylist(playlistUpdateDTO);
    }

    /**
     * 更新歌单封面
     *
     * @param playlistId 歌单ID
     * @param cover      封面文件
     * @return 更新结果（含新封面URL）
     */
    @PatchMapping("/cover/{id}")
    @Operation(summary = "更新歌单封面", description = "上传并更新指定歌单的封面图片")
    public Result updatePlaylistCover(
            @PathVariable("id") @Parameter(description = "歌单ID", required = true, example = "4001") Long playlistId,
            @RequestParam("cover") @Parameter(description = "封面图片文件", required = true) MultipartFile cover) {
        String coverUrl = minioService.uploadFile(cover, "playlists");
        return playlistService.updatePlaylistCover(playlistId, coverUrl);
    }

    /**
     * 删除单个歌单
     *
     * @param playlistId 歌单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除单个歌单", description = "根据歌单ID删除指定歌单")
    public Result deletePlaylist(
            @PathVariable("id") @Parameter(description = "歌单ID", required = true, example = "4001") Long playlistId) {
        return playlistService.deletePlaylist(playlistId);
    }

    /**
     * 批量删除歌单
     *
     * @param playlistIds 歌单ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除歌单", description = "根据歌单ID列表批量删除指定歌单")
    public Result deletePlaylists(
            @RequestBody @Parameter(description = "歌单ID列表", required = true, example = "[4001,4002]") List<Long> playlistIds) {
        return playlistService.deletePlaylists(playlistIds);
    }

}
