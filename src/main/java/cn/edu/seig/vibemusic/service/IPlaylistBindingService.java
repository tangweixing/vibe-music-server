package cn.edu.seig.vibemusic.service;

import cn.edu.seig.vibemusic.model.dto.PlaylistSongDTO;
import cn.edu.seig.vibemusic.model.entity.PlaylistBinding;
import cn.edu.seig.vibemusic.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunpingli
 * @since 2025-01-09
 */
public interface IPlaylistBindingService extends IService<PlaylistBinding> {
    // 添加歌曲到歌单
    Result addSongsToPlaylist(PlaylistSongDTO dto);

    // 从歌单删除歌曲
    Result removeSongsFromPlaylist(PlaylistSongDTO dto);

    // 清空歌单所有歌曲
    Result clearPlaylistSongs(Long playlistId);
}
