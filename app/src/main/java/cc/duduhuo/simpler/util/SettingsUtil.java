package cc.duduhuo.simpler.util;

import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.bean.Settings;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/15 21:47
 * 版本：1.0
 * 描述：应用设置工具类
 * 备注：
 * =======================================================
 */
public class SettingsUtil {
    /**
     * 读取当前设置信息
     *
     * @param uid 用户Id
     * @return 当前设置
     */
    public static Settings readSettings(String uid) {
        if (BaseSettings.sSettings != null) {
            return BaseSettings.sSettings;
        } else {
            return App.settingsServices.getSettingsById(uid);
        }
    }

    /**
     * 安全地插入用户设置（如果设置存在，则不插入）
     *
     * @param uid 用户Id
     */
    public static void insertSettingsSafely(String uid) {
        if (null == App.settingsServices.getSettingsById(uid)) {
            App.settingsServices.insertSettings(uid);
        }
    }

    /**
     * 更新refreshCount
     *
     * @param refreshCount
     */
    public static void updateRefreshCount(int refreshCount) {
        if (refreshCount == BaseSettings.sSettings.refreshCount) {
            return;
        }
        BaseSettings.sSettings.refreshCount = refreshCount;
        // 同步到数据库
        App.settingsServices.updateRefreshCount(BaseConfig.sUid, refreshCount);
    }

    /**
     * 更新autoRefresh
     *
     * @param autoRefresh
     */
    public static void updateAutoRefresh(boolean autoRefresh) {
        if (autoRefresh == BaseSettings.sSettings.autoRefresh) {
            return;
        }
        BaseSettings.sSettings.autoRefresh = autoRefresh;
        // 同步到数据库
        App.settingsServices.updateAutoRefresh(BaseConfig.sUid, autoRefresh);
    }

    /**
     * 更新picQuality
     *
     * @param picQuality
     */
    public static void updatePicQuality(int picQuality) {
        if (picQuality == BaseSettings.sSettings.picQuality) {
            return;
        }
        BaseSettings.sSettings.picQuality = picQuality;
        // 同步到数据库
        App.settingsServices.updatePicQuality(BaseConfig.sUid, picQuality);
    }

    /**
     * 更新uploadQuality
     *
     * @param uploadQuality
     */
    public static void updateUploadQuality(int uploadQuality) {
        if (uploadQuality == BaseSettings.sSettings.uploadQuality) {
            return;
        }
        BaseSettings.sSettings.uploadQuality = uploadQuality;
        // 同步到数据库
        App.settingsServices.updateUploadQuality(BaseConfig.sUid, uploadQuality);
    }

    /**
     * 更新fontSize
     *
     * @param fontSize
     */
    public static void updateFontSize(int fontSize) {
        if (fontSize == BaseSettings.sSettings.fontSize) {
            return;
        }
        BaseSettings.sSettings.fontSize = fontSize;
        // 同步到数据库
        App.settingsServices.updateFontSize(BaseConfig.sUid, fontSize);
    }

    /**
     * 更新browser
     *
     * @param browser
     */
    public static void updateBrowser(int browser) {
        if (browser == BaseSettings.sSettings.browser) {
            return;
        }
        BaseSettings.sSettings.browser = browser;
        // 同步到数据库
        App.settingsServices.updateBrowser(BaseConfig.sUid, browser);
    }

    /**
     * 更新messageNotification
     *
     * @param messageNotification
     */
    public static void updateMessageNotification(boolean messageNotification) {
        if (messageNotification == BaseSettings.sSettings.messageNotification) {
            return;
        }
        BaseSettings.sSettings.messageNotification = messageNotification;
        // 同步到数据库
        App.settingsServices.updateMessageNotification(BaseConfig.sUid, messageNotification);
    }

    /**
     * 更新privateLetterNotification
     *
     * @param privateLetterNotification
     */
    public static void updatePrivateLetterNotification(boolean privateLetterNotification) {
        if (privateLetterNotification == BaseSettings.sSettings.privateLetterNotification) {
            return;
        }
        BaseSettings.sSettings.privateLetterNotification = privateLetterNotification;
        // 同步到数据库
        App.settingsServices.updatePrivateLetterNotification(BaseConfig.sUid, privateLetterNotification);
    }

    /**
     * 更新notifyInterval
     *
     * @param notifyInterval
     */
    public static void updateNotifyInterval(int notifyInterval) {
        if (notifyInterval == BaseSettings.sSettings.notifyInterval) {
            return;
        }
        BaseSettings.sSettings.notifyInterval = notifyInterval;
        // 同步到数据库
        App.settingsServices.updateNotifyInterval(BaseConfig.sUid, notifyInterval);
    }

}
