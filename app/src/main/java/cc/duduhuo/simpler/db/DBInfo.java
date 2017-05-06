package cc.duduhuo.simpler.db;

import cc.duduhuo.simpler.bean.Account;
import cc.duduhuo.simpler.bean.Draft;
import cc.duduhuo.simpler.bean.Settings;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2015/12/06 12:29
 * 版本：1.0
 * 描述：数据库常量信息
 * 备注：
 * =======================================================
 */
class DBInfo {
    /**
     * 数据库名称
     */
    static final String DB_NAME = "users.db";
    /**
     * 数据库版本
     */
    static final int DB_VERSION = 1;

    /**
     * 数据表
     */
    static class Table {
        static final String USER_TB_NAME = "access_token";
        static final String SETTINGS_TB_NAME = "settings";
        static final String DRAFT_TB_NAME = "status_draft";
        // 创建access_token表的SQL语句
        static final String USER_TB_CREATE = "CREATE TABLE IF NOT EXISTS " + USER_TB_NAME + " (" +
                Account.UID + " TEXT PRIMARY KEY NOT NULL, " +
                Account.ACCESS_TOKEN + " TEXT NOT NULL, " +
                Account.EXPIRES_IN + " LONG NOT NULL," +
                Account.REFRESH_TOKEN + " TEXT NOT NULL," +
                Account.SCREEN_NAME + " TEXT DEFAULT ''," +
                Account.NAME + " TEXT DEFAULT ''," +
                Account.HEAD_CACHE_PATH + " TEXT DEFAULT ''," +
                Account.HEAD_URL + " TEXT DEFAULT ''," +
                Account.COOKIE + " TEXT DEFAULT ''" +
                ")";


        private static final int AUTO_REFRESH = Settings.Default.AUTO_REFRESH ? 1 : 0;
        private static final int MESSAGE_NOTIFICATION = Settings.Default.MESSAGE_NOTIFICATION ? 1 : 0;
        private static final int PRIVATE_LETTER_NOTIFICATION = Settings.Default.PRIVATE_LETTER_NOTIFICATION ? 1 : 0;

        // 创建settings表的SQL语句
        static final String SETTINGS_TB_CREATE = "CREATE TABLE IF NOT EXISTS " + SETTINGS_TB_NAME + " (" +
                Settings.UID + " TEXT PRIMARY KEY NOT NULL, " +
                Settings.REFRESH_COUNT + " INTEGER DEFAULT " + Settings.Default.REFRESH_COUNT + " NOT NULL, " +
                Settings.AUTO_REFRESH + " INTEGER DEFAULT " + AUTO_REFRESH + " NOT NULL," +
                Settings.PIC_QUALITY + " INTEGER DEFAULT " + Settings.Default.PIC_QUALITY + " NOT NULL," +
                Settings.UPLOAD_QUALITY + " INTEGER DEFAULT " + Settings.Default.UPLOAD_QUALITY + " NOT NULL," +
                Settings.FONT_SIZE + " INTEGER DEFAULT " + Settings.Default.FONT_SIZE + " NOT NULL," +
                Settings.BROWSER + " INTEGER DEFAULT " + Settings.Default.BROWSER + " NOT NULL," +
                Settings.MESSAGE_NOTIFICATION + " INTEGER DEFAULT " + MESSAGE_NOTIFICATION + " NOT NULL," +
                Settings.PRIVATE_LETTER_NOTIFICATION + " INTEGER DEFAULT " + PRIVATE_LETTER_NOTIFICATION + " NOT NULL," +
                Settings.NOTIFY_INTERVAL + " INTEGER DEFAULT " + Settings.Default.NOTIFY_INTERVAL + " NOT NULL" +
                ")";


        private static final int IS_ENABLE_GEO = Draft.Default.IS_ENABLE_GEO ? 1 : 0;
        private static final int IS_LOCATION = Draft.Default.IS_LOCATION ? 1 : 0;

        // 创建status_draft表的SQL语句
        static final String DRAFT_TB_CREATE = "CREATE TABLE IF NOT EXISTS " + DRAFT_TB_NAME + " (" +
                Draft.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Draft.UID + " TEXT NOT NULL, " +
                Draft.CONTENT + " TEXT DEFAULT '" + Draft.Default.CONTENT + "' NOT NULL, " +
                Draft.PHOTO_LIST + " TEXT DEFAULT '" + Draft.photoList2String(Draft.Default.PHOTO_LIST) + "' NOT NULL, " +
                Draft.LATITUDE + " DOUBLE DEFAULT " + Draft.Default.LATITUDE + " NOT NULL, " +
                Draft.LONGITUDE + " DOUBLE DEFAULT " + Draft.Default.LONGITUDE + " NOT NULL, " +
                Draft.IS_ENABLE_GEO + " INTEGER DEFAULT " + IS_ENABLE_GEO + " NOT NULL, " +
                Draft.IS_LOCATION + " INTEGER DEFAULT " + IS_LOCATION + " NOT NULL, " +
                Draft.ADDR_STR + " TEXT DEFAULT '" + Draft.Default.ADDR_STR + "' NOT NULL, " +
                Draft.MENU_ID + " INTEGER DEFAULT " + Draft.Default.MENU_ID + " NOT NULL, " +
                Draft.GROUP_ID + " TEXT DEFAULT '" + Draft.Default.GROUP_ID + "' NOT NULL, " +
                Draft.GROUP_NAME + " TEXT DEFAULT '" + Draft.Default.GROUP_NAME + "' NOT NULL" +
                ")";
    }
}
