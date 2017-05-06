package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 16:50
 * 版本：1.0
 * 描述：分组成员管理实现接口
 * 备注：
 * =======================================================
 */
public interface OnGroupMemberMgrListener {
    /**
     * 删除好友分组内的关注用户
     *
     * @param uid      需要删除的用户的UID
     * @param listId   好友分组ID
     * @param position
     */
    void onDestroy(long uid, long listId, int position);

    /**
     * 添加关注用户到好友分组
     *
     * @param uid    需要添加的用户的UID
     * @param listId 好友分组ID
     */
    void onAdd(long uid, long listId);
}
