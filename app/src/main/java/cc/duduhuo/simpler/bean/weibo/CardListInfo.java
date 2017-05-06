package cc.duduhuo.simpler.bean.weibo;

import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:25
 * 版本：1.0
 * 描述：H5接口微博列表信息类
 * 备注：
 * =======================================================
 */
public class CardListInfo {
    public String v_p;
    public String statistics_from;
    public String containerid;
    public String title_top;
    public int total;
    public int show_style;
    public long starttime;
    public int can_shared;
    public int since_id;
    public int page;

    public static CardListInfo parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        CardListInfo info = new CardListInfo();
        info.v_p = obj.optString("v_p", "");
        info.statistics_from = obj.optString("statistics_from", "");
        info.containerid = obj.optString("containerid", "");
        info.title_top = obj.optString("title_top", "");
        info.total = obj.optInt("total", 0);
        info.show_style = obj.optInt("show_style", 0);
        info.starttime = obj.optLong("starttime", 0L);
        info.can_shared = obj.optInt("can_shared", 0);
        info.since_id = obj.optInt("since_id", 0);
        info.page = obj.optInt("page", 0);
        return info;
    }
}
