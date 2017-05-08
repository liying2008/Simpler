package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.bean.weibo.AuthInfo;
import cc.duduhuo.simpler.util.AccessTokenKeeper;
import cc.duduhuo.simpler.config.SinaConsts;

public class WBLoginActivity extends BaseActivity {
    private AuthInfo mAuthInfo;

    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    @BindView(R.id.tvMsg)
    TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_login);
        ButterKnife.bind(this);

        if (BaseConfig.sAddAccountMode) {
            mTvMsg.setText(R.string.add_another_account);
        } else {
            mTvMsg.setText(R.string.has_not_logined);
        }
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        // 高级授权
        mAuthInfo = new AuthInfo(this, SinaConsts.WEICO_APP_KEY, SinaConsts.WEICO_REDIRECT_URL,
                SinaConsts.SCOPE, SinaConsts.WEICO_PACKAGE_NAME);
        // 普通授权
//        mAuthInfo = new AuthInfo(this, SinaConsts.APP_KEY, SinaConsts.REDIRECT_URL,
//                SinaConsts.SCOPE, App.getInstance().getPackageName());
        mSsoHandler = new SsoHandler(this, mAuthInfo);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, WBLoginActivity.class);
        return intent;
    }

    @OnClick(R.id.tvLogin)
    public void login() {
        // SSO 授权, 仅Web
        // 注意：使用高级授权模式，如果设备上已经安装了微博官方客户端，则无法调用mSsoHandler.authorize()方法完成授权
        // 只好使用mSsoHandler.authorizeWeb()方法
        mSsoHandler.authorizeWeb(new AuthListener());
    }

    @OnClick(R.id.tvAccount)
    void switchAccount() {
        startActivity(SwitchAccountActivity.newIntent(this));
    }

    @OnClick(R.id.tvBack)
    public void back() {
        this.finish();
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String phoneNum = mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
//                Log.d("Auth", sAccessToken.toString());
                // 保存 Token
                AccessTokenKeeper.writeAccessToken(mAccessToken);

                AppToast.showToast(R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT);
                BaseConfig.sTokenExpired = false;
                if (BaseConfig.sAddAccountMode) {
                    // 添加帐号模式，结束之前所有Activity
                    App.getInstance().finishAllActivities();
                }
                startActivity(MainActivity.newIntent(WBLoginActivity.this));
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                AppToast.showToast(message, Toast.LENGTH_LONG);
            }
            finish();
        }

        @Override
        public void onCancel() {
            AppToast.showToast(R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            AppToast.showToast("Auth exception : " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }
}
