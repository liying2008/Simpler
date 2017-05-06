package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.config.Constants;

public class WebViewActivity extends BaseActivity {
    /** Intent传值的键（URL） */
    private static final String INTENT_URL = "url";
    @BindView(R.id.tvWebTitle)
    TextView mTvWebTitle;

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.pb)
    ProgressBar mPb;

    @BindView(R.id.btnOverflow)
    Button mBtnOverflow;

    /** 当前页面URL */
    private String mCurUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        mBtnOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebViewOpWindow(v);
            }
        });

        initWebView();
        Intent intent = getIntent();
        mCurUrl = intent.getStringExtra(INTENT_URL);
        mWebView.loadUrl(mCurUrl);   // 加载网页
    }

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(INTENT_URL, url);
        return intent;
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        // 设置支持 Javascript
        webSettings.setJavaScriptEnabled(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        // 设置UA
        webSettings.setUserAgentString(Constants.MOBILE_USER_AGENT);
        // 在当前WebView打开新链接
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mCurUrl = url;
                view.loadUrl(url);
                return true;
            }

            /**
             * 网页开始加载
             * @param view
             * @param url
             * @param favicon
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mPb.setVisibility(View.VISIBLE);
                mTvWebTitle.setText(R.string.web_view_loading);
            }

            /**
             * 网页加载完毕
             * @param view
             * @param url
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mPb.setVisibility(View.GONE);
            }

            /**
             * 网页加载错误
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

        /*
         * 处理网页中的一些对话框信息（提示对话框，带选择的对话框，带输入的对话
         * 框）
         */
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                // 使用自定义的AlertDialog显示信息
                final AlertDialog dialog = App.getAlertDialogBuilder(WebViewActivity.this).create();
                View rootView = LayoutInflater.from(WebViewActivity.this).inflate(R.layout.dialog_js_alert, null);
                TextView tvMsg = ButterKnife.findById(rootView, R.id.tvMsg);
                TextView tvClose = ButterKnife.findById(rootView, R.id.tvClose);
                tvMsg.setText(message);
                dialog.setView(rootView);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                tvClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        result.confirm();
                    }
                });
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                // 使用自定义的AlertDialog显示信息
                final AlertDialog dialog = App.getAlertDialogBuilder(WebViewActivity.this).create();
                View rootView = LayoutInflater.from(WebViewActivity.this).inflate(R.layout.dialog_alert, null);
                TextView tvTitle = ButterKnife.findById(rootView, R.id.tvTitle);
                TextView tvMsg = ButterKnife.findById(rootView, R.id.tvMsg);
                TextView tv1 = ButterKnife.findById(rootView, R.id.tv1);
                TextView tv2 = ButterKnife.findById(rootView, R.id.tv2);
                tvTitle.setText("来自网页的消息");
                tvMsg.setText(message);
                tv1.setText("取消");
                tv2.setText("确定");
                dialog.setView(rootView);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        result.cancel();
                    }
                });
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        result.confirm();
                    }
                });
                return true;
            }

            /**
             * 获取网页标题
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTvWebTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mPb.setProgress(newProgress);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 打开系统浏览器进行下载任务
                CommonUtils.openBrowser(WebViewActivity.this, url);
            }
        });
    }

    /**
     * 显示PopupWindow：WebView操作
     *
     * @param view
     */
    private void showWebViewOpWindow(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.window_webview_op, null);
        // 设置Item的点击事件
        LinearLayout llRefresh = (LinearLayout) contentView.findViewById(R.id.llRefresh);
        LinearLayout llCopyUrl = (LinearLayout) contentView.findViewById(R.id.llCopyUrl);
        LinearLayout llOpen = (LinearLayout) contentView.findViewById(R.id.llOpen);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        OpClickListener listener = new OpClickListener(popupWindow);
        llRefresh.setOnClickListener(listener);
        llCopyUrl.setOnClickListener(listener);
        llOpen.setOnClickListener(listener);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_transparent_bg));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);
    }

    private class OpClickListener implements View.OnClickListener {
        PopupWindow mPopupWindow;

        public OpClickListener(PopupWindow popupWindow) {
            this.mPopupWindow = popupWindow;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llRefresh:
                    mWebView.reload();
                    break;
                case R.id.llCopyUrl:
                    CommonUtils.copyText(WebViewActivity.this, mCurUrl);
                    AppToast.showToast(R.string.copied);
                    break;
                case R.id.llOpen:
                    CommonUtils.openBrowser(WebViewActivity.this, mCurUrl);
                    break;
                default:
                    break;
            }
            mPopupWindow.dismiss();
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

    @Override
    protected void onDestroy() {
        // 解决android webview ZoomButtonsController 导致android.view.WindowLeaked
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.onDestroy();
    }
}
