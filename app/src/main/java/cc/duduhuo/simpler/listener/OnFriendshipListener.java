package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 14:27
 * 版本：1.0
 * 描述：用户关系操作监听接口
 * 备注：
 * =======================================================
 */
public interface OnFriendshipListener {
    /**
     * 关注某用户
     *
     * @param position
     * @param uid
     * @param screenName
     */
    void onCreate(int position, long uid, String screenName);

    /**
     * 取消关注某用户
     *
     * @param position
     * @param uid
     * @param screenName
     */
    void onDestroy(int position, long uid, String screenName);
}
