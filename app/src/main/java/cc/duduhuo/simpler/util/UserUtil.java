package cc.duduhuo.simpler.util;


import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.bean.KeyValue;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/30 11:48
 * 版本：1.0
 * 描述：User工具类
 * 备注：
 * =======================================================
 */
public class UserUtil {
    /**
     * 将用户信息拆分成键值对
     *
     * @param user
     * @return
     */
    public static List<KeyValue> analyzeUser(User user) {
        List<KeyValue> kvs = new ArrayList<>(20);
        kvs.add(new KeyValue("用户ID", user.idstr));
        kvs.add(new KeyValue("昵称", user.screen_name));
        if (!user.screen_name.equals(user.name)) {
            // 有备注
            kvs.add(new KeyValue("备注", user.name));
        }
        if (!"".equals(user.location)) {
            kvs.add(new KeyValue("位置", user.location));
        }
        if (!"".equals(user.description)) {
            kvs.add(new KeyValue("描述", user.description));
        }
        if (!"".equals(user.url)) {
            kvs.add(new KeyValue("博客地址", user.url));
        }
        if (!"".equals(user.domain)) {
            kvs.add(new KeyValue("个性化域名", user.domain));
        }
        if (!"".equals(user.weihao)) {
            kvs.add(new KeyValue("微号", user.weihao));
        }
        if ("m".equals(user.gender)) {
            kvs.add(new KeyValue("性别", "男"));
        } else if ("f".equals(user.gender)) {
            kvs.add(new KeyValue("性别", "女"));
        } else if ("n".equals(user.gender)) {
            kvs.add(new KeyValue("性别", "未知"));
        }
        kvs.add(new KeyValue("粉丝数", String.valueOf(user.followers_count)));
        kvs.add(new KeyValue("关注数", String.valueOf(user.friends_count)));
        kvs.add(new KeyValue("微博数", String.valueOf(user.statuses_count)));
        kvs.add(new KeyValue("收藏数", String.valueOf(user.favourites_count)));
        kvs.add(new KeyValue("注册时间", NumberFormatter.dateTransfer(-1, user.created_at)));
        if (!BaseConfig.sUid.equals(user.idstr)) {
            if (user.following) {
                kvs.add(new KeyValue("关注状态", "已关注"));
            } else {
                kvs.add(new KeyValue("关注状态", "未关注"));
            }
            if (user.follow_me) {
                kvs.add(new KeyValue("是否关注我", "是"));
            } else {
                kvs.add(new KeyValue("是否关注我", "否"));
            }
        }
        if (user.verified) {
            if (user.verified_type > 0) {
                kvs.add(new KeyValue("认证状态", "企业认证"));
            } else {
                kvs.add(new KeyValue("认证状态", "个人认证"));
            }
            if (!"".equals(user.verified_reason)) {
                kvs.add(new KeyValue("认证原因", user.verified_reason));
            }
        } else {
            kvs.add(new KeyValue("认证状态", "未认证"));
        }
        kvs.add(new KeyValue("互粉数", String.valueOf(user.bi_followers_count)));
        if ("zh-cn".equals(user.lang)) {
            kvs.add(new KeyValue("当前语言版本", "简体中文"));
        } else if ("zh-tw".equals(user.lang)) {
            kvs.add(new KeyValue("当前语言版本", "繁体中文"));
        } else if ("en".equals(user.lang)) {
            kvs.add(new KeyValue("当前语言版本", "英语"));
        }
        return kvs;
    }

    /**
     * 将微博H5网页获取得到用户信息转换为User对象
     *
     * @param object JSONObject
     * @return
     */
    public static List<User> parseH5User(JSONObject object) throws JSONException {
        JSONArray array = object.optJSONArray("cards");
        List<User> users = null;
        if (array != null) {
            users = new ArrayList<>();
            int length = array.length();
            for (int i = 0; i < length; i++) {
                User user = new User();
                JSONObject o = (JSONObject) array.get(i);
                JSONObject jsonObject = o.optJSONObject("user");
                user.id = jsonObject.optLong("id");
                user.screen_name = jsonObject.optString("screen_name");
                user.profile_image_url = jsonObject.optString("profile_image_url");
                user.verified = jsonObject.optBoolean("verified");
                user.verified_type = jsonObject.optInt("verified_type");
                user.remark = jsonObject.optString("remark");
                users.add(user);
            }
        }
        return users;
    }
}
