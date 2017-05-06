package cc.duduhuo.imageselector.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 19:52
 * 版本：1.0
 * 描述：文件相关工具类
 * 备注：
 * =======================================================
 */
public class FileUtils {
    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String getRootPath(Context context) {
        String rootPath = "";
        if (isSdCardAvailable()) {
            // 图片保存到相册
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera";
            File file = new File(rootPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            // /data/data/<application package>/cache
            rootPath = context.getCacheDir().getPath();
        }
        return rootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 创建文件
     *
     * @param file
     * @return 创建失败返回""
     */
    public static String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {
                Log.d("Image", "----- 创建文件" + file.getAbsolutePath());
                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
                Log.d("Image", "----- 创建文件" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getApplicationId(Context appContext) throws IllegalArgumentException {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo == null) {
                throw new IllegalArgumentException(" get application info = null, has no meta data! ");
            }
            Log.d("Image", appContext.getPackageName() + " " + applicationInfo.metaData.getString("APP_ID"));
            return applicationInfo.metaData.getString("APP_ID");
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(" get application info error! ", e);
        }
    }
}
