package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnDelStatusListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/23 23:19
 * 版本：1.0
 * 描述：删除微博操作类
 * 备注：
 * =======================================================
 */
public class DelCommentOp implements OnDelStatusListener {
    private Context mContext;
    private CommentsAPI mCApi;
    private OnDelCommentOpListener mListener;

    public DelCommentOp(Context context) {
        this.mContext = context;
    }

    @Override
    public void onDestroy(long cid, final int position) {
        if (mCApi == null) {
            mCApi = new CommentsAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mCApi.destroy(cid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            // 成功删除
                            if (mListener != null) {
                                mListener.onSuccess(position);
                            }
                        } else {
                            if (mListener != null) {
                                mListener.onFailure(mContext.getString(R.string.delete_failure));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onFailure(e.getMessage());
                        }
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

    public void setOnDelCommentOpListener(OnDelCommentOpListener listener) {
        this.mListener = listener;
    }

    public interface OnDelCommentOpListener {
        /**
         * 成功删除
         *
         * @param position
         */
        void onSuccess(int position);

        /**
         * 删除失败
         *
         * @param msg
         */
        void onFailure(String msg);
    }
}
