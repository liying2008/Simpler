package cc.duduhuo.simpler.net;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 21:03
 * 版本：1.0
 * 描述：检测视频链接是否有重定向
 * 备注：
 * =======================================================
 */
public class MovedTempTester extends AsyncTask<String, Void, String> {
    private static OkHttpClient client;
    private MovedTempListener mListener;

    public MovedTempTester(MovedTempListener listener) {
        this.mListener = listener;
        if (client == null) {
            client = new OkHttpClient().newBuilder()
                    .followRedirects(false)  //禁制OkHttp的重定向操作
                    .followSslRedirects(false).build();
        }
    }

    /**
     * @param params @param params * params[0]：url <br />
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        try {
            String location = Http.getLocation(client, url);
            if (!TextUtils.isEmpty(location)) {
                return location;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            if (mListener != null) {
                mListener.onRedirect(s);
            }
        } else {
            if (mListener != null) {
                mListener.onNo();
            }
        }
    }
}
