package cc.duduhuo.simpler.service.notifier;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sina.weibo.sdk.openapi.models.RemindCount;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.CmtMentionActivity;
import cc.duduhuo.simpler.activity.StatusMentionActivity;
import cc.duduhuo.simpler.activity.WBFollowersActivity;
import cc.duduhuo.simpler.activity.WebWBActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 14:32
 * 版本：1.0
 * 描述：未读消息数通知
 * 备注：
 * =======================================================
 */
public class UnreadCountNotifier extends Notifier {
    public UnreadCountNotifier(Context context) {
        super(context);
    }

    public void notifyUnreadCount(RemindCount count) {
        String fromSimpler = mContext.getString(R.string.notify_from_simpler);

        // 消息通知
        if (BaseSettings.sSettings.messageNotification) {
            // 新粉丝
            if (count.follower > 0) {
                String contentTitle = mContext.getString(R.string.notify_new_followers, count.follower);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle).setContentText(fromSimpler);

                Intent intent = WBFollowersActivity.newIntent(mContext, Long.parseLong(BaseConfig.sUid), null);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_FOLLOWERS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);
                builder.setTicker(contentTitle);
                notify(REMIND_UNREAD_FOLLOWERS, builder.build());
            }
            if (count.follower == 0) {
                cancelNotification(REMIND_UNREAD_FOLLOWERS);
            }

            // 新评论
            if (count.cmt > 0) {
                String contentTitle = mContext.getString(R.string.notify_new_cmts, count.cmt);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle).setContentText(fromSimpler);
                builder.setTicker(contentTitle);

                Intent intent = CmtMentionActivity.newIntent(mContext, CmtMentionActivity.TYPE_TO_ME);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_COMMENTS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);

                notify(REMIND_UNREAD_COMMENTS, builder.build());
            }
            if (count.cmt == 0) {
                cancelNotification(REMIND_UNREAD_COMMENTS);
            }

            // 新提及我的微博数
            if (count.mention_status > 0) {
                String contentTitle = mContext.getString(R.string.notify_new_mention_status, count.mention_status);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle)
                        .setContentText(fromSimpler);
                builder.setTicker(contentTitle);

                Intent intent = StatusMentionActivity.newIntent(mContext);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_MENTION_STATUS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);

                notify(REMIND_UNREAD_MENTION_STATUS, builder.build());
            }

            if (count.mention_status == 0) {
                cancelNotification(REMIND_UNREAD_MENTION_STATUS);
            }

            // 新提及我的评论数
            if (count.mention_cmt > 0) {
                String contentTitle = String.format(mContext.getString(R.string.notify_new_mention_cmt), count.mention_cmt);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle)
                        .setContentText(fromSimpler);
                builder.setTicker(contentTitle);

                Intent intent = CmtMentionActivity.newIntent(mContext, CmtMentionActivity.TYPE_MENTION);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_MENTION_COMMENTS, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);

                notify(REMIND_UNREAD_MENTION_COMMENTS, builder.build());
            }

            if (count.mention_cmt == 0) {
                cancelNotification(REMIND_UNREAD_MENTION_COMMENTS);
            }
        }

        // 私信通知
        if (BaseSettings.sSettings.privateLetterNotification) {
            // 新私信
            if (count.dm > 0) {
                String contentTitle = String.format(mContext.getString(R.string.notify_new_dm), count.dm);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle)
                        .setContentText(fromSimpler);
                builder.setTicker(contentTitle);

                Intent intent = WebWBActivity.newIntent(mContext, false);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_DM, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);

                notify(REMIND_UNREAD_DM, builder.build());
            }
            if (count.dm == 0) {
                cancelNotification(REMIND_UNREAD_DM);
            }

            // 新未关注人私信
            if (count.msgbox > 0) {
                String contentTitle = String.format(mContext.getString(R.string.notify_new_msg), count.msgbox);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(contentTitle)
                        .setContentText(fromSimpler);
                builder.setTicker(contentTitle);

                Intent intent = WebWBActivity.newIntent(mContext, false);
                PendingIntent contentIntent = PendingIntent.getActivity(mContext, REMIND_UNREAD_MSG_BOX, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent).setAutoCancel(true);

                notify(REMIND_UNREAD_MSG_BOX, builder.build());
            }
            if (count.msgbox == 0) {
                cancelNotification(REMIND_UNREAD_MSG_BOX);
            }
        }
    }

}
