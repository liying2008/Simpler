package cc.duduhuo.simpler.bean.weibo;

import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:56
 * 版本：1.0
 * 描述：H5网页接口CardGroup类
 * 备注：
 * =======================================================
 */
public class CardGroup {
    public int card_type;
    public String card_type_name;
    public String itemid;
    public int display_arrow;
    public int show_type;
    public MBlog mblog;
    public String scheme;
    public String openurl;

    public static CardGroup parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        CardGroup cardGroup = new CardGroup();
        cardGroup.card_type = obj.optInt("card_type", 0);
        cardGroup.card_type_name = obj.optString("card_type_name", "");
        cardGroup.itemid = obj.optString("itemid", "");
        cardGroup.display_arrow = obj.optInt("display_arrow", 0);
        cardGroup.show_type = obj.optInt("show_type", 0);
        cardGroup.mblog = MBlog.parse(obj.optJSONObject("mblog"));
        cardGroup.scheme = obj.optString("scheme", "");
        cardGroup.openurl = obj.optString("openurl", "");
        return cardGroup;
    }
}
