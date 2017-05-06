package cc.duduhuo.simpler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/29 15:26
 * 版本：1.0
 * 描述：格式化日期时间/粉丝数/微博数/关注数等数据
 * 备注：
 * =======================================================
 */
public class NumberFormatter {
    /**
     * 格式化微博粉丝数/关注数/微博数等
     *
     * @param count 微博粉丝数/关注数/微博数等
     * @param base  格式化的基数 <br />
     *              10000：大于10000的以万为单位格式化<br />
     *              60000：大于60000的以万为单位格式化<br />
     *              100000：大于100000的以万为单位格式化<br />
     * @return
     */
    public static String formatWBCount(int count, int base) {
        switch (base) {
            case 10000:
                return formatCount(count, base);
            case 60000:
                return formatCount(count, base);
            case 100000:
                return formatCount(count, base);
            default:
                return String.valueOf(count);
        }
    }

    private static String formatCount(int count, int base) {
        if (count < base) {
            return String.valueOf(count);
        } else if (count < 100000000) {
            return Math.round(count / 10000.0) + "万";
        } else {
            double d = count / 100000000.0;
            return String.format(Locale.getDefault(), "%.2f", d) + "亿";
        }
    }

    /**
     * 转换时间
     *
     * @param now  -1：直接格式化为yyyy-MM-dd HH:mm:ss <br />
     *             当前毫秒数：格式化为微博格式 <br />
     * @param time
     * @return
     */
    public static String dateTransfer(long now, String time) {
        long createAtTime = Date.parse(time);
        Date createAt = new Date(createAtTime);
        if (now != -1) {
            long second = (now - createAtTime) / 1000L;
            if (second < 0) {
                second = 0;
            }
            if (second == 0) {
                return "刚刚";
            } else if (second < 30) {
                return second + "秒以前";
            } else if (second < 60) {
                return "半分钟前";
            } else if (second < 60 * 60) {
                return (second / 60) + "分钟前";
            } else if (second <= 60 * 60 * 3) {
                return (second / 60 / 60) + "小时前";
            } else if (getFormatTime(createAt, "d").equals(Calendar.getInstance().get(Calendar.DATE) + "")) {
                return "今天 " + getFormatTime(createAt, "HH:mm");
            } else if (getFormatTime(createAt, "d").equals((Calendar.getInstance().get(Calendar.DATE) - 1) + "")) {
                return "昨天 " + getFormatTime(createAt, "HH:mm");
            } else if (second < 60 * 60 * 24 * 7) {
                return (((second / 60) / 60) / 24) + "天前";
            } else if (getFormatTime(createAt, "yyyy").equals(Calendar.getInstance().get(Calendar.YEAR) + "")) {
                // 同一年
                return getFormatTime(createAt, "MM-dd HH:mm");
            }
            return getFormatTime(createAt, "yyyy-MM-dd HH:mm");
        } else {
            return getFormatTime(createAt, "yyyy-MM-dd HH:mm:ss");
        }
    }

    private static String getFormatTime(Date date, String sdf) {
        return (new SimpleDateFormat(sdf, Locale.US)).format(date);
    }

    /**
     * 格式化消息数目标识
     * @param count 消息数目
     * @return
     */
    public static String formatUnreadCount(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else {
            return "999+";
        }
    }
}
