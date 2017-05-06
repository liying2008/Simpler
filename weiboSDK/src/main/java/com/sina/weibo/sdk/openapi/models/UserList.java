package com.sina.weibo.sdk.openapi.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 16:16
 * 版本：1.0
 * 描述：用户List
 * 备注：
 * =======================================================
 */
public class UserList {
    /** 用户列表 */
    public ArrayList<User> users;
    public int next_cursor;
    public int previous_cursor;
    public long total_number;

    public static UserList parse(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(s);
            return parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserList parse(JSONObject jsonObject) {
        UserList userList = new UserList();
        userList.next_cursor = jsonObject.optInt("next_cursor", 0);
        userList.previous_cursor = jsonObject.optInt("previous_cursor", 0);
        userList.total_number = jsonObject.optLong("total_number", 0L);
        JSONArray array = jsonObject.optJSONArray("users");
        if (array != null && array.length() > 0) {
            userList.users = new ArrayList<>(array.length());
            User user = null;
            for (int i = 0; i < array.length(); i++) {
                user = User.parse(array.optJSONObject(i));
                if (user != null) {
                    userList.users.add(user);
                }
            }
        }
        return userList;
    }
}
