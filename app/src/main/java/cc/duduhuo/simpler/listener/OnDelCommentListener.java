package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/23 23:18
 * 版本：1.0
 * 描述：删除（自己发布的）一条评论监听接口
 * 备注：
 * =======================================================
 */
public interface OnDelCommentListener {
    /**
     * 删除一条微博
     *
     * @param sid      微博Id
     * @param position
     */
    void onDestroy(long sid, int position);
}
