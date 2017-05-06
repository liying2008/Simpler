package cc.duduhuo.simpler.listener;

import android.widget.TextView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 20:37
 * 版本：1.0
 * 描述：微博表态操作接口
 * 备注：
 * =======================================================
 */
public interface OnAttitudeListener {
    /**
     * 点赞
     *
     * @param sid        微博ID
     * @param attitude   [可选值：smile,naughty,surprise,sad,heart]（只有heart是赞）
     * @param tvAttitude
     */
    void onCreate(long sid, String attitude, TextView tvAttitude);

    /**
     * 取消赞
     *
     * @param sid        微博ID
     * @param attid      表态ID
     * @param tvAttitude
     */
    void onDestroy(long sid, long attid, TextView tvAttitude);
}
