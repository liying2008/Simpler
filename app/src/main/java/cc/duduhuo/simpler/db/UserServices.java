package cc.duduhuo.simpler.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.simpler.bean.Account;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2015/12/06 12:34
 * 版本：1.0
 * 描述：用户帐号信息业务类
 * 备注：
 * =======================================================
 */
public class UserServices extends BaseDbService {

    private DatabaseHelper dbHelper;
    private String[] columns = {Account.UID, Account.ACCESS_TOKEN, Account.EXPIRES_IN,
            Account.REFRESH_TOKEN, Account.SCREEN_NAME, Account.NAME, Account.HEAD_CACHE_PATH,
            Account.HEAD_URL, Account.COOKIE};

    public UserServices(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }


    /**
     * 添加或更新用户accessToken到数据库
     *
     * @param account 帐号信息
     */
    public void insertAccount(Account account) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBInfo.Table.USER_TB_NAME + "(" + Account.UID + ", "
                + Account.ACCESS_TOKEN + ", " + Account.EXPIRES_IN + ", " + Account.REFRESH_TOKEN
                + ") VALUES ('" + account.uid + "','" + account.accessToken + "',"
                + account.expiresIn + ", '" + account.refreshToken + "');";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定userName的账户
     */
    public void deleteAccountByScreenName(String screenName) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.USER_TB_NAME + " WHERE " + Account.SCREEN_NAME + " = '" + screenName + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定uid的账户
     */
    public void deleteAccountById(String uid) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.USER_TB_NAME + " WHERE " + Account.UID + " = '" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除数据表中的全部数据
     */
    public void deleteAllAccounts() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBInfo.Table.USER_TB_NAME);
        db.close();
    }

    /**
     * 根据昵称(screenName)获取账号信息
     *
     * @param screenName 昵称
     * @return 账号信息，没有找到返回null
     */
    public Account getAccountByScreenName(String screenName) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.USER_TB_NAME, columns,
                Account.SCREEN_NAME + "=?", new String[]{screenName}, null, null, null);

        Account account = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String uid = cursor.getString(cursor.getColumnIndex(Account.UID));
                String accessToken = cursor.getString(cursor.getColumnIndex(Account.ACCESS_TOKEN));
                long expiresIn = cursor.getLong(cursor.getColumnIndex(Account.EXPIRES_IN));
                String refreshToken = cursor.getString(cursor.getColumnIndex(Account.REFRESH_TOKEN));
                String name = cursor.getString(cursor.getColumnIndex(Account.NAME));
                String headCachePath = cursor.getString(cursor.getColumnIndex(Account.HEAD_CACHE_PATH));
                String headUrl = cursor.getString(cursor.getColumnIndex(Account.HEAD_URL));
                String cookie = cursor.getString(cursor.getColumnIndex(Account.COOKIE));

                account = new Account(uid, accessToken, expiresIn, refreshToken, screenName, name,
                        headCachePath, headUrl, cookie);
            }
            cursor.close();
        }
        db.close();
        return account;
    }

    /**
     * 根据用户Id(uid)获取账号信息
     *
     * @param uid 用户Id
     * @return 账号信息，没有找到返回null
     */
    public Account getAccountById(String uid) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.USER_TB_NAME, columns,
                Account.UID + "=?", new String[]{uid}, null, null, null);

        Account account = null;
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String accessToken = cursor.getString(cursor.getColumnIndex(Account.ACCESS_TOKEN));
                long expiresIn = cursor.getLong(cursor.getColumnIndex(Account.EXPIRES_IN));
                String refreshToken = cursor.getString(cursor.getColumnIndex(Account.REFRESH_TOKEN));
                String screenName = cursor.getString(cursor.getColumnIndex(Account.SCREEN_NAME));
                String name = cursor.getString(cursor.getColumnIndex(Account.NAME));
                String headCachePath = cursor.getString(cursor.getColumnIndex(Account.HEAD_CACHE_PATH));
                String headUrl = cursor.getString(cursor.getColumnIndex(Account.HEAD_URL));
                String cookie = cursor.getString(cursor.getColumnIndex(Account.COOKIE));

                account = new Account(uid, accessToken, expiresIn, refreshToken, screenName, name,
                        headCachePath, headUrl, cookie);
            }
            cursor.close();
        }
        db.close();
        return account;
    }

    /**
     * 查询所有用户的信息
     *
     * @return 保存有用户信息的List
     */
    public List<Account> getAllUsers() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = db.query(DBInfo.Table.USER_TB_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Account account = null;
                while (cursor.moveToNext()) {
                    String uid = cursor.getString(cursor.getColumnIndex(Account.UID));
                    String accessToken = cursor.getString(cursor.getColumnIndex(Account.ACCESS_TOKEN));
                    long expiresIn = cursor.getLong(cursor.getColumnIndex(Account.EXPIRES_IN));
                    String refreshToken = cursor.getString(cursor.getColumnIndex(Account.REFRESH_TOKEN));
                    String screenName = cursor.getString(cursor.getColumnIndex(Account.SCREEN_NAME));
                    String name = cursor.getString(cursor.getColumnIndex(Account.NAME));
                    String headCachePath = cursor.getString(cursor.getColumnIndex(Account.HEAD_CACHE_PATH));
                    String headUrl = cursor.getString(cursor.getColumnIndex(Account.HEAD_URL));
                    String cookie = cursor.getString(cursor.getColumnIndex(Account.COOKIE));

                    account = new Account(uid, accessToken, expiresIn, refreshToken, screenName,
                            name, headCachePath, headUrl, cookie);
                    accounts.add(account);
                }
                cursor.close();
            }
        }
        db.close();
        return accounts;
    }

    /**
     * 更新ScreenName
     *
     * @param uid        用户Id
     * @param screenName 用户昵称
     */
    public void updateScreenName(String uid, String screenName) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.SCREEN_NAME + "='"
                + screenName + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新Oauth2AccessToken
     *
     * @param uid   用户Id
     * @param token Oauth2AccessToken
     */
    public void updateOauth2AccessToken(String uid, Oauth2AccessToken token) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.ACCESS_TOKEN + "='"
                + token.getToken() + "', " + Account.EXPIRES_IN + "=" + token.getExpiresTime()
                + ", " + Account.REFRESH_TOKEN + "='" + token.getRefreshToken()
                + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新Name
     *
     * @param uid  用户Id
     * @param name 友好名称
     */
    public void updateName(String uid, String name) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.NAME + "='"
                + name + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新头像缓存路径
     *
     * @param uid           用户Id
     * @param headCachePath 头像缓存路径
     */
    public void updateHeadCachePath(String uid, String headCachePath) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.HEAD_CACHE_PATH + "='"
                + headCachePath + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新头像地址
     *
     * @param uid     用户Id
     * @param headUrl 头像地址
     */
    public void updateHeadUrl(String uid, String headUrl) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.HEAD_URL + "='"
                + headUrl + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新用户登录微博的Cookie
     *
     * @param uid    用户Id
     * @param cookie Cookie
     */
    public void updateCookie(String uid, String cookie) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.USER_TB_NAME + " SET " + Account.COOKIE + "='"
                + cookie + "' WHERE " + Account.UID + "='" + uid + "'";
        db.execSQL(sql);
        db.close();
    }
}
