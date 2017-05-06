package com.sina.weibo.sdk.openapi.models;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 20:50
 * 版本：1.0
 * 描述：表态
 * 备注：
 * =======================================================
 */
public class Attitude {
    /** 表态ID */
    public long id;
    /** 创建时间 */
    public String created_at;
    /** 态度 */
    public String attitude;
    /** 类型 */
    public int attitude_type;
    /** 上次的态度 */
    public String last_attitude;
    public int source_allowclick;
    public int source_type;
    /** 态度来源 */
    public String source;
    /** 表态的用户 */
    public User user;
    /** 被表态的微博 */
    public Status status;

    public static Attitude parse(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        Attitude attitude = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            attitude = parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attitude;
    }

    public static Attitude parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        Attitude attitude = new Attitude();
        attitude.id = jsonObject.optLong("id", 0L);
        attitude.created_at = jsonObject.optString("created_at", "");
        attitude.attitude = jsonObject.optString("attitude", "");
        attitude.attitude_type = jsonObject.optInt("attitude_type", 0);
        attitude.last_attitude = jsonObject.optString("last_attitude", "");
        attitude.source_allowclick = jsonObject.optInt("source_allowclick", 0);
        attitude.source_type = jsonObject.optInt("source_type", 0);
        attitude.source = jsonObject.optString("source", "");
        attitude.user = User.parse(jsonObject.optJSONObject("user"));
        attitude.status = Status.parse(jsonObject.optJSONObject("status"));
        return attitude;
    }
}
