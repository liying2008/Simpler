package cc.duduhuo.simpler.bean;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/6 14:21
 * 版本：1.0
 * 描述：开源库
 * 备注：
 * =======================================================
 */
public class OpenSourceLib {
    /** 开源库名称 */
    public String name;
    /** 作者 */
    public String author;
    /** 简要描述 */
    public String description;
    /** 开源地址 */
    public String url;

    public OpenSourceLib(String name, String author, String description, String url) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.url = url;
    }

    public OpenSourceLib() {

    }
}
