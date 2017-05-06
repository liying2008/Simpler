package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/17 14:13
 * 版本：1.0
 * 描述：收藏微博相关操作接口
 * 备注：
 * =======================================================
 */
public interface OnFavoriteListener {
    /**
     * 收藏微博
     * @param sid 微博Id
     */
    void onCreate(long sid);

    /**
     * 取消收藏微博
     * @param sid 微博Id
     */
    void onDestroy(long sid);
}
