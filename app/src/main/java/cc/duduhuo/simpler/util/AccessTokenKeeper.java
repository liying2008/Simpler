package cc.duduhuo.simpler.util;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.bean.Account;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 14:57
 * 版本：1.0
 * 描述：该类定义了微博授权时所需要的参数。
 * 备注：
 * =======================================================
 */
public class AccessTokenKeeper {
    /**
     * 保存 Token 对象到数据库，并设置为当前用户。
     *
     * @param token Token 对象
     */
    public static void writeAccessToken(Oauth2AccessToken token) {
        String uid = token.getUid();
        Account account = new Account(uid, token.getToken(), token.getExpiresTime(), token.getRefreshToken());
        // 设置当前的用户ID
        BaseConfig.sUid = uid;
        AccountUtil.updateOauth2AccessToken(token);
        SettingsUtil.insertSettingsSafely(uid);

        if (BaseConfig.sAccount == null) {
            // 设置当前帐户
            BaseConfig.sAccount = account;
        }
        // 设置当前应用设置
        BaseSettings.sSettings = SettingsUtil.readSettings(uid, false);
        // 保存当前用户ID
        PrefsUtils.putString(Constants.PREFS_CUR_UID, uid);
    }

    /**
     * 从 数据库 读取 Token 信息。
     *
     * @param uid     用户Id
     * @param refresh 是否强制从数据库中读取
     * @return 返回 Token 对象
     */
    public static Oauth2AccessToken readAccessToken(String uid, boolean refresh) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        if (refresh) {
            Account account = App.userServices.getAccountById(uid);
            if (account != null) {
                token.setUid(account.uid);
                token.setToken(account.accessToken);
                token.setRefreshToken(account.refreshToken);
                token.setExpiresTime(account.expiresIn);
            }
        } else {
            if (BaseConfig.sAccount != null) {
                token.setUid(BaseConfig.sAccount.uid);
                token.setToken(BaseConfig.sAccount.accessToken);
                token.setRefreshToken(BaseConfig.sAccount.refreshToken);
                token.setExpiresTime(BaseConfig.sAccount.expiresIn);
            } else {
                Account account = App.userServices.getAccountById(uid);
                if (account != null) {
                    token.setUid(account.uid);
                    token.setToken(account.accessToken);
                    token.setRefreshToken(account.refreshToken);
                    token.setExpiresTime(account.expiresIn);
                }
            }
        }
        return token;
    }

    /**
     * 删除Token信息。
     */
    public static void delToken(String uid) {
        App.userServices.deleteAccountById(uid);
        // 清空当前 Token
        BaseConfig.sAccessToken = null;
        // 删除当前的用户ID
        PrefsUtils.putString(Constants.PREFS_CUR_UID, "");
        // 将当前用户ID置为空
        BaseConfig.sUid = "";
        // 将当前用户对象置空
        BaseConfig.sUser = null;
    }
}
