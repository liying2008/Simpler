package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.listener.impl.RemindOp;
import cc.duduhuo.simpler.util.CookieKeeper;

public class WebWBActivity extends BaseActivity {
    /** Intent传值的键（获取Cookie后自动关闭） */
    private static final String INTENT_AUTO_CLOSE = "auto_close";
    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.pb)
    ProgressBar mPb;

    public static final String BASE_URL = "http://m.weibo.cn";
    public static final String LOGIN_URL = "https://passport.weibo.cn/signin/login";
    /** 是否自动关闭 */
    private boolean mAutoClose;
    /** 是否已经记录Cookie */
    private boolean mHasRecord = false;
    private boolean mIsResultOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_wb);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mAutoClose = intent.getBooleanExtra(INTENT_AUTO_CLOSE, false);
        initWebView();
        if (mAutoClose) {
            mWebView.loadUrl(LOGIN_URL);
        } else {
            mWebView.loadUrl(BASE_URL);
        }
    }

    public static Intent newIntent(Context context, boolean autoClose) {
        Intent intent = new Intent(context, WebWBActivity.class);
        intent.putExtra(INTENT_AUTO_CLOSE, autoClose);
        return intent;
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        // 设置支持 Javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置UA
        webSettings.setUserAgentString(Constants.MOBILE_USER_AGENT);
        // 在当前WebView打开新链接
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            /**
             * 网页开始加载
             *
             * @param view
             * @param url
             * @param favicon
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mPb.setVisibility(View.VISIBLE);
            }

            /**
             * 网页加载完毕
             *
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mPb.setVisibility(View.GONE);
                if (!mHasRecord) {
                    if (url.startsWith("http://m.weibo.cn/")) {
                        saveCookie();
                        if (mAutoClose) {
                            setResult(RESULT_OK);
                            finish();
                        }
                        if (!mIsResultOk) {
                            mIsResultOk = true;
                            setResult(RESULT_OK);
                            RemindOp remindOp = new RemindOp(WebWBActivity.this);
                            remindOp.onSetCount("dm");
                            remindOp.onSetCount("msgbox");
                        }
                    }
                }
            }

            /**
             * 网页加载错误
             *
             * @param view
             * @param request
             * @param error
             */
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mPb.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 保存登录Cookie
     */
    private void saveCookie() {
        mHasRecord = true;
        String cookie = CookieManager.getInstance().getCookie(BASE_URL);
        CookieKeeper.saveCookie(cookie);    // 保存Cookie
        if (mAutoClose) {
            AppToast.showToast(R.string.cookie_saved);
        }
    }

    /**
     * 点击Back键，如果WebView可以返回上一页，则返回上一页，否则关闭Activity
     */
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
