package cc.duduhuo.simpler.bean.weibo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:29
 * 版本：1.0
 * 描述：H5网页接口Card类
 * 备注：
 * =======================================================
 */
public class Card {
    public int card_type;
    public int show_type;
    public ArrayList<CardGroup> card_group;
    public String openurl;
    // 热门微博有这个字段
    public MBlog mblog;

    public static Card parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        Card card = new Card();
        card.card_type = obj.optInt("card_type", 0);
        card.show_type = obj.optInt("show_type", 0);

        JSONArray cardGroups = obj.optJSONArray("card_group");
        if (cardGroups != null && cardGroups.length() > 0) {
            card.card_group = new ArrayList<>(cardGroups.length());
            for (int i = 0; i < cardGroups.length(); i++) {
                CardGroup cardGroup = CardGroup.parse(cardGroups.optJSONObject(i));
                if (cardGroup != null) {
                    card.card_group.add(cardGroup);
                }
            }
        }
        card.openurl = obj.optString("openurl", "");

        card.mblog = MBlog.parse(obj.optJSONObject("mblog"));
        return card;
    }
}
