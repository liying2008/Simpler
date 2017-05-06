package cc.duduhuo.simpler.config;

import android.os.Environment;

import java.io.File;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 10:03
 * 版本：1.0
 * 描述：常量信息
 * 备注：
 * =======================================================
 */
public interface Constants {
    /** SharedPreferences的名字 */
    String PREFS_NAME = "simpler";
    /** SharedPreferences储存的版本号 */
    String PREFS_VERSION = "VERSION_CODE";
    /** SharedPreferences储存的当前用户ID */
    String PREFS_CUR_UID = "current_uid";

    /**
     * 文件/目录常量
     */
    interface Dir {
        String IMAGE_CACHE_DIR = "images";
        String OUTER_DIR = "duduhuo";
        String SIMPLER_DIR = "simpler";
        /** 工作目录 */
        String WORK_DIR = Environment.getExternalStorageDirectory()
                + File.separator + OUTER_DIR + File.separator + SIMPLER_DIR;
        /** 头像存储目录 */
        String AVATAR_DIR = WORK_DIR + File.separator + "avatar";
        /** 微博缓存目录 */
        String CACHE_DIR = WORK_DIR + File.separator + "cache";
        /** 微博图片和视频下载文件夹 */
        String PIC_DIR = WORK_DIR + File.separator + "simpler";
    }

    /** 手机浏览器UA */
    String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.23 Mobile Safari/537.36";
    /** PC浏览器UA */
    String PC_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    /** 百度地图开放平台的AK */
    String BAIDU_LBS_AK = "D0jqqMaAlpsU2EguH4KYGMrBAu1o6VRj";
    /** 检查更新URL */
    String UPDATE_PATH = "http://duduhuo.cc/simpler/update/update.json";
    /** 检查更新测试URL */
    String UPDATE_PATH_TEST = "http://duduhuo.cc/simpler/update/update_test.json";
}
