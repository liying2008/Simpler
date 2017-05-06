package cc.duduhuo.simpler.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.simpler.bean.Draft;
import cc.duduhuo.simpler.bean.Photo;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/22 22:33
 * 版本：1.0
 * 描述：草稿箱业务类
 * 备注：
 * =======================================================
 */
public class DraftServices extends BaseDbService {

    private DatabaseHelper dbHelper;
    private String[] columns = {Draft.ID, Draft.UID, Draft.CONTENT, Draft.PHOTO_LIST,
            Draft.LATITUDE, Draft.LONGITUDE, Draft.IS_ENABLE_GEO, Draft.IS_LOCATION,
            Draft.ADDR_STR, Draft.MENU_ID, Draft.GROUP_ID, Draft.GROUP_NAME};

    public DraftServices(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }


    /**
     * 向数据库中增加一个草稿
     *
     * @param draft 草稿
     */
    public void insertDraft(Draft draft) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int isEnableGeo = draft.isEnableGeo ? 1 : 0;
        int isLocation = draft.isLocation ? 1 : 0;
        String sql = "INSERT INTO " + DBInfo.Table.DRAFT_TB_NAME + "(" + Draft.UID + ", "
                + Draft.CONTENT + ", " + Draft.PHOTO_LIST + ", " + Draft.LATITUDE + ", "
                + Draft.LONGITUDE + ", " + Draft.IS_ENABLE_GEO + ", " + Draft.IS_LOCATION + ", "
                + Draft.ADDR_STR + ", " + Draft.MENU_ID + ", " + Draft.GROUP_ID + ", " + Draft.GROUP_NAME
                + ") VALUES ('" + draft.uid + "','" + draft.content + "','"
                + Draft.photoList2String(draft.photoList) + "', " + draft.latitude + ", " + draft.longitude + ", "
                + isEnableGeo + ", " + isLocation + ", '" + draft.addrStr + "', " + draft.menuId + ", '"
                + draft.groupId + "', '" + draft.groupName
                + "');";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新草稿
     *
     * @param draft 草稿
     */
    public void updateDraft(Draft draft) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int isEnableGeo = draft.isEnableGeo ? 1 : 0;
        int isLocation = draft.isLocation ? 1 : 0;
        String sql = "UPDATE " + DBInfo.Table.DRAFT_TB_NAME + " SET "
                + Draft.CONTENT + "='" + draft.content + "', "
                + Draft.PHOTO_LIST + "='" + Draft.photoList2String(draft.photoList) + "', "
                + Draft.LATITUDE + "=" + draft.latitude + ", " + Draft.LONGITUDE + "=" + draft.longitude + ", "
                + Draft.IS_ENABLE_GEO + "=" + isEnableGeo + ", " + Draft.IS_LOCATION + "=" + isLocation + ", "
                + Draft.ADDR_STR + "='" + draft.addrStr + "', " + Draft.MENU_ID + "=" + draft.menuId + ", "
                + Draft.GROUP_ID + "='" + draft.groupId + "', " + Draft.GROUP_NAME + "='" + draft.groupName
                + "' WHERE " + Draft.ID + "=" + draft.id;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定草稿
     *
     * @param id 草稿ID
     */
    public void deleteDraftById(int id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.DRAFT_TB_NAME + " WHERE " + Draft.ID + " = " + id;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除所有草稿
     */
    public void deleteAllDrafts() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBInfo.Table.DRAFT_TB_NAME);
        db.close();
    }

    /**
     * 根据用户Id(uid)获取草稿
     *
     * @param uid 用户Id
     * @return 该用户的所有草稿，没有找到返回null
     */
    public List<Draft> getDraftsByUid(String uid) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.DRAFT_TB_NAME, columns,
                Draft.UID + "=?", new String[]{uid}, null, null, null);

        List<Draft> draftList = null;
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                draftList = new ArrayList<>(count);
                Draft draft = null;
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Draft.ID));
                    String content = cursor.getString(cursor.getColumnIndex(Draft.CONTENT));
                    List<Photo> photoList = Draft.string2PhotoList(cursor.getString(cursor.getColumnIndex(Draft.PHOTO_LIST)));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(Draft.LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(Draft.LONGITUDE));
                    boolean isEnableGeo = cursor.getInt(cursor.getColumnIndex(Draft.IS_ENABLE_GEO)) == 1;
                    boolean isLocation = cursor.getInt(cursor.getColumnIndex(Draft.IS_LOCATION)) == 1;
                    String addrStr = cursor.getString(cursor.getColumnIndex(Draft.ADDR_STR));
                    int menuId = cursor.getInt(cursor.getColumnIndex(Draft.MENU_ID));
                    String groupId = cursor.getString(cursor.getColumnIndex(Draft.GROUP_ID));
                    String groupName = cursor.getString(cursor.getColumnIndex(Draft.GROUP_NAME));

                    draft = new Draft(id, uid, content, photoList, latitude, longitude, isEnableGeo,
                            isLocation, addrStr, menuId, groupId, groupName);
                    draftList.add(draft);
                }
            }
            cursor.close();
        }
        db.close();
        return draftList;
    }

}
