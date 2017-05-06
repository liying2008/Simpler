package com.sina.weibo.sdk.openapi.rm;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/24 14:00
 * 版本：1.0
 * 描述：新浪微博消息提醒接口
 * 备注：
 * =======================================================
 */
public class RemindAPI extends AbsOpenAPI {
    private static final String API_SERVER = "https://rm.api.weibo.com/2";
    private static final String SERVER_URL_PRIX = API_SERVER + "/remind";

    public RemindAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    /**
     * 获取某个用户的各种消息未读数。
     *
     * @param uid      需要查询的用户ID
     * @param listener 异步请求回调接口
     */
    public void unreadCount(long uid, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("uid", uid);
        params.put("unread_message", 1);
        requestAsync(SERVER_URL_PRIX + "/unread_count.json", params, HTTPMETHOD_GET, listener);
    }

    /**
     * 对当前登录用户某一种消息未读数进行清零。
     *
     * @param type     需要设置未读数计数的消息项
     *                 follower：新粉丝数、cmt：新评论数、dm：新私信数、mention_status：新提及我的微博数、
     *                 mention_cmt：新提及我的评论数、group：微群消息数、notice：新通知数、
     *                 invite：新邀请数、badge：新勋章数、photo：相册消息数、close_friends_feeds：密友feeds未读数、
     *                 close_friends_mention_status：密友提及我的微博未读数、
     *                 close_friends_mention_cmt：密友提及我的评论未读数、close_friends_cmt：密友评论未读数、
     *                 close_friends_attitude：密友表态未读数、close_friends_common_cmt：密友共同评论未读数、
     *                 close_friends_invite：密友邀请未读数，一次只能操作一项。
     * @param listener 异步请求回调接口
     */
    public void setCount(String type, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("type", type);
        requestAsync(SERVER_URL_PRIX + "/set_count.json", params, HTTPMETHOD_POST, listener);
    }

}
