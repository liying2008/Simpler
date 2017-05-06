package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnFavoriteListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/17 14:19
 * 版本：1.0
 * 描述：微博收藏相关操作类
 * 备注：
 * =======================================================
 */
public class FavoriteOp implements OnFavoriteListener {
    private Context mContext;
    private FavoritesAPI mFApi;
    private OnFavoriteOpResultListener mListener;

    public FavoriteOp(Context context) {
        this.mContext = context;
        mFApi = new FavoritesAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
    }

    @Override
    public void onCreate(long sid) {
        mFApi.create(sid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            if (mListener != null) {
                                mListener.onCreateSuccess();
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onCreateFailure("error_code:" + obj.optString("error_code"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onCreateFailure(e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code");
                    if (20704 == errorCode) {
                        if (mListener != null) {
                            mListener.onCreateFailure("该微博已经收藏过了");
                            return;
                        }
                    } else if (20101 == errorCode) {
                        if (mListener != null) {
                            mListener.onCreateFailure("微博不存在");
                            return;
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (mListener != null) {
                    mListener.onCreateFailure(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy(long sid) {
        mFApi.destroy(sid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            if (mListener != null) {
                                mListener.onDestroySuccess();
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onDestroyFailure("error_code:" + obj.optString("error_code"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(e.getMessage());
                            int errorCode = obj.optInt("error_code");
                            if (20705 == errorCode) {
                                if (mListener != null) {
                                    mListener.onCreateFailure("尚未收藏该微博");
                                    return;
                                }
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (mListener != null) {
                            mListener.onDestroyFailure(e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onDestroyFailure(e.getMessage());
                }
            }
        });
    }

    public void setOnFavoriteOpResultListener(OnFavoriteOpResultListener listener) {
        this.mListener = listener;
    }

    /**
     * 操作结果回调
     */
    public interface OnFavoriteOpResultListener {
        /**
         * 收藏成功
         */
        void onCreateSuccess();

        /**
         * 收藏失败
         */
        void onCreateFailure(String msg);

        /**
         * 取消收藏成功
         */
        void onDestroySuccess();

        /**
         * 取消收藏失败
         */
        void onDestroyFailure(String msg);
    }
}
