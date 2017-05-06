package cc.duduhuo.simpler.listener.impl;

import android.content.Context;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.activity.WBLoginActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.OnShortURLListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/19 16:41
 * 版本：1.0
 * 描述：短链接操作类
 * 备注：
 * =======================================================
 */
public class ShortUrlOp implements OnShortURLListener {
    private Context mContext;
    private ShortUrlAPI mUApi;
    private OnShortUrlOpResultListener mListener;

    public ShortUrlOp(Context context, ShortUrlAPI uApi) {
        this.mContext = context;
        this.mUApi = uApi;
    }

    @Override
    public void expand(String shortUrl) {
        mUApi.expand(new String[]{shortUrl}, new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.isNull("error")) {
                        JSONArray array = jsonObject.getJSONArray("urls");
                        if (array != null && array.length() > 0) {
                            JSONObject obj = (JSONObject) array.get(0);
                            String longUrl = obj.getString("url_long");
                            if (mListener != null) {
                                mListener.onSuccess(longUrl);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onFailure("无结果");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        JSONObject obj = new JSONObject(e.getMessage());
                        int errorCode = obj.optInt("error_code");
                        if (errorCode == 10006 || errorCode == 21332) {
                            // 授权过期
                            AppToast.showToast("应用授权过期，请重新授权");
                            if (!BaseConfig.sTokenExpired) {
                                BaseConfig.sTokenExpired = true;
                                App.getInstance().finishAllActivities();
                                mContext.startActivity(WBLoginActivity.newIntent(mContext));
                                return;
                            }
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    if (mListener != null) {
                        mListener.onFailure(e.getMessage());
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onFailure(e.getMessage());
                }
            }
        });
    }

    public void setOnShortUrlOpResultListener(OnShortUrlOpResultListener listener) {
        this.mListener = listener;
    }

    public interface OnShortUrlOpResultListener {
        /**
         * 请求结果成功
         * @param longUrl 长链接
         */
        void onSuccess(String longUrl);

        /**
         * 请求结果失败
         * @param msg 失败信息
         */
        void onFailure(String msg);
    }
}
