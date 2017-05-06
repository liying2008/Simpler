package cc.duduhuo.simpler.net;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 21:05
 * 版本：1.0
 * 描述：链接是否有重定向回调接口
 * 备注：
 * =======================================================
 */
public interface MovedTempListener {
    /**
     * 有重定向
     * @param url 重定向的地址
     */
    void onRedirect(String url);

    /**
     * 无重定向
     */
    void onNo();
}
