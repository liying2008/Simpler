package cc.duduhuo.simpler.net;

import android.text.TextUtils;

import java.io.IOException;

import cc.duduhuo.simpler.config.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 19:26
 * 版本：1.0
 * 描述：Http工具类
 * 备注：
 * =======================================================
 */
public class Http {
    /**
     * 带Cookie的Get方法
     *
     * @param client
     * @param url    请求参数直接拼接在url后面
     * @param cookie 请求时向服务器发送的cookie
     * @return
     * @throws IOException
     */
    public static String doGet(OkHttpClient client, String url, String cookie, boolean mobile) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder = builder.url(url);
        if (mobile) {
            builder = builder.header("User-Agent", Constants.MOBILE_USER_AGENT);
        } else {
            builder = builder.header("User-Agent", Constants.PC_USER_AGENT);
        }
        if (!TextUtils.isEmpty(cookie)) {
            builder = builder.header("Cookie", cookie);
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    /**
     * 获取重定向地址
     *
     * @param client
     * @param url
     * @return
     * @throws IOException
     */
    public static String getLocation(OkHttpClient client, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", Constants.MOBILE_USER_AGENT)
                .build();
        Response response = client.newCall(request).execute();
        return response.header("Location");
    }
}
