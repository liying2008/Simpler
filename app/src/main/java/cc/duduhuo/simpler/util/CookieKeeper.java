package cc.duduhuo.simpler.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;



/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 15:47
 * 版本：1.0
 * 描述：用户登录微博的Cookie的工具类
 * 备注：
 * =======================================================
 */
public class CookieKeeper {
    /**
     * 抽取Cookie
     *
     * @param cookieString WebView获取到的Cookie字符串
     * @return SUB Cookie
     */
    private static String extractCookie(String cookieString) {
        String cookie = cookieString;
        if (!TextUtils.isEmpty(cookieString)) {
            String[] cookies = cookieString.split(";");
            if (cookies.length > 0) {
                for (int i = 0; i < cookies.length; i++) {
//                    Log.d("Cookie", cookies[i]);
                    String[] kv = cookies[i].split("=");
                    if (kv[0].trim().equalsIgnoreCase("SUB")) {
                        // 要保存的Cookie
                        cookie = cookies[i].trim();
                    }
                }
            }
        }
        return cookie;
    }

    /**
     * 保存Cookie
     *
     * @param cookieString WebView获取到的Cookie字符串
     * @return Cookie是否有值（不为空）
     */
    public static boolean saveCookie(String cookieString) {
        String cookie = extractCookie(cookieString);
        if (cookie != null) {
            // 保存Cookie
            AccountUtil.updateCookie(cookie);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true：同步cookie成功；false：同步cookie失败
     */
    public static boolean syncCookie(Context context, String url, String cookie) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);
        String newCookie = cookieManager.getCookie(url);
        return !TextUtils.isEmpty(newCookie);
    }
}
