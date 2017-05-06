package com.sina.weibo.sdk.openapi.legacy;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.AbsOpenAPI;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 19:49
 * 版本：1.0
 * 描述：微博表态API
 * 备注：
 * =======================================================
 */
public class AttitudeAPI extends AbsOpenAPI {
    private static final String SERVER_URL_PRIX = API_SERVER + "/attitudes";

    /**
     * 构造函数，使用各个 API 接口提供的服务前必须先获取 Token。
     *
     * @param context
     * @param appKey
     * @param accessToken
     */
    public AttitudeAPI(Context context, String appKey, Oauth2AccessToken accessToken) {
        super(context, appKey, accessToken);
    }

    /**
     * 发表或更新一条表态。
     *
     * @param sid      微博ID。
     * @param attitude [可选值：smile,naughty,surprise,sad,heart] <br />
     *                 只有heart是赞
     * @param listener
     */
    public void create(long sid, String attitude, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", sid);
        params.put("attitude", attitude);
        requestAsync(SERVER_URL_PRIX + "/create.json", params, HTTPMETHOD_POST, listener);
    }

    /**
     * 发表或更新一条表态。
     *
     * @param sid      微博ID。
     * @param attid    表态ID。
     * @param listener
     */
    public void destroy(long sid, long attid, RequestListener listener) {
        WeiboParameters params = new WeiboParameters(mAppKey);
        params.put("id", sid);
        params.put("attid", attid);
        requestAsync(SERVER_URL_PRIX + "/destroy.json", params, HTTPMETHOD_POST, listener);
    }
}
