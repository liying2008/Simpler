package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/19 16:10
 * 版本：1.0
 * 描述：微博短链转长链
 * 备注：
 * =======================================================
 */
public interface OnShortURLListener {
    /**
     * 将一个或多个短链接还原成原始的长链接
     *
     * @param shortUrl
     */
    void expand(String shortUrl);
}
