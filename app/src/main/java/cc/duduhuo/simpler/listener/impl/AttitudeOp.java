package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.AttitudeAPI;
import com.sina.weibo.sdk.openapi.models.Attitude;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnAttitudeListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 20:40
 * 版本：1.0
 * 描述：微博点赞操作类
 * 备注：
 * =======================================================
 */
public class AttitudeOp implements OnAttitudeListener {
    private Context mContext;
    private AttitudeAPI mAApi;
    private AttitudeOpResultListener mListener;

    public AttitudeOp(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(final long sid, String attitude, final TextView tvAttitude) {
        if (mAApi == null) {
            mAApi = new AttitudeAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mAApi.create(sid, attitude, new RequestListener() {
            @Override
            public void onComplete(String s) {
                Attitude atti = Attitude.parse(s);
                if (atti != null) {
                    if ("heart".equals(atti.attitude)) {
                        if ("heart".equals(atti.last_attitude)) {
                            // 上次赞过了
                            if (mListener != null) {
                                mListener.onCreateSuccess(true, sid, atti.id, tvAttitude);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onCreateSuccess(false, sid, atti.id, tvAttitude);
                            }
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onCreateFailure("未知错误");
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onCreateFailure("点赞失败");
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onCreateFailure("点赞失败");
                }
            }
        });
    }

    @Override
    public void onDestroy(final long sid, final long attid, final TextView tvAttitude) {
        if (mAApi == null) {
            mAApi = new AttitudeAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mAApi.destroy(sid, attid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        boolean result = obj.optBoolean("result", false);
                        if (result) {
                            if (mListener != null) {
                                mListener.onDestroySuccess(sid, attid, tvAttitude);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onDestroyFailure("取消赞失败");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onDestroyFailure("取消赞失败");
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onDestroyFailure("取消赞失败");
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onDestroyFailure("取消赞失败");
                }
            }
        });
    }

    public void setAttitudeOpResultListener(AttitudeOpResultListener listener) {
        this.mListener = listener;
    }

    /**
     * 操作结果回调
     */
    public interface AttitudeOpResultListener {
        /**
         * 点赞成功
         *
         * @param lastHeart  是否上次已经赞过了
         * @param sid        微博ID
         * @param aid        Attitude ID
         * @param tvAttitude
         */
        void onCreateSuccess(boolean lastHeart, long sid, long aid, TextView tvAttitude);

        /** 点赞失败 */
        void onCreateFailure(String msg);

        /**
         * 取消赞成功
         *
         * @param sid        微博ID
         * @param aid        Attitude ID
         * @param tvAttitude
         */
        void onDestroySuccess(long sid, long aid, TextView tvAttitude);

        /** 取消赞失败 */
        void onDestroyFailure(String msg);
    }
}
