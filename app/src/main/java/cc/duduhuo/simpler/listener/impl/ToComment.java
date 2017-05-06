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
 * 日期：2017/3/31 20:06
 * 版本：1.0
 * 描述：评论一条微博
 * 备注：
 * =======================================================
 */
public class ToComment implements View.OnClickListener {
    private Context mContext;
    private long mSid;

    public ToComment(Context context, long sid) {
        this.mContext = context;
        this.mSid = sid;
    }

    @Override
    public void onClick(View v) {
        if (NetWorkUtils.isConnectedByState(mContext)) {
            mContext.startActivity(WBCommentActivity.newIntent(mContext, mSid));
        } else {
            AppToast.showToast(R.string.network_unavailable);
        }
    }
}
