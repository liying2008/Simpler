package cc.duduhuo.simpler.bean.weibo;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:22
 * 版本：1.0
 * 描述：H5网页接口微博列表类
 * 备注：
 * =======================================================
 */
public class CardList {
    public CardListInfo cardlistInfo;
    public ArrayList<Card> cards;
    public int ok;
    public int seeLevel;
    public int showAppTips;
    public String scheme;

    public static CardList parse(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        CardList cardList = null;
        try {
            JSONObject obj = new JSONObject(s);
            cardList = parse(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardList;
    }

    public static CardList parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        CardList cardList = new CardList();
        cardList.cardlistInfo = CardListInfo.parse(obj.optJSONObject("cardlistInfo"));

        JSONArray array = obj.optJSONArray("cards");
        if (array != null && array.length() > 0) {
            cardList.cards = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                Card card = Card.parse(array.optJSONObject(i));
                if (card != null) {
                    cardList.cards.add(card);
                }
            }
        }
        cardList.ok = obj.optInt("ok", 0);
        cardList.seeLevel = obj.optInt("seeLevel", 0);
        cardList.showAppTips = obj.optInt("showAppTips", 0);
        cardList.scheme = obj.optString("scheme", "");
        return cardList;
    }
}
