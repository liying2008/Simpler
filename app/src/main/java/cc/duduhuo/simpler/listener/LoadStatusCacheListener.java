package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 14:10
 * 版本：1.0
 * 描述：加载微博缓存回调接口
 * 备注：
 * =======================================================
 */
public interface LoadStatusCacheListener {
    /**
     * 缓存文件读取完毕
     * @param cacheStr
     */
    void cacheLoaded(String cacheStr);

    /**
     * 无微博缓存
     */
    void noCache();
}
