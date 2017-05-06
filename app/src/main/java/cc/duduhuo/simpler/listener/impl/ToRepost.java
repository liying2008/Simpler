package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.view.View;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.WBCommentActivity;
import cc.duduhuo.simpler.util.NetWorkUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 20:11
 * 版本：1.0
 * 描述：转发一条微博
 * 备注：
 * =======================================================
 */
public class ToRepost implements View.OnClickListener {
    private Context mContext;
    private long mSid;
    private String mScreenName;
    private String mStatus;
    private boolean mRetweeted;

    public ToRepost(Context context, long id, String screenName, String status, boolean retweeted) {
        this.mSid = id;
        this.mContext = context;
        this.mScreenName = screenName;
        this.mStatus = status;
        this.mRetweeted = retweeted;
    }

    @Override
    public void onClick(View v) {
        if (NetWorkUtils.isConnectedByState(mContext)) {
            mContext.startActivity(WBCommentActivity.newIntent(mContext, mSid, mScreenName, mStatus,
                    mRetweeted, true));
        } else {
            AppToast.showToast(R.string.network_unavailable);
        }
    }
}
