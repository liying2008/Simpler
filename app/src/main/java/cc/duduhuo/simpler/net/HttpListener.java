package cc.duduhuo.simpler.net;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 19:47
 * 版本：1.0
 * 描述：网络请求相关回调接口
 * 备注：
 * =======================================================
 */
public interface HttpListener {
    /**
     * 请求成功
     * @param response
     */
    void onResponse(String response);

    /**
     * 请求失败
     */
    void onFailure();
}
