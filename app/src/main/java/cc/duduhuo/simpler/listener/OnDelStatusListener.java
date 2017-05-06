package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/23 21:35
 * 版本：1.0
 * 描述：删除（自己发布的）一条微博监听接口
 * 备注：
 * =======================================================
 */
public interface OnDelStatusListener {
    /**
     * 删除一条微博
     *
     * @param sid      微博Id
     * @param position
     */
    void onDestroy(long sid, int position);
}
