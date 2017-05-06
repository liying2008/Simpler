package cc.duduhuo.simpler.listener;


/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/8 0:38
 * 版本：1.0
 * 描述：分组更改回调接口
 * 备注：
 * =======================================================
 */
public interface GroupChangeListener {
    /**
     * 点击了菜单（分组）
     *
     * @param menuId    菜单Id
     * @param groupId   分组Id
     * @param groupName 分组名称
     */
    void onGroupChange(int menuId, String groupId, String groupName);
}
