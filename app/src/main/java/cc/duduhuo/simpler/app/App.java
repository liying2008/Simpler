package cc.duduhuo.simpler.app;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloader;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.LinkedList;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.db.SettingsServices;
import cc.duduhuo.simpler.db.UserServices;
import cc.duduhuo.simpler.listener.OnSettingsChangeListener;
import cc.duduhuo.simpler.util.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 10:01
 * 版本：1.0
 * 描述：Application
 * 备注：
 * =======================================================
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {
    /** 当前Application实例 */
    private static App mInstance;
    /** 全局prefs */
    public SharedPreferences prefs;
    /** 全局editor */
    public SharedPreferences.Editor editor;
    /** 用户帐户业务类对象 */
    public static UserServices userServices;
    /** 用户设置业务类对象 */
    public static SettingsServices settingsServices;
    /** Activity链表 */
    private LinkedList<Activity> mActivityList = new LinkedList<>();
    /** 设置项改变监听器 */
    public OnSettingsChangeListener mSettingsChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();
        // 所有Activity生命周期回调
        registerActivityLifecycleCallbacks(this);
        mInstance = this;
        // 实例化SharedPreferences
        prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        editor = prefs.edit();
        userServices = new UserServices(this);
        settingsServices = new SettingsServices(this);

        // 检查工作目录
        checkWorkDir();
        // 检查存储卡
        checkSdCard();
        // 友盟场景类型设置接口
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        // 初始化ApplicationToast
        AppToast.init(this);
        // FileDownloader全局初始化
        FileDownloader.init(getApplicationContext());
    }

    /**
     * 检查用户的设备是否有存储卡
     */
    private void checkSdCard() {
        BaseConfig.sSDCardExist = FileUtils.hasStorage();
    }

    /**
     * 得到本Application实例
     */
    public static App getInstance() {
        return mInstance;
    }

    /**
     * 检查工作目录和下载目录是否存在，不存在则创建
     */
    private void checkWorkDir() {
        File avatarDir = new File(Constants.Dir.AVATAR_DIR);
        File cacheDir = new File(Constants.Dir.CACHE_DIR);
        File picDir = new File(Constants.Dir.PIC_DIR);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (!picDir.exists()) {
            picDir.mkdirs();
        }
    }

    /**
     * 得到对话框构建器实例
     *
     * @param context You need to use your Activity as the Context for the Dialog not the Application.
     * @return
     */
    public static AlertDialog.Builder getAlertDialogBuilder(Activity context) {
        return new AlertDialog.Builder(context);
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        int versionCode = 0;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return 版本名称
     */
    public String getVersionName() {
        String packageName = getPackageName();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取在AndroidManifest.xml中声明的更新日期
     *
     * @return 更新日期
     */
    public String getUpdateDate() {
        String packageName = getPackageName();
        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("cc.duduhuo.simpler.update_date");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取ApplicationId
     *
     * @return ApplicationId
     */
    public String getApplicationId() {
        String packageName = getPackageName();
        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Log.d("Image", packageName + " " + appInfo.metaData.getString("APP_ID"));
            return appInfo.metaData.getString("APP_ID");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 结束所有没有Destroy的Activity
     */
    public void finishAllActivities() {
        if (null != mActivityList) {
            // 先暂停监听（省得同时在2个地方操作列表）
            unregisterActivityLifecycleCallbacks(this);
            // 弹出的时候从头开始弹，和系统的 activity 堆栈保持一致
            for (Activity activity : mActivityList) {
                if (null == activity) {
                    continue;
                }
                try {
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mActivityList.clear();
            // 退出完之后再添加监听
            registerActivityLifecycleCallbacks(this);
        }
    }

    public void setOnSettingsChangeListener(OnSettingsChangeListener settingsChangeListener) {
        this.mSettingsChangeListener = settingsChangeListener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (null != mActivityList && null != activity) {
            // 把新的 activity 添加到最前面，和系统的 activity 堆栈保持一致
            mActivityList.offerFirst(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (null != mActivityList && null != activity) {
            Activity aty = findActivity(activity);
            if (null != aty) {
                mActivityList.remove(aty);
            }
        }
    }

    private Activity findActivity(Activity activity) {
        if (null == activity || null == mActivityList) {
            return null;
        }
        for (Activity aty : mActivityList) {
            if (null == aty) {
                continue;
            }
            if (activity.equals(aty)) {
                return aty;
            }
        }
        return null;
    }
}
