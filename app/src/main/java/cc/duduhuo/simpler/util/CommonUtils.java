package cc.duduhuo.simpler.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 19:07
 * 版本：1.0
 * 描述：通用工具类
 * 备注：
 * =======================================================
 */
public class CommonUtils {
    /**
     * 拷贝文本
     *
     * @param context
     * @param text    拷贝的文本
     */
    public static void copyText(Context context, String text) {
        ClipboardManager cmbName = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipDataName = ClipData.newPlainText(null, text);
        cmbName.setPrimaryClip(clipDataName);
    }

    /**
     * 分享文本
     *
     * @param context
     * @param text    分享的文本
     */
    public static void shareText(Context context, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        ComponentName componentName = shareIntent.resolveActivity(context.getPackageManager());
        if (componentName != null) {
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_to)));
        } else {
            AppToast.showToast("无法分享。");
        }
    }

    /**
     * 打开浏览器
     *
     * @param context
     * @param url     浏览器加载的网址
     */
    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(App.getInstance().getPackageManager());
        if (componentName != null) {
            context.startActivity(intent);
        } else {
            AppToast.showToast("您没有安装浏览器。");
        }
    }

    /**
     * 统计微博字符个数
     *
     * @param s 待统计字符串
     * @return
     */
    public static int calcLength(String s) {
        double len = 0;
        for (int i = 0; i < s.length(); i++) {
            int temp = (int) s.charAt(i);
            if (temp > 0 && temp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return (int) len;
    }

    /**
     * 拍照
     */
    public static String capture(Activity activity, int requestCode) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String picName = null;
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (BaseConfig.sSDCardExist) {
                picName = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + Environment.DIRECTORY_DCIM + File.separator +
                        "Camera" + File.separator + activity.getString(R.string.app_name) +
                        "_" + System.currentTimeMillis() + ".jpg";

            } else {
                picName = activity.getCacheDir().getPath() + File.separator +
                        activity.getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".jpg";
            }
            File photoFile = new File(picName);
//            FileUtils.createFile(photoFile);

            Uri uri = FileProvider.getUriForFile(activity,
                    App.getInstance().getApplicationId() + ".provider", photoFile);

            List<ResolveInfo> resInfoList = activity.getPackageManager()
                    .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(cameraIntent, requestCode);
        } else {
            AppToast.showToast(R.string.open_camera_failure);
        }
        return picName;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param activity
     * @param uri         图片uri
     * @param requestCode 请求码
     */
    public static void clipPhoto(Activity activity, Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 图片格式
        intent.putExtra("outputFormat", "PNG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 从Url中获取文件名
     *
     * @param url
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        String name = String.valueOf(System.currentTimeMillis());
        try {
            name = url.substring(url.lastIndexOf('/') + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
