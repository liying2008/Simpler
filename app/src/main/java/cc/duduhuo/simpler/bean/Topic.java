package cc.duduhuo.simpler.bean;

import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/20 15:41
 * 版本：1.0
 * 描述：微博话题实体
 * 备注：
 * =======================================================
 */
public class Topic {
    /** 话题标题 */
    public String name;
    /** 话题显示名称 */
    public String topic;
    /** 话题图片地址 */
    public String picUrl;
    /** 话题描述1 */
    public String desc1;
    /** 话题描述2 */
    public String desc2;

    public static Topic parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }

        Topic user = new Topic();
        user.name = jsonObject.optString("card_type_name", "");
        user.topic = jsonObject.optString("title_sub", "");
        user.picUrl = jsonObject.optString("pic", "");
        user.desc1 = jsonObject.optString("desc1", "");
        user.desc2 = jsonObject.optString("desc2", "");
        if (!"".equals(user.picUrl)) {
            user.picUrl = user.picUrl.replace("/thumbnail/", "/bmiddle/");
        }
        return user;
    }
}
