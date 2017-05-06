package cc.duduhuo.simpler.bean;

import cc.duduhuo.simpler.config.Browser;
import cc.duduhuo.simpler.config.PicQuality;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/15 17:06
 * 版本：1.0
 * 描述：用户设置实体类
 * 备注：
 * =======================================================
 */
public class Settings {
    public static final String UID = "uid";
    public static final String REFRESH_COUNT = "refresh_count";
    public static final String AUTO_REFRESH = "auto_refresh";
    public static final String PIC_QUALITY = "pic_quality";
    public static final String UPLOAD_QUALITY = "upload_quality";
    public static final String FONT_SIZE = "font_size";
    public static final String BROWSER = "BROWSER";
    public static final String MESSAGE_NOTIFICATION = "message_notification";
    public static final String PRIVATE_LETTER_NOTIFICATION = "private_letter_notification";
    public static final String NOTIFY_INTERVAL = "notify_interval";

    /** 用户Id */
    public String uid;
    /** 每次刷新微博数 */
    public int refreshCount;
    /** 每次打开自动刷新微博 */
    public boolean autoRefresh;
    /** 微博图片显示质量 */
    public int picQuality;
    /** 图片上传质量 */
    public int uploadQuality;
    /** 微博字体大小 */
    public int fontSize;
    /** 浏览器选项 */
    public int browser;
    /** 是否打开消息通知 */
    public boolean messageNotification;
    /** 是否打开私信通知 */
    public boolean privateLetterNotification;
    /** 通知时间间隔 */
    public int notifyInterval;

    public Settings() {
    }

    public Settings(String uid, int refreshCount, boolean autoRefresh, int picQuality, int uploadQuality, int fontSize, int browser, boolean messageNotification, boolean privateLetterNotification, int notifyInterval) {
        this.uid = uid;
        this.refreshCount = refreshCount;
        this.autoRefresh = autoRefresh;
        this.picQuality = picQuality;
        this.uploadQuality = uploadQuality;
        this.fontSize = fontSize;
        this.browser = browser;
        this.messageNotification = messageNotification;
        this.privateLetterNotification = privateLetterNotification;
        this.notifyInterval = notifyInterval;
    }

    /**
     * 应用的默认设置
     */
    public static final class Default {
        /** 默认每次刷新微博数 */
        public static final int REFRESH_COUNT = 20;
        /** 默认每次打开不自动刷新微博 */
        public static final boolean AUTO_REFRESH = false;
        /** 默认微博图片显示质量为中图 */
        public static final int PIC_QUALITY = PicQuality.MIDDLE;
        /** 默认图片上传质量为原图 */
        public static final int UPLOAD_QUALITY = PicQuality.ORIGINAL;
        /** 默认微博字体大小为14pt */
        public static final int FONT_SIZE = 14;
        /** 默认使用浏览器为内置浏览器 */
        public static final int BROWSER = Browser.BUILT_IN;
        /** 默认打开消息通知 */
        public static final boolean MESSAGE_NOTIFICATION = true;
        /** 默认打开私信通知 */
        public static final boolean PRIVATE_LETTER_NOTIFICATION = true;
        /** 默认通知时间间隔为5分钟 */
        public static final int NOTIFY_INTERVAL = 5 * 60;
    }
}
