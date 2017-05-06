package cc.duduhuo.simpler.bean.weibo;

import android.content.Context;
import android.os.Bundle;

import com.sina.weibo.sdk.utils.Utility;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/28 15:51
 * 版本：1.0
 * 描述：用户授权信息
 * 备注：
 * =======================================================
 */
public class AuthInfo extends com.sina.weibo.sdk.auth.AuthInfo {
    private String mAppKey = "";
    private String mRedirectUrl = "";
    private String mScope = "";
    private String mPackageName = "";
    private String mKeyHash = "";

    public AuthInfo(Context context, String appKey, String redirectUrl, String scope, String packageName) {
        super(context, appKey, redirectUrl, scope);
        this.mAppKey = appKey;
        this.mRedirectUrl = redirectUrl;
        this.mScope = scope;
        this.mPackageName = packageName;
        this.mKeyHash = Utility.getSign(context, this.mPackageName);
    }

    @Override
    public String getAppKey() {
        return this.mAppKey;
    }

    @Override
    public String getRedirectUrl() {
        return this.mRedirectUrl;
    }

    @Override
    public String getScope() {
        return this.mScope;
    }

    @Override
    public String getPackageName() {
        return this.mPackageName;
    }

    @Override
    public String getKeyHash() {
        return this.mKeyHash;
    }

    @Override
    public Bundle getAuthBundle() {
        Bundle mBundle = new Bundle();
        mBundle.putString("appKey", this.mAppKey);
        mBundle.putString("redirectUri", this.mRedirectUrl);
        mBundle.putString("scope", this.mScope);
        mBundle.putString("packagename", this.mPackageName);
        mBundle.putString("key_hash", this.mKeyHash);
        return mBundle;
    }

    public static com.sina.weibo.sdk.auth.AuthInfo parseBundleData(Context context, Bundle data) {
        String appKey = data.getString("appKey");
        String redirectUrl = data.getString("redirectUri");
        String scope = data.getString("scope");
        return new com.sina.weibo.sdk.auth.AuthInfo(context, appKey, redirectUrl, scope);
    }
}
