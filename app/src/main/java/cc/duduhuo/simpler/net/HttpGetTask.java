package cc.duduhuo.simpler.net;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/4 19:32
 * 版本：1.0
 * 描述：网络任务类
 * 备注：
 * =======================================================
 */
public class HttpGetTask extends AsyncTask<String, Void, String> {
    private boolean mIsMobile;  // 是否模拟手机端请求
    private OkHttpClient mClient;
    private HttpListener mListener;

    public HttpGetTask(boolean isMobile, HttpListener listener) {
        this.mIsMobile = isMobile;
        this.mListener = listener;
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
    }


    /**
     * @param params * params[0]：url <br />
     *               params[1]：cookie <br />
     * @return
     */
    @Override
    protected String doInBackground(String... params) {
        int length = params.length;
        String url = params[0];
        String cookie = null;
        if (length >= 2) {
            cookie = params[1];
        }
        try {
            String result = Http.doGet(mClient, url, cookie, mIsMobile);
//            Log.d("Cookie4", result);
            return result;
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
                mListener.onResponse(s);
            }
        } else {
            if (mListener != null) {
                mListener.onFailure();
            }
        }
    }
}
