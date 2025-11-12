package cn.edu.seig.vibemusic.service.impl;

import cn.edu.seig.vibemusic.constant.JwtClaimsConstant;
import cn.edu.seig.vibemusic.model.dto.PlaylistSongDTO;
import cn.edu.seig.vibemusic.model.entity.Playlist;
import cn.edu.seig.vibemusic.model.entity.PlaylistBinding;
import cn.edu.seig.vibemusic.mapper.PlaylistBindingMapper;
import cn.edu.seig.vibemusic.result.Result;
import cn.edu.seig.vibemusic.service.IPlaylistBindingService;
import cn.edu.seig.vibemusic.service.IPlaylistService;
import cn.edu.seig.vibemusic.service.ISongService;
import cn.edu.seig.vibemusic.model.entity.Song;
import cn.edu.seig.vibemusic.constant.MessageConstant;
import cn.edu.seig.vibemusic.util.ThreadLocalUtil;
import cn.edu.seig.vibemusic.util.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
@Service
public class PlaylistBindingServiceImpl extends ServiceImpl<PlaylistBindingMapper, PlaylistBinding> implements IPlaylistBindingService {
    @Autowired
    private IPlaylistService playlistService;

    @Autowired
    private ISongService songService;

    /**
     * 添加歌曲到歌单
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "playlistCache", allEntries = true)
    public Result addSongsToPlaylist(PlaylistSongDTO dto) {
        // 1. 验证歌单是否存在
        Playlist playlist = playlistService.getById(dto.getPlaylistId());
        if (playlist == null) {
            return Result.error(MessageConstant.PLAYLIST_NOT_FOUND);
        }

        // 2. 验证歌曲是否存在
        List<Long> validSongIds = songService.listByIds(dto.getSongIds()).stream()
                .map(Song::getSongId)
                .collect(Collectors.toList());

        if (validSongIds.isEmpty()) {
            return Result.error(MessageConstant.SONG_NOT_FOUND);
        }
        //3.验证歌单是否是自己创建的
        // 从ThreadLocal获取当前登录用户信息
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Long userId = TypeConversionUtil.toLong(userMap.get(JwtClaimsConstant.USER_ID));
        if(!playlist.getUserId().equals(userId)){
            return Result.error(MessageConstant.PLAYLIST_NOT_OWNED);
        }

        // 4. 批量添加绑定关系（过滤已存在的关联）
        List<PlaylistBinding> bindings = validSongIds.stream()
                .map(songId -> new PlaylistBinding()
                        .setPlaylistId(dto.getPlaylistId())
                        .setSongId(songId))
                .filter(binding -> !exists(new QueryWrapper<PlaylistBinding>()
                        .eq("playlist_id", binding.getPlaylistId())
                        .eq("song_id", binding.getSongId())))
                .collect(Collectors.toList());

        if (bindings.isEmpty()) {
            return Result.error(MessageConstant.SONGS_ALREADY_IN_PLAYLIST);
        }

        boolean success = saveBatch(bindings);
        return success ? Result.success(MessageConstant.ADD_SUCCESS) : Result.error(MessageConstant.ADD_FAILED);
    }

    /**
     * 从歌单删除歌曲
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "playlistCache", allEntries = true)
    public Result removeSongsFromPlaylist(PlaylistSongDTO dto) {
        // 1. 验证歌单是否存在
        if (!playlistService.existsById(dto.getPlaylistId())) {
            return Result.error(MessageConstant.PLAYLIST_NOT_FOUND);
        }

        // 2. 批量删除绑定关系
        QueryWrapper<PlaylistBinding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", dto.getPlaylistId())
                .in("song_id", dto.getSongIds());

        boolean success = remove(queryWrapper);
        return success ? Result.success(MessageConstant.DELETE_SUCCESS) : Result.error(MessageConstant.DELETE_FAILED);
    }

    /**
     * 清空歌单所有歌曲
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "playlistCache", allEntries = true)
    public Result clearPlaylistSongs(Long playlistId) {
        // 1. 验证歌单是否存在
        if (!playlistService.existsById(playlistId)) {
            return Result.error(MessageConstant.PLAYLIST_NOT_FOUND);
        }

        // 2. 删除该歌单所有绑定关系
        QueryWrapper<PlaylistBinding> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistId);

        boolean success = remove(queryWrapper);
        return success ? Result.success(MessageConstant.CLEAR_SUCCESS) : Result.error(MessageConstant.CLEAR_FAILED);
    }
}
