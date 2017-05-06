package cc.duduhuo.simpler.config;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Group;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;

import cc.duduhuo.simpler.bean.Account;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/29 10:56
 * 版本：1.0
 * 描述：用户配置类（全局静态变量）
 * 备注：
 * =======================================================
 */
public class BaseConfig {
    /** 当前用户ID (为空表示没有用户已经登录) */
    public static String sUid = "";
    /** 当前用户的AccessToken */
    public static Oauth2AccessToken sAccessToken;
    /** 当前用户 */
    public static User sUser;
    /** 授权用户的分组列表 */
    public static ArrayList<Group> sGroups;
    /** 用户的设备是否有存储卡 */
    public static boolean sSDCardExist = false;
    public static int[] sSwipeRefreshColor = {android.R.color.holo_red_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light,
            android.R.color.holo_blue_bright};
    /** 当前帐号 */
    public static Account sAccount = null;
    /** 添加帐号模式 */
    public static boolean sAddAccountMode = false;
    /** 视频暂停时的播放位置 */
    public static int sPositionWhenPaused = -1;
    /** Token是否失效 */
    public static boolean sTokenExpired = false;
}
