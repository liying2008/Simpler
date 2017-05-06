package cc.duduhuo.simpler.listener;

import android.view.View;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/9 10:38
 * 版本：1.0
 * 描述：微博特殊元素点击事件
 * 备注：
 * =======================================================
 */
public interface StatusItemListener {
    /**
     * AT
     * @param widget
     * @param at
     */
    void onItemAtListener(View widget, String at);

    /**
     * 微博话题
     * @param widget
     * @param topic
     */
    void onItemTopicListener(View widget, String topic);

    /**
     * 网页链接
     * @param widget
     * @param url
     */
    void onItemWebLinkListener(View widget, String url);

    /**
     * 图片链接
     * @param widget
     * @param url
     */
    void onItemPhotoLinkListener(View widget, String url);

    /**
     * 秒拍视频链接
     * @param widget
     * @param url
     */
    void onItemMiaoPaiLinkListener(View widget, String url);

    /**
     * 微博视频（url可能会重定向到秒拍）
     * @param widget
     * @param url
     */
    void onItemVideoLinkListener(View widget, String url);
}
