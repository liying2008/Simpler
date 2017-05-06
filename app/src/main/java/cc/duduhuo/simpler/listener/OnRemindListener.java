package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/25 16:55
 * 版本：1.0
 * 描述：微博提醒接口
 * 备注：
 * =======================================================
 */
public interface OnRemindListener {
    /**
     * 获取某个用户的各种消息未读数
     *
     * @param uid
     */
    void onUnreadCount(long uid);

    /**
     * 对当前登录用户某一种消息未读数进行清零
     *
     * @param type 需要设置未读数计数的消息项
     */
    void onSetCount(String type);
}
