package cc.duduhuo.simpler.bean.weibo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 13:21
 * 版本：1.0
 * 描述：H5网页接口微博类
 * 备注：
 * =======================================================
 */
public class MBlog {
    public String created_at;
    public String id;
    public String mid;
    public String idstr;
    public String text;
    public int textLength;
    public String source;
    public boolean favorited;
    public User user;
    public MBlog retweeted_status;
    public int reposts_count;
    public int comments_count;
    public int attitudes_count;
    public boolean isLongText;
    public String rid;
    public int status;
    public String itemid;
    public String raw_text;
    public String bid;
    public ArrayList<String> pics;

    public static MBlog parse(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        MBlog mBlog = new MBlog();
        mBlog.created_at = obj.optString("created_at", "");
        mBlog.id = obj.optString("id", "");
        mBlog.mid = obj.optString("mid", "");
        mBlog.idstr = obj.optString("idstr", "");
        mBlog.text = obj.optString("text", "");
        mBlog.textLength = obj.optInt("textLength", 0);
        mBlog.source = obj.optString("source", "");
        mBlog.favorited = obj.optBoolean("favorited", false);
        mBlog.user = User.parse(obj.optJSONObject("user"));
        mBlog.retweeted_status = MBlog.parse(obj.optJSONObject("retweeted_status"));
        mBlog.reposts_count = obj.optInt("reposts_count", 0);
        mBlog.comments_count = obj.optInt("comments_count", 0);
        mBlog.attitudes_count = obj.optInt("attitudes_count", 0);
        mBlog.isLongText = obj.optBoolean("isLongText", false);
        mBlog.rid = obj.optString("rid", "");
        mBlog.status = obj.optInt("status", 0);
        mBlog.itemid = obj.optString("itemid", "");
        mBlog.raw_text = obj.optString("raw_text", "");
        mBlog.bid = obj.optString("bid", "");
        
        JSONArray array = obj.optJSONArray("pics");
        if (array != null && array.length() > 0) {
            mBlog.pics = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj1 = array.optJSONObject(i);
                mBlog.pics.add(obj1.optString("url").replace("/orj360/", "/thumbnail/"));
            }
        }
        return mBlog;
    }
}
