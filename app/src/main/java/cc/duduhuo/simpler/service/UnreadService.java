package cc.duduhuo.simpler.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sina.weibo.sdk.openapi.models.RemindCount;

import java.util.Calendar;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.bean.Settings;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.listener.impl.RemindOp;
import cc.duduhuo.simpler.service.notifier.UnreadCountNotifier;
import cc.duduhuo.simpler.util.SettingsUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 14:02
 * 版本：1.0
 * 描述：未读消息服务
 * 备注：
 * =======================================================
 */
public class UnreadService extends Service implements RemindOp.OnRemindOpResultListener {
    public static final String ACTION_GET = "cc.duduhuo.simpler.ACTION_GET";
    public static final String ACTION_UPDATE = "cc.duduhuo.simpler.ACTION_UPDATE";

    public UnreadCountNotifier mNotifier;
    private RemindOp mRemindOp;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mNotifier == null) {
            mNotifier = new UnreadCountNotifier(this);
        }

        String action = intent != null ? intent.getAction() : "";

        if (ACTION_GET.equals(action)) {
            resetTheTime();
            if (mRemindOp == null) {
                mRemindOp = new RemindOp(this);
                mRemindOp.setOnRemindOpResultListener(this);
            }
            mRemindOp.onUnreadCount(Long.parseLong(BaseConfig.sUid));
        } else if (ACTION_UPDATE.equals(action)) {
            resetTheTime();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService() {
        if (!BaseSettings.sSettings.messageNotification && !BaseSettings.sSettings.privateLetterNotification) {
            // 关闭了消息通知和私信通知
            BaseSettings.sNotifyEnable = false;
            return;
        }
        BaseSettings.sNotifyEnable = true;

        Intent intent = new Intent(App.getInstance(), UnreadService.class);
        intent.setAction(ACTION_GET);
        App.getInstance().startService(intent);
    }

    public static void stopService() {
        clearAlarm();
        Intent intent = new Intent(App.getInstance(), UnreadService.class);
        App.getInstance().stopService(intent);
        BaseSettings.sNotifyEnable = false;
    }

    public static void updateAlarm() {
        Intent intent = new Intent(App.getInstance(), UnreadService.class);
        intent.setAction(ACTION_UPDATE);
        App.getInstance().startService(intent);
    }

    private static PendingIntent getOperation() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(ACTION_GET);
        PendingIntent sender = PendingIntent.getService(App.getInstance().getBaseContext(), 1000,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return sender;
    }

    private static void clearAlarm() {
        AlarmManager am = (AlarmManager) App.getInstance().getSystemService(ALARM_SERVICE);
        am.cancel(getOperation());
    }

    private void resetTheTime() {
        SettingsUtil.readSettings(BaseConfig.sUid);
        Settings settings = BaseSettings.sSettings;
        if (settings != null) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            // 指定时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, BaseSettings.sSettings.notifyInterval);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getOperation());
        }
    }

    @Override
    public void onUnreadCountSuccess(RemindCount remindCount) {
        // 通知消息
        mNotifier.notifyUnreadCount(remindCount);
    }

    @Override
    public void onUnreadCountFailure(String msg) {
        AppToast.showToast(msg);
    }

    @Override
    public void onSetCountSuccess() {
        //  no op
    }

    @Override
    public void onSetCountFailure(String msg) {
            //  no op
    }
}
