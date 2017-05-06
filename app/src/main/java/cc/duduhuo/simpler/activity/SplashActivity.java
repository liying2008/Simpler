package cc.duduhuo.simpler.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.umeng.analytics.MobclickAgent;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.task.SplashLoadDataTask;
import cc.duduhuo.simpler.util.PrefsUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 9:33
 * 版本：1.0
 * 描述：启动界面
 * 备注：
 * =======================================================
 */
public class SplashActivity extends BaseActivity implements SplashLoadDataTask.LoadDataCallback{
    private static PackageInfo packageInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        MobclickAgent.enableEncrypt(true);
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 启动加载应用数据任务类
        startTask();
    }

    /**
     * 启动加载应用数据任务类
     */
    private void startTask() {
        SplashLoadDataTask task = new SplashLoadDataTask(this);
        task.execute();
    }

    /**
     * 跳转界面
     */
    private void jump() {
        int currentVersion = packageInfo.versionCode;
        int version = PrefsUtils.getInt(Constants.PREFS_VERSION, 0);
        if (currentVersion > version) {
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            PrefsUtils.putInt(Constants.PREFS_VERSION, currentVersion);
        }
        if (!BaseConfig.sUid.equals("")) {
            // 已经登录，跳转到MainActivity
            startActivity(MainActivity.newIntent(this));
        } else {
            // 未登录，则跳转到授权登录界面
            startActivity(WBLoginActivity.newIntent(this));
        }
        finish();
    }

    @Override
    public void loaded() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jump();// 界面跳转
            }
        }, 500);   // 停留时间500ms
    }

    @Override
    public void onBackPressed() {
        // Splash界面不允许使用back键
    }

}
