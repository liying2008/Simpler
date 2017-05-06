package cc.duduhuo.simpler.bean;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/4 19:53
 * 版本：1.0
 * 描述：菜单项（功能项和分组项）
 * 备注：
 * =======================================================
 */
public class MenuItem {
    public static final int TYPE_TITLE = 0x0000;
    public static final int TYPE_MENU = 0x0001;
    public static final int MENU_ID_GROUP = -1;
    public static final int MENU_ID_ALL_STATUS = 0;
    public static final int MENU_ID_HOT = 1;
    public static final int MENU_ID_MESSAGE = 2;
    public static final int MENU_ID_SEARCH = 3;
    public static final int MENU_ID_FAVORITE = 4;
    public static final int MENU_ID_DRAFT = 5;
    public static final int MENU_ID_FRIEND_STATUS = 6;
    public static final int MENU_ID_ALL_VISIBLE = 7;
    public static final int MENU_ID_NONE = 100;

    /** 类型（标题/菜单项） */
    public int type;
    /** 菜单项名称 */
    public String name;
    /** 分组Id */
    public String groupId;
    /** 菜单项图标 */
    public int resId;
    /**
     * 菜单Id             <br />
     * -1：微博分组      <br />
     * 0：全部微博       <br />
     * 1：热门             <br />
     * 2：消息             <br />
     * 3：搜索             <br />
     * 4：我的收藏           <br />
     * 5：草稿箱            <br />
     * 6：朋友圈            <br />
     * 7：所有人可见      <br />
     * 100：无              <br />
     */
    public int menuId;

    public MenuItem() {
    }

    /**
     * 标题项的构造方法
     * @param name
     */
    public MenuItem(String name) {
        this.type = TYPE_TITLE;
        this.name = name;
        this.groupId = "";
        this.resId = -1;
        this.menuId = MENU_ID_NONE;
    }

    /**
     * 菜单项的构造方法
     * @param type
     * @param name
     * @param groupId
     * @param resId
     * @param menuId
     */
    public MenuItem(int type, String name, String groupId, int resId, int menuId) {
        this.type = type;
        this.name = name;
        this.groupId = groupId;
        this.resId = resId;
        this.menuId = menuId;
    }
}
