package cc.duduhuo.simpler.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cc.duduhuo.simpler.bean.Settings;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/15 19:36
 * 版本：1.0
 * 描述：用户设置业务类
 * 备注：
 * =======================================================
 */
public class SettingsServices extends BaseDbService {

    private DatabaseHelper dbHelper;
    private String[] columns = {Settings.UID, Settings.REFRESH_COUNT, Settings.AUTO_REFRESH,
            Settings.PIC_QUALITY, Settings.UPLOAD_QUALITY, Settings.FONT_SIZE, Settings.BROWSER,
            Settings.MESSAGE_NOTIFICATION, Settings.PRIVATE_LETTER_NOTIFICATION, Settings.NOTIFY_INTERVAL};

    public SettingsServices(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }


    /**
     * 添加用户设置信息到数据库
     *
     * @param uid 用户Id
     */
    public void insertSettings(String uid) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBInfo.Table.SETTINGS_TB_NAME + "(" + Settings.UID + ") VALUES ('" + uid + "');";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定uid的设置
     */
    public void deleteSettingsById(String uid) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.SETTINGS_TB_NAME + " WHERE " + Settings.UID + " = '" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除表中的全部数据
     */
    public void deleteAllSettings() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBInfo.Table.SETTINGS_TB_NAME);
        db.close();
    }

    /**
     * 根据用户Id(uid)获取设置信息
     *
     * @param uid 用户Id
     * @return 账号信息，没有找到返回null
     */
    public Settings getSettingsById(String uid) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.SETTINGS_TB_NAME, columns,
                Settings.UID + "=?", new String[]{uid}, null, null, null);

        Settings settings = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                int refreshCount = cursor.getInt(cursor.getColumnIndex(Settings.REFRESH_COUNT));
                boolean autoRefresh = cursor.getInt(cursor.getColumnIndex(Settings.AUTO_REFRESH)) == 1;
                int picQuality = cursor.getInt(cursor.getColumnIndex(Settings.PIC_QUALITY));
                int uploadQuality = cursor.getInt(cursor.getColumnIndex(Settings.UPLOAD_QUALITY));
                int fontSize = cursor.getInt(cursor.getColumnIndex(Settings.FONT_SIZE));
                int browser = cursor.getInt(cursor.getColumnIndex(Settings.BROWSER));
                boolean messageNotification = cursor.getInt(cursor.getColumnIndex(Settings.MESSAGE_NOTIFICATION)) == 1;
                boolean privateLetterNotification = cursor.getInt(cursor.getColumnIndex(Settings.PRIVATE_LETTER_NOTIFICATION)) == 1;
                int notifyInterval = cursor.getInt(cursor.getColumnIndex(Settings.NOTIFY_INTERVAL));

                settings = new Settings(uid, refreshCount, autoRefresh, picQuality, uploadQuality, fontSize,
                        browser, messageNotification, privateLetterNotification, notifyInterval);
            }
            cursor.close();
        }
        db.close();
        return settings;
    }

    /**
     * 更新每次刷新微博数
     *
     * @param uid          用户Id
     * @param refreshCount 每次刷新微博数
     */
    public void updateRefreshCount(String uid, int refreshCount) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.REFRESH_COUNT + "="
                + refreshCount + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 每次打开是否自动刷新微博
     *
     * @param uid         用户Id
     * @param autoRefresh 是否每次打开自动刷新微博
     */
    public void updateAutoRefresh(String uid, boolean autoRefresh) {
        int ar = autoRefresh ? 1 : 0;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.AUTO_REFRESH + "="
                + ar + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新微博图片显示质量
     *
     * @param uid        用户Id
     * @param picQuality 微博图片显示质量
     */
    public void updatePicQuality(String uid, int picQuality) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.PIC_QUALITY + "="
                + picQuality + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新图片上传质量
     *
     * @param uid           用户Id
     * @param uploadQuality 图片上传质量
     */
    public void updateUploadQuality(String uid, int uploadQuality) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.UPLOAD_QUALITY + "="
                + uploadQuality + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新微博字体大小
     *
     * @param uid      用户Id
     * @param fontSize 微博字体大小
     */
    public void updateFontSize(String uid, int fontSize) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.FONT_SIZE + "="
                + fontSize + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新浏览器选项
     *
     * @param uid     用户Id
     * @param browser 浏览器选项
     */
    public void updateBrowser(String uid, int browser) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.BROWSER + "="
                + browser + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 是否打开消息通知
     *
     * @param uid                 用户Id
     * @param messageNotification 是否打开消息通知
     */
    public void updateMessageNotification(String uid, boolean messageNotification) {
        int mn = messageNotification ? 1 : 0;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.MESSAGE_NOTIFICATION + "="
                + mn + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 是否打开私信通知
     *
     * @param uid                       用户Id
     * @param privateLetterNotification 是否打开私信通知
     */
    public void updatePrivateLetterNotification(String uid, boolean privateLetterNotification) {
        int pln = privateLetterNotification ? 1 : 0;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.PRIVATE_LETTER_NOTIFICATION + "="
                + pln + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新通知时间间隔
     *
     * @param uid            用户Id
     * @param notifyInterval 通知时间间隔
     */
    public void updateNotifyInterval(String uid, int notifyInterval) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.SETTINGS_TB_NAME + " SET " + Settings.NOTIFY_INTERVAL + "="
                + notifyInterval + " WHERE " + Settings.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

}
