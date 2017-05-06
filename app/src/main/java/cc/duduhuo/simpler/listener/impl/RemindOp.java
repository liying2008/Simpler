package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.RemindCount;
import com.sina.weibo.sdk.openapi.rm.RemindAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.activity.WBLoginActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnRemindListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/25 16:58
 * 版本：1.0
 * 描述：微博提醒操作类
 * 备注：
 * =======================================================
 */
public class RemindOp implements OnRemindListener {
    private static final String TAG = "Remind";
    private Context mContext;
    private RemindAPI mRApi;
    private OnRemindOpResultListener mListener;

    public RemindOp(Context context) {
        this.mContext = context;
    }

    @Override
    public void onUnreadCount(long uid) {
        if (mRApi == null) {
            mRApi = new RemindAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mRApi.unreadCount(uid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    RemindCount count = RemindCount.parse(s);
                    if (count != null) {
                        if (mListener != null) {
                            mListener.onUnreadCountSuccess(count);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onUnreadCountFailure("解析失败");
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onUnreadCountFailure("请求结果为空");
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
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
                    mListener.onUnreadCountFailure(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onSetCount(String type) {
        if (mRApi == null) {
            mRApi = new RemindAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mRApi.setCount(type, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                Log.d(TAG, s);
                if (TextUtils.isEmpty(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        boolean result = jsonObject.optBoolean("result", false);
                        if (result) {
                            if (mListener != null) {
                                mListener.onSetCountSuccess();
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onSetCountFailure("清零失败");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onSetCountFailure(e.getMessage());
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onSetCountFailure("请求结果为空");
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onSetCountFailure(e.getMessage());
                }
            }
        });
    }

    public void setOnRemindOpResultListener(OnRemindOpResultListener listener) {
        this.mListener = listener;
    }

    public interface OnRemindOpResultListener {
        /** 请求未读消息数成功 */
        void onUnreadCountSuccess(RemindCount remindCount);

        /** 请求未读消息数失败 */
        void onUnreadCountFailure(String msg);

        /** 请求清除未读消息数成功 */
        void onSetCountSuccess();

        /** 请求清除未读消息数失败 */
        void onSetCountFailure(String msg);
    }
}
