package cc.duduhuo.simpler.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.AccessTokenKeeper;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.util.PrefsUtils;
import cc.duduhuo.simpler.util.SettingsUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 10:11
 * 版本：1.0
 * 描述：Splash界面加载应用数据任务类
 * 备注：
 * =======================================================
 */
public class SplashLoadDataTask extends AsyncTask<Void, Void, Void> {
    private LoadDataCallback callback;

    public SplashLoadDataTask(LoadDataCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        loadSettings();
        loadData();
        return null;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        String uid = PrefsUtils.getString(Constants.PREFS_CUR_UID, "");
        BaseConfig.sUid = uid;
        // 设置当前帐号
        BaseConfig.sAccount = AccountUtil.readAccount(uid);
        // 设置当前应用设置
        BaseSettings.sSettings = SettingsUtil.readSettings(uid);
        if (!TextUtils.isEmpty(uid)) {
            BaseConfig.sAccessToken = AccessTokenKeeper.readAccessToken(uid);
        }
    }

    /**
     * 加载设置
     */
    private void loadSettings() {
        BaseConfig.sAddAccountMode = false;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null) {
            // 数据加载完毕
            callback.loaded();
        }
    }

    /**
     * 加载数据回调
     */
    public interface LoadDataCallback {
        /** 数据加载完毕 */
        void loaded();
    }

}
