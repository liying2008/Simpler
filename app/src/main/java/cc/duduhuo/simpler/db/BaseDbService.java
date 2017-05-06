package cc.duduhuo.simpler.db;

import android.content.Context;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2016/9/16 13:09
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
class BaseDbService {
    DatabaseHelper dbHelper = null;
    /**
     * 构造方法
     *
     * @param context
     */
    BaseDbService(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
    }
}
