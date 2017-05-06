package cc.duduhuo.simpler.bean.weibo;

import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:21
 * 版本：1.0
 * 描述：H5网页接口User类
 * 备注：
 * =======================================================
 */
public class User {
    public long id;
    public String screen_name;
    public String profile_image_url;
    public String profile_url;
    public int statuses_count;
    public boolean verified;
    public int verified_type;
    public String verified_reason;
    public String description;
    public String gender;
    public int mbtype;
    public int urank;
    public int mbrank;
    public boolean follow_me;
    public boolean following;
    public int followers_count;
    public int follow_count;
    public String cover_image_phone;

    public static User parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        User user = new User();
        user.id = obj.optLong("id", 0L);
        user.screen_name = obj.optString("screen_name", "");
        user.profile_image_url = obj.optString("profile_image_url", "");
        user.profile_url = obj.optString("profile_url", "");
        user.statuses_count = obj.optInt("statuses_count", 0);
        user.verified = obj.optBoolean("verified", false);
        user.verified_type = obj.optInt("verified_type", -1);
        user.verified_reason = obj.optString("verified_reason", "");
        user.description = obj.optString("description", "");
        user.gender = obj.optString("gender", "");
        user.mbtype = obj.optInt("mbtype", 0);
        user.urank = obj.optInt("urank", 0);
        user.mbrank = obj.optInt("mbrank", 0);
        user.follow_me = obj.optBoolean("follow_me", false);
        user.following = obj.optBoolean("following", false);
        user.followers_count = obj.optInt("followers_count", 0);
        user.follow_count = obj.optInt("follow_count", 0);
        user.cover_image_phone = obj.optString("cover_image_phone", "");
        return user;
    }
}
