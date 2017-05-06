package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 22:45
 * 版本：1.0
 * 描述：分组管理实现接口
 * 备注：
 * =======================================================
 */
public interface OnGroupMgrListener {
    /**
     * 更新好友分组
     *
     * @param listId      好友分组ID
     * @param position
     * @param name        好友分组的名称，不超过10个汉字，20个半角字符。
     * @param description 好友分组的描述，不超过70个汉字，140个半角字符。
     * @param tags        好友分组的标签，多个之间用逗号分隔，最多不超过10个，每个不超过7个汉字，14个半角字符。
     */
    void onUpdate(long listId, int position, String name, String description, String tags);

    /**
     * 新增好友分组
     *
     * @param name        好友分组的名称，不超过10个汉字，20个半角字符。
     * @param description 好友分组的描述，不超过70个汉字，140个半角字符。
     * @param tags        好友分组的标签，多个之间用逗号分隔，最多不超过10个，每个不超过7个汉字，14个半角字符。
     */
    void onCreate(String name, String description, String tags);

    /**
     * 删除好友分组
     *
     * @param listId   好友分组ID
     * @param position
     */
    void onDestroy(long listId, int position);
}
