package cn.edu.seig.vibemusic.constant;

public class PathConstant {

    public static final String PLAYLIST_DETAIL_PATH = "/playlist/getPlaylistDetail/**";
    public static final String ARTIST_DETAIL_PATH = "/artist/getArtistDetail/**";
    public static final String SONG_LIST_PATH = "/song/getAllSongs";
    public static final String SONG_DETAIL_PATH = "/song/getSongDetail/**";
    // Swagger/Knife4j 完整路径匹配（包含API文档接口和UI页面）/doc.html/**,/v3/api-docs/**,/webjars/**,/swagger-ui/**
    // Swagger/Knife4j 所有相关路径（必须逐个明确）
    public static final String SWAGGER_DOC_HTML = "/doc.html/**";
    public static final String SWAGGER_API_DOCS = "/v3/api-docs/**";
    public static final String SWAGGER_WEBJARS = "/webjars/**";
    public static final String SWAGGER_UI = "/swagger-ui/**";

}
