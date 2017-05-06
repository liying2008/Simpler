package cc.duduhuo.simpler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2015/12/06 12:31
 * 版本：1.0
 * 描述：自定义数据库Helper
 * 备注：
 * =======================================================
 */
class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DBInfo.DB_NAME, null, DBInfo.DB_VERSION);
    }

    /**
     * 数据库第一次创建时候调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库表
        db.execSQL(DBInfo.Table.USER_TB_CREATE);
        db.execSQL(DBInfo.Table.SETTINGS_TB_CREATE);
        db.execSQL(DBInfo.Table.DRAFT_TB_CREATE);
    }

    /**
     * 数据库文件版本号发生变化时调用
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
