package cc.duduhuo.simpler.config;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 14:34
 * 版本：1.0
 * 描述：新浪微博相关的常量
 * 备注：
 * =======================================================
 */
public interface SinaConsts {
    /** 应用的APP_KEY */
    String APP_KEY = "1260079963";
    /** weico的APP_KEY */
    String WEICO_APP_KEY = "211160679";
    /** 默认回调页 */
    String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    /** weico的回调页 */
    String WEICO_REDIRECT_URL = "http://oauth.weico.cc";
    /** 应用申请的高级权限 */
    String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog";
    /** Weico的包名 */
    String WEICO_PACKAGE_NAME = "com.eico.weico";
}
