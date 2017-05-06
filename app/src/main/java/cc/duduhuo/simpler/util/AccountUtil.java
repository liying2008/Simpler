package cc.duduhuo.simpler.util;

import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.bean.Account;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 16:22
 * 版本：1.0
 * 描述：帐号工具类
 * 备注：
 * =======================================================
 */
public class AccountUtil {
    /**
     * 读取当前帐号信息
     * @param uid 用户Id
     * @return 当前帐号。无当前帐号信息则返回null
     */
    public static Account readAccount(String uid) {
        if (BaseConfig.sAccount != null) {
            return BaseConfig.sAccount;
        } else {
            return App.userServices.getAccountById(uid);
        }
    }

    /**
     * 更新ScreenName
     * @param screenName
     */
    public static void updateScreenName(String screenName) {
        if (screenName.equals(BaseConfig.sAccount.screenName)) {
            return;
        }
        BaseConfig.sAccount.screenName = screenName;
        // 同步到数据库
        App.userServices.updateScreenName(BaseConfig.sUid, screenName);
    }

    /**
     * 更新Name
     * @param name
     */
    public static void updateName(String name) {
        if (name.equals(BaseConfig.sAccount.name)) {
            return;
        }
        BaseConfig.sAccount.name = name;
        // 同步到数据库
        App.userServices.updateName(BaseConfig.sUid, name);
    }

    /**
     * 更新用户头像地址
     * @param headUrl
     */
    public static void updateHeadUrl(String headUrl) {
        if (headUrl.equals(BaseConfig.sAccount.headUrl)) {
            return;
        }
        BaseConfig.sAccount.headUrl = headUrl;
        // 同步到数据库
        App.userServices.updateHeadUrl(BaseConfig.sUid, headUrl);
    }

    /**
     * 更新用户头像缓存路径
     * @param headCachePath
     */
    public static void updateHeadCachePath(String headCachePath) {
        if (headCachePath.equals(BaseConfig.sAccount.headCachePath)) {
            return;
        }
        BaseConfig.sAccount.headCachePath = headCachePath;
        // 同步到数据库
        App.userServices.updateHeadCachePath(BaseConfig.sUid, headCachePath);
    }

    /**
     * 更新用户登录的Cookie
     * @param cookie
     */
    public static void updateCookie(String cookie) {
        if (cookie.equals(BaseConfig.sAccount.cookie)) {
            return;
        }
        BaseConfig.sAccount.cookie = cookie;
        // 同步到数据库
        App.userServices.updateCookie(BaseConfig.sUid, cookie);
    }
}
