package cn.edu.seig.vibemusic.controller;


import cn.edu.seig.vibemusic.model.dto.*;
import cn.edu.seig.vibemusic.model.entity.Artist;
import cn.edu.seig.vibemusic.model.entity.Playlist;
import cn.edu.seig.vibemusic.model.vo.ArtistNameVO;
import cn.edu.seig.vibemusic.model.vo.SongAdminVO;
import cn.edu.seig.vibemusic.model.vo.UserManagementVO;
import cn.edu.seig.vibemusic.result.PageResult;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.*;
import cn.edu.seig.vibemusic.util.BindingResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 管理员模块前端控制器
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员模块接口", description = "管理员相关操作接口（用户/歌手/歌曲/歌单管理）")
public class AdminController {

    @Autowired
    private IAdminService adminService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IArtistService artistService;
    @Autowired
    private ISongService songService;
    @Autowired
    private IPlaylistService playlistService;
    @Autowired
    private MinioService minioService;

    /**
     * 注册管理员
     *
     * @param adminDTO      管理员注册信息
     * @param bindingResult 参数校验结果
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "管理员注册", description = "新增系统管理员账号")
    public Result register(
            @RequestBody @Valid @Parameter(description = "管理员注册参数", required = true) AdminDTO adminDTO,
            @Parameter(hidden = true) BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return adminService.register(adminDTO);
    }

    /**
     * 管理员登录
     *
     * @param adminDTO      管理员登录信息
     * @param bindingResult 参数校验结果
     * @return 登录结果（含token）
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员账号登录系统，获取认证token")
    public Result login(
            @RequestBody @Valid @Parameter(description = "管理员登录参数", required = true) AdminDTO adminDTO,
            @Parameter(hidden = true) BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return adminService.login(adminDTO);
    }

    /**
     * 管理员登出
     *
     * @param token 认证token
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "注销当前管理员的登录状态")
    public Result logout(
            @RequestHeader("Authorization") @Parameter(description = "登录认证token", required = true) String token) {
        return adminService.logout(token);
    }

    /**********************************************************************************************/
    //  用户管理相关接口
    /**********************************************************************************************/

    /**
     * 获取所有用户数量
     *
     * @return 系统总用户数
     */
    @GetMapping("/getAllUsersCount")
    @Operation(summary = "获取用户总数", description = "统计系统中所有注册用户的数量")
    public Result<Long> getAllUsersCount() {
        return userService.getAllUsersCount();
    }

    /**
     * 分页查询用户列表
     *
     * @param userSearchDTO 用户搜索条件（含分页参数）
     * @return 分页用户列表
     */
    @PostMapping("/getAllUsers")
    @Operation(summary = "分页查询用户", description = "根据条件分页查询用户信息，支持多条件筛选")
    public Result<PageResult<UserManagementVO>> getAllUsers(
            @RequestBody @Parameter(description = "用户搜索及分页参数", required = true) UserSearchDTO userSearchDTO) {
        return userService.getAllUsers(userSearchDTO);
    }

    /**
     * 新增用户
     *
     * @param userAddDTO    用户新增信息
     * @param bindingResult 参数校验结果
     * @return 新增结果
     */
    @PostMapping("/addUser")
    @Operation(summary = "新增用户", description = "管理员手动新增系统用户")
    public Result addUser(
            @RequestBody @Valid @Parameter(description = "用户新增参数", required = true) UserAddDTO userAddDTO,
            @Parameter(hidden = true) BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return userService.addUser(userAddDTO);
    }

    /**
     * 更新用户信息
     *
     * @param userDTO       用户更新信息
     * @param bindingResult 参数校验结果
     * @return 更新结果
     */
    @PutMapping("/updateUser")
    @Operation(summary = "更新用户信息", description = "修改用户的基本信息（不含状态）")
    public Result updateUser(
            @RequestBody @Valid @Parameter(description = "用户更新参数", required = true) UserDTO userDTO,
            @Parameter(hidden = true) BindingResult bindingResult) {
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }
        return userService.updateUser(userDTO);
    }

    /**
     * 更新用户状态
     *
     * @param userId     用户ID
     * @param userStatus 用户状态（0-禁用，1-正常）
     * @return 更新结果
     */
    @PatchMapping("/updateUserStatus/{id}/{status}")
    @Operation(summary = "更新用户状态", description = "启用或禁用指定用户账号")
    public Result updateUserStatus(
            @PathVariable("id") @Parameter(description = "用户ID", required = true, example = "1001") Long userId,
            @PathVariable("status") @Parameter(description = "用户状态（0-禁用，1-正常）", required = true, example = "1") Integer userStatus) {
        return userService.updateUserStatus(userId, userStatus);
    }

    /**
     * 删除单个用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteUser/{id}")
    @Operation(summary = "删除单个用户", description = "根据用户ID删除指定用户")
    public Result deleteUser(
            @PathVariable("id") @Parameter(description = "用户ID", required = true, example = "1001") Long userId) {
        return userService.deleteUser(userId);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     * @return 删除结果
     */
    @DeleteMapping("/deleteUsers")
    @Operation(summary = "批量删除用户", description = "根据用户ID列表批量删除用户")
    public Result deleteUsers(
            @RequestBody @Parameter(description = "用户ID列表", required = true, example = "[1001,1002]") List<Long> userIds) {
        return userService.deleteUsers(userIds);
    }

    /**********************************************************************************************/
    //  歌手管理相关接口
    /**********************************************************************************************/

    /**
     * 获取歌手总数
     *
     * @param gender 歌手性别（可选）
     * @param area   歌手地区（可选）
     * @return 歌手总数
     */
    @GetMapping("/getAllArtistsCount")
    @Operation(summary = "获取歌手总数", description = "统计系统中歌手数量，支持按性别和地区筛选")
    public Result<Long> getAllArtistsCount(
            @RequestParam(required = false) @Parameter(description = "歌手性别（可选）", example = "1") Integer gender,
            @RequestParam(required = false) @Parameter(description = "歌手地区（可选）", example = "中国大陆") String area) {
        return artistService.getAllArtistsCount(gender, area);
    }

    /**
     * 分页查询歌手列表
     *
     * @param artistDTO 歌手搜索条件（含分页参数）
     * @return 分页歌手列表
     */
    @PostMapping("/getAllArtists")
    @Operation(summary = "分页查询歌手", description = "根据条件分页查询歌手信息，支持多条件筛选")
    public Result<PageResult<Artist>> getAllArtists(
            @RequestBody @Parameter(description = "歌手搜索及分页参数", required = true) ArtistDTO artistDTO) {
        return artistService.getAllArtistsAndDetail(artistDTO);
    }

    /**
     * 新增歌手
     *
     * @param artistAddDTO 歌手新增信息
     * @return 新增结果
     */
    @PostMapping("/addArtist")
    @Operation(summary = "新增歌手", description = "添加新的歌手信息到系统")
    public Result addArtist(
            @RequestBody @Parameter(description = "歌手新增参数", required = true) ArtistAddDTO artistAddDTO) {
        return artistService.addArtist(artistAddDTO);
    }

    /**
     * 更新歌手信息
     *
     * @param artistUpdateDTO 歌手更新信息
     * @return 更新结果
     */
    @PutMapping("/updateArtist")
    @Operation(summary = "更新歌手信息", description = "修改歌手的基本信息（不含头像）")
    public Result updateArtist(
            @RequestBody @Parameter(description = "歌手更新参数", required = true) ArtistUpdateDTO artistUpdateDTO) {
        return artistService.updateArtist(artistUpdateDTO);
    }

    /**
     * 更新歌手头像
     *
     * @param artistId 歌手ID
     * @param avatar   头像文件
     * @return 更新结果（含新头像URL）
     */
    @PatchMapping("/updateArtistAvatar/{id}")
    @Operation(summary = "更新歌手头像", description = "上传并更新指定歌手的头像图片")
    public Result updateArtistAvatar(
            @PathVariable("id") @Parameter(description = "歌手ID", required = true, example = "2001") Long artistId,
            @RequestParam("avatar") @Parameter(description = "头像图片文件", required = true) MultipartFile avatar) {
        String avatarUrl = minioService.uploadFile(avatar, "artists");
        return artistService.updateArtistAvatar(artistId, avatarUrl);
    }

    /**
     * 删除单个歌手
     *
     * @param artistId 歌手ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteArtist/{id}")
    @Operation(summary = "删除单个歌手", description = "根据歌手ID删除指定歌手")
    public Result deleteArtist(
            @PathVariable("id") @Parameter(description = "歌手ID", required = true, example = "2001") Long artistId) {
        return artistService.deleteArtist(artistId);
    }

    /**
     * 批量删除歌手
     *
     * @param artistIds 歌手ID列表
     * @return 删除结果
     */
    @DeleteMapping("/deleteArtists")
    @Operation(summary = "批量删除歌手", description = "根据歌手ID列表批量删除歌手")
    public Result deleteArtists(
            @RequestBody @Parameter(description = "歌手ID列表", required = true, example = "[2001,2002]") List<Long> artistIds) {
        return artistService.deleteArtists(artistIds);
    }

    /**********************************************************************************************/
    //  歌曲管理相关接口
    /**********************************************************************************************/

    /**
     * 获取歌曲总数
     *
     * @param style 歌曲风格（可选）
     * @return 歌曲总数
     */
    @GetMapping("/getAllSongsCount")
    @Operation(summary = "获取歌曲总数", description = "统计系统中歌曲数量，支持按风格筛选")
    public Result<Long> getAllSongsCount(
            @RequestParam(required = false) @Parameter(description = "歌曲风格（可选）", example = "流行") String style) {
        return songService.getAllSongsCount(style);
    }

    /**
     * 获取所有歌手ID和名称列表
     *
     * @return 歌手ID-名称映射列表
     */
    @GetMapping("/getAllArtistNames")
    @Operation(summary = "获取歌手ID和名称", description = "查询所有歌手的ID和名称，用于歌曲关联选择")
    public Result<List<ArtistNameVO>> getAllArtistNames() {
        return artistService.getAllArtistNames();
    }

    /**
     * 按歌手查询歌曲列表
     *
     * @param songDTO 歌曲搜索条件（含歌手ID、分页参数）
     * @return 分页歌曲列表
     */
    @PostMapping("/getAllSongsByArtist")
    @Operation(summary = "按歌手查询歌曲", description = "根据歌手ID分页查询该歌手的所有歌曲")
    public Result<PageResult<SongAdminVO>> getAllSongsByArtist(
            @RequestBody @Parameter(description = "歌曲搜索及分页参数", required = true) SongAndArtistDTO songDTO) {
        return songService.getAllSongsByArtist(songDTO);
    }

    /**
     * 新增歌曲
     *
     * @param songAddDTO 歌曲新增信息
     * @return 新增结果
     */
    @PostMapping("/addSong")
    @Operation(summary = "新增歌曲", description = "添加新的歌曲信息到系统")
    public Result addSong(
            @RequestBody @Parameter(description = "歌曲新增参数", required = true) SongAddDTO songAddDTO) {
        return songService.addSong(songAddDTO);
    }

    /**
     * 更新歌曲信息
     *
     * @param songUpdateDTO 歌曲更新信息
     * @return 更新结果
     */
    @PutMapping("/updateSong")
    @Operation(summary = "更新歌曲信息", description = "修改歌曲的基本信息（不含封面和音频）")
    public Result UpdateSong(
            @RequestBody @Parameter(description = "歌曲更新参数", required = true) SongUpdateDTO songUpdateDTO) {
        return songService.updateSong(songUpdateDTO);
    }

    /**
     * 更新歌曲封面
     *
     * @param songId 歌曲ID
     * @param cover  封面文件
     * @return 更新结果（含新封面URL）
     */
    @PatchMapping("/updateSongCover/{id}")
    @Operation(summary = "更新歌曲封面", description = "上传并更新指定歌曲的封面图片")
    public Result updateSongCover(
            @PathVariable("id") @Parameter(description = "歌曲ID", required = true, example = "3001") Long songId,
            @RequestParam("cover") @Parameter(description = "封面图片文件", required = true) MultipartFile cover) {
        String coverUrl = minioService.uploadFile(cover, "songCovers");
        return songService.updateSongCover(songId, coverUrl);
    }

    /**
     * 更新歌曲音频
     *
     * @param songId 歌曲ID
     * @param audio  音频文件
     * @return 更新结果（含新音频URL）
     */
    @PatchMapping("/updateSongAudio/{id}")
    @Operation(summary = "更新歌曲音频", description = "上传并更新指定歌曲的音频文件")
    public Result updateSongAudio(
            @PathVariable("id") @Parameter(description = "歌曲ID", required = true, example = "3001") Long songId,
            @RequestParam("audio") @Parameter(description = "音频文件", required = true) MultipartFile audio) {
        String audioUrl = minioService.uploadFile(audio, "songs");
        return songService.updateSongAudio(songId, audioUrl);
    }

    /**
     * 删除单个歌曲
     *
     * @param songId 歌曲ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteSong/{id}")
    @Operation(summary = "删除单个歌曲", description = "根据歌曲ID删除指定歌曲")
    public Result deleteSong(
            @PathVariable("id") @Parameter(description = "歌曲ID", required = true, example = "3001") Long songId) {
        return songService.deleteSong(songId);
    }
    /**
     * 批量删除歌曲
     *
     * @param songIds 歌曲ID列表
     * @return 删除结果
     */
    @DeleteMapping("/deleteSongs")
    @Operation(summary = "批量删除歌曲", description = "根据歌曲ID列表批量删除指定歌曲")
    public Result deleteSongs(
            @RequestBody @Parameter(description = "歌曲ID列表", required = true, example = "[3001,3002]") List<Long> songIds) {
        return songService.deleteSongs(songIds);
    }

    /**********************************************************************************************/
    //  歌单管理相关接口
    /**********************************************************************************************/

    /**
     * 获取所有歌单数量
     *
     * @param style 歌单风格
     * @return 歌单数量
     */
    @GetMapping("/getAllPlaylistsCount")
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
    @PostMapping("/getAllPlaylists")
    @Operation(summary = "分页查询歌单", description = "根据条件分页查询歌单信息，支持多条件筛选")
    public Result<PageResult<Playlist>> getAllPlaylists(
            @RequestBody @Parameter(description = "歌单搜索及分页参数", required = true) PlaylistDTO playlistDTO) {
        return playlistService.getAllPlaylistsInfo(playlistDTO);
    }

    /**
     * 新增歌单
     *
     * @param playlistAddDTO 歌单新增信息
     * @return 新增结果
     */
    @PostMapping("/addPlaylist")
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
    @PutMapping("/updatePlaylist")
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
    @PatchMapping("/updatePlaylistCover/{id}")
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
    @DeleteMapping("/deletePlaylist/{id}")
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
    @DeleteMapping("/deletePlaylists")
    @Operation(summary = "批量删除歌单", description = "根据歌单ID列表批量删除指定歌单")
    public Result deletePlaylists(
            @RequestBody @Parameter(description = "歌单ID列表", required = true, example = "[4001,4002]") List<Long> playlistIds) {
        return playlistService.deletePlaylists(playlistIds);
    }
}