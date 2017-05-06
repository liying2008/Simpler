package cc.duduhuo.simpler.bean;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/15 15:38
 * 版本：1.0
 * 描述：设置选项实体
 * 备注：
 * =======================================================
 */
public class Option {
    /** 选项名称 */
    public String name;
    /** 选项值 */
    public Object value;
    /** 是否选中 */
    public boolean selected;

    public Option() {
    }

    public Option(String name, Object value, boolean selected) {
        this.name = name;
        this.value = value;
        this.selected = selected;
    }
}
