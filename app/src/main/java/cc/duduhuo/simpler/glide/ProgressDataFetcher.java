package cc.duduhuo.simpler.glide;

import android.os.Handler;
import android.os.Message;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @see com.bumptech.glide.load.data.HttpUrlFetcher
 * @see <a href="https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java">OkHttp sample</a>
 */
public class ProgressDataFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "ProgressDataFetcher";
    private final String url;
    private final Handler handler;
    private Call progressCall;
    private InputStream stream;
    private volatile boolean isCancelled;
    private volatile boolean isUrl = false;

    public ProgressDataFetcher(String url, Handler handler) {
        this.url = url;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            isUrl = true;
        }
        this.handler = handler;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        if (isUrl) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            final ProgressListener glideProgressListener = new ProgressListener() {

                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
//                Log.e(TAG, bytesRead + "," + contentLength + done);
                    if (handler != null) {
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.arg1 = (int) bytesRead;
                        message.arg2 = (int) contentLength;
                        handler.sendMessage(message);
                    }
                }
            };
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {

                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new ProgressResponseBody(originalResponse.body(), glideProgressListener))
                                    .build();
                        }
                    })
                    .build();
            try {
                progressCall = client.newCall(request);
                Response response = progressCall.execute();
                if (isCancelled) {
                    return null;
                }
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                stream = response.body().byteStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stream;
        } else {
            return new FileInputStream(url);
        }
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                // Ignore
            }
        }
        if (progressCall != null) {
            progressCall.cancel();
        }
    }

    @Override
    public String getId() {
        return url;
    }

    @Override
    public void cancel() {
        // TODO: we should consider disconnecting the url connection here, but we can't do so directly because cancel is
        // often called on the main thread.
        isCancelled = true;
    }
}
