package cc.duduhuo.simpler.bean;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/5 16:39
 * 版本：1.0
 * 描述：新浪微博账号实体类
 * 备注：
 * =======================================================
 */
public class Account {
    public static final String UID = "uid";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String SCREEN_NAME = "screen_name";
    public static final String NAME = "name";
    public static final String HEAD_CACHE_PATH = "head_cache_path";
    public static final String HEAD_URL = "head_url";
    public static final String COOKIE = "cookie";

    /** 用户Id */
    public String uid;
    /** Access Token */
    public String accessToken;
    /** access_token的生命周期，单位是秒 */
    public long expiresIn;
    /** Refresh Token，用来刷新Access Token */
    public String refreshToken;
    /** 昵称 */
    public String screenName;
    /** 备注(友好名称) */
    public String name;
    /** 用户头像缓存路径 */
    public String headCachePath;
    /** 用户头像地址（大图），180×180像素 */
    public String headUrl;
    /** 用户登录微博的Cookie */
    public String cookie;

    public Account() {
    }

    public Account(String uid, String accessToken, long expiresIn, String refreshToken) {
        this.uid = uid;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public Account(String uid, String accessToken, long expiresIn, String refreshToken,
                   String screenName, String name, String headCachePath, String headUrl, String cookie) {
        this.uid = uid;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.screenName = screenName;
        this.name = name;
        this.headCachePath = headCachePath;
        this.headUrl = headUrl;
        this.cookie = cookie;
    }
}
