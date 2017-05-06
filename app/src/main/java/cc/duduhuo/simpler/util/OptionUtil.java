package cc.duduhuo.simpler.util;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Browser;
import cc.duduhuo.simpler.config.PicQuality;
import cc.duduhuo.simpler.bean.Option;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 13:11
 * 版本：1.0
 * 描述：设置界面选项工具类（主要用于获取选项数据）
 * 备注：
 * =======================================================
 */
public class OptionUtil {
    /**
     * 浏览器选项
     *
     * @return
     */
    public static List<Option> getBrowserOptions() {
        List<Option> options = new ArrayList<>(2);
        options.add(new Option("内置浏览器", Browser.BUILT_IN, false));
        options.add(new Option("系统浏览器", Browser.SYSTEM, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.browser) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 每次刷新微博数
     *
     * @return
     */
    public static List<Option> getRefreshCountOptions() {
        List<Option> options = new ArrayList<>(5);
        options.add(new Option("10", 10, false));
        options.add(new Option("20", 20, false));
        options.add(new Option("30", 30, false));
        options.add(new Option("50", 50, false));
        options.add(new Option("100", 100, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.refreshCount) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 每次打开自动刷新微博
     *
     * @return
     */
    public static List<Option> getAutoRefreshOptions() {
        List<Option> options = new ArrayList<>(2);
        options.add(new Option("是", true, false));
        options.add(new Option("否", false, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((boolean) options.get(i).value == BaseSettings.sSettings.autoRefresh) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 微博图片显示质量
     *
     * @return
     */
    public static List<Option> getPicQualityOptions() {
        List<Option> options = new ArrayList<>(5);
        options.add(new Option("无图", PicQuality.NO_PIC, false));
        options.add(new Option("智能无图", PicQuality.INTELLIGENT, false));
        options.add(new Option("小图", PicQuality.THUMBNAIL, false));
        options.add(new Option("中图", PicQuality.MIDDLE, false));
        options.add(new Option("原始大图", PicQuality.ORIGINAL, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.picQuality) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 图片上传质量
     *
     * @return
     */
    public static List<Option> getUploadQualityOptions() {
        List<Option> options = new ArrayList<>(3);
        options.add(new Option("小图", PicQuality.THUMBNAIL, false));
        options.add(new Option("中图", PicQuality.MIDDLE, false));
        options.add(new Option("原始大图", PicQuality.ORIGINAL, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.uploadQuality) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 微博字体大小
     *
     * @return
     */
    public static List<Option> getFontSizeOptions() {
        List<Option> options = new ArrayList<>(6);
        options.add(new Option("13pt", 13, false));
        options.add(new Option("14pt", 14, false));
        options.add(new Option("15pt", 15, false));
        options.add(new Option("16pt", 16, false));
        options.add(new Option("17pt", 17, false));
        options.add(new Option("18pt", 18, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.fontSize) {
                options.get(i).selected = true;
            }
        }
        return options;
    }

    /**
     * 通知时间间隔
     *
     * @return
     */
    public static List<Option> getNotifyIntervalOptions() {
        List<Option> options = new ArrayList<>(7);
        options.add(new Option("半分钟", 30, false));
        options.add(new Option("1分钟", 60, false));
        options.add(new Option("5分钟", 5 * 60, false));
        options.add(new Option("10分钟", 10 * 60, false));
        options.add(new Option("30分钟", 30 * 60, false));
        options.add(new Option("60分钟", 60 * 60, false));
        options.add(new Option("120分钟", 120 * 60, false));
        int size = options.size();
        for (int i = 0; i < size; i++) {
            if ((int) options.get(i).value == BaseSettings.sSettings.notifyInterval) {
                options.get(i).selected = true;
            }
        }
        return options;
    }
}
