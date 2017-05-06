package cc.duduhuo.simpler.service.notifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import cc.duduhuo.simpler.app.App;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 14:35
 * 版本：1.0
 * 描述：消息通知
 * 备注：
 * =======================================================
 */
public abstract class Notifier {

    protected final Context mContext;
    private final NotificationManager mNotificationManager;

    public static final int PUBLISH_STATUS_NOTIFICATION_REQUEST = 1;// 新微博
    public static final int REMIND_UNREAD_COMMENTS = 2;// 新评论
    public static final int REMIND_UNREAD_MENTION_COMMENTS = 3;// 新提及评论
    public static final int REMIND_UNREAD_MENTION_STATUS = 4;// 新提及微博
    public static final int REMIND_UNREAD_DM = 5;// 新私信
    public static final int REMIND_UNREAD_MSG_BOX = 6;// 新未关注人私信
    public static final int REMIND_UNREAD_FOLLOWERS = 7;// 新粉丝

    public Notifier(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected final void notify(int id, Notification notification) {
        mNotificationManager.notify(id, notification);
    }

    public final void cancelNotification(int request) {
        mNotificationManager.cancel(request);
    }

    public static final void cancelAll() {
        NotificationManager notificationManager = (NotificationManager) App.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(PUBLISH_STATUS_NOTIFICATION_REQUEST);
        notificationManager.cancel(REMIND_UNREAD_COMMENTS);
        notificationManager.cancel(REMIND_UNREAD_MENTION_COMMENTS);
        notificationManager.cancel(REMIND_UNREAD_MENTION_STATUS);
        notificationManager.cancel(REMIND_UNREAD_FOLLOWERS);
        notificationManager.cancel(REMIND_UNREAD_DM);
    }

}
