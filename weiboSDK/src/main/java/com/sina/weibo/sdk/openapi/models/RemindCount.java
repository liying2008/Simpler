package com.sina.weibo.sdk.openapi.models;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/24 14:33
 * 版本：1.0
 * 描述：微博提醒（未读消息）数
 * 备注：
 * =======================================================
 */
public class RemindCount {
    /** 新粉丝数 */
    public int follower;
    /** 新评论数 */
    public int cmt;
    /** 关注人私信数 */
    public int dm;
    public int chat_group_pc;
    public int chat_group_client;
    /** 新提及我的微博数 */
    public int mention_status;
    /** 新提及我的评论数 */
    public int mention_cmt;
    /** 新邀请未读数 */
    public int invite;
    /** 新勋章数 */
    public int badge;
    /** 新赞数 */
    public int attitude;
    public int tome;
    /** 未关注人私信数 */
    public int msgbox;
    public int page_follower;
    public int all_mention_status;
    public int attention_mention_status;
    public int all_mention_cmt;
    public int attention_mention_cmt;
    public int all_cmt;
    public int attention_cmt;
    public int all_follower;
    public int attention_follower;
    public int page_friends_to_me;
    public int page_group_to_me;
    public int hot_status;
    public int chat_group_total;
    public int message_flow_aggregate;
    public int message_flow_unaggregate;
    public int voip;
    public int message_flow_agg_at;
    public int message_flow_agg_repost;
    public int message_flow_agg_comment;
    public int message_flow_agg_attitude;
    public int pc_viedo;
    public int status_24unread;
    public int message_flow_aggr_wild_card;
    public int message_flow_unaggr_wild_card;
    public int fans_group_unread;
    public int message_flow_follow;
    /** 新微博未读数 */
    public int status;
    public int sys_notice;

    public static RemindCount parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        RemindCount count = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            count = parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static RemindCount parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        RemindCount count = new RemindCount();
        count.follower = jsonObject.optInt("follower", 0);
        count.cmt = jsonObject.optInt("cmt", 0);
        count.dm = jsonObject.optInt("dm", 0);
        count.chat_group_pc = jsonObject.optInt("chat_group_pc", 0);
        count.chat_group_client = jsonObject.optInt("chat_group_client", 0);
        count.mention_status = jsonObject.optInt("mention_status", 0);
        count.mention_cmt = jsonObject.optInt("mention_cmt", 0);
        count.invite = jsonObject.optInt("invite", 0);
        count.badge = jsonObject.optInt("badge", 0);
        count.attitude = jsonObject.optInt("attitude", 0);
        count.tome = jsonObject.optInt("tome", 0);
        count.msgbox = jsonObject.optInt("msgbox", 0);
        count.page_follower = jsonObject.optInt("page_follower", 0);
        count.all_mention_status = jsonObject.optInt("all_mention_status", 0);
        count.attention_mention_status = jsonObject.optInt("attention_mention_status", 0);
        count.all_mention_cmt = jsonObject.optInt("all_mention_cmt", 0);
        count.attention_mention_cmt = jsonObject.optInt("attention_mention_cmt", 0);
        count.all_cmt = jsonObject.optInt("all_cmt", 0);
        count.attention_cmt = jsonObject.optInt("attention_cmt", 0);
        count.all_follower = jsonObject.optInt("all_follower", 0);
        count.attention_follower = jsonObject.optInt("attention_follower", 0);
        count.page_friends_to_me = jsonObject.optInt("page_friends_to_me", 0);
        count.page_friends_to_me = jsonObject.optInt("page_friends_to_me", 0);
        count.page_group_to_me = jsonObject.optInt("page_group_to_me", 0);
        count.hot_status = jsonObject.optInt("hot_status", 0);
        count.chat_group_total = jsonObject.optInt("chat_group_total", 0);
        count.message_flow_aggregate = jsonObject.optInt("message_flow_aggregate", 0);
        count.message_flow_unaggregate = jsonObject.optInt("message_flow_unaggregate", 0);
        count.voip = jsonObject.optInt("voip", 0);
        count.message_flow_agg_at = jsonObject.optInt("message_flow_agg_at", 0);
        count.message_flow_agg_repost = jsonObject.optInt("message_flow_agg_repost", 0);
        count.message_flow_agg_comment = jsonObject.optInt("message_flow_agg_comment", 0);
        count.message_flow_agg_attitude = jsonObject.optInt("message_flow_agg_attitude", 0);
        count.pc_viedo = jsonObject.optInt("pc_viedo", 0);
        count.status_24unread = jsonObject.optInt("status_24unread", 0);
        count.message_flow_aggr_wild_card = jsonObject.optInt("message_flow_aggr_wild_card", 0);
        count.message_flow_unaggr_wild_card = jsonObject.optInt("message_flow_unaggr_wild_card", 0);
        count.fans_group_unread = jsonObject.optInt("fans_group_unread", 0);
        count.message_flow_follow = jsonObject.optInt("message_flow_follow", 0);
        count.status = jsonObject.optInt("status", 0);
        count.sys_notice = jsonObject.optInt("sys_notice", 0);
        return count;
    }
}
