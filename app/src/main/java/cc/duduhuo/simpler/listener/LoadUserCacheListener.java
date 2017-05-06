package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 14:10
 * 版本：1.0
 * 描述：加载用户信息缓存回调接口
 * 备注：
 * =======================================================
 */
public interface LoadUserCacheListener {
    /**
     * 缓存文件读取完毕
     * @param cacheStr
     */
    void userCacheLoaded(String cacheStr);

    /**
     * 无微博缓存
     */
    void noUserCache();
}
