package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cc.duduhuo.simpler.activity.UserHomepageActivity;
import cc.duduhuo.simpler.activity.WBUserHomeActivity;
import cc.duduhuo.simpler.config.BaseConfig;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/27 16:37
 * 版本：1.0
 * 描述：打开用户中心（主页）Activity
 * 备注：
 * =======================================================
 */
public class ViewUserOp implements View.OnClickListener {
    private Context mContext;
    /**
     * 用户昵称
     */
    private String mScreenName;

    public ViewUserOp(Context context, String screenName) {
        this.mContext = context;
        this.mScreenName = screenName;
    }

    @Override
    public void onClick(View v) {
        if (mScreenName.equals(BaseConfig.sUser.screen_name)) {
            // 进入个人中心
            mContext.startActivity(UserHomepageActivity.newIntent(mContext));
        } else {
            // 打开用户信息Activity
            Intent intent = WBUserHomeActivity.newIntent(mContext, mScreenName);
            mContext.startActivity(intent);
        }
    }
}
