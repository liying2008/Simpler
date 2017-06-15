package cc.duduhuo.simpler.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.PicQuality;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/21 17:00
 * 版本：1.0
 * 描述：Bitmap工具类
 * 备注：
 * =======================================================
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    private BitmapUtils() {
    }

    /**
     * 得到上传图片的Bitmap
     *
     * @param source 图片路径
     * @return
     */
    public static Bitmap getUploadImage(String source) {
        File file = new File(source);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(source);

        if (source.toLowerCase().endsWith(".gif")) {
            Log.w(TAG, "上传图片是GIF图片，上传原图");
            return BitmapFactory.decodeFile(source);
        }
        BaseSettings.sSettings = SettingsUtil.readSettings(BaseConfig.sUid, false);
        int type = BaseSettings.sSettings.uploadQuality;
        int size = bitmap.getByteCount();
        int sizeM;
        switch (type) {
            case PicQuality.ORIGINAL:
                sizeM = 5;
                if (size < sizeM * 1024) {
                    // 小于sizeM
                    return bitmap;
                } else {
                    Bitmap image = getImage(bitmap, sizeM);
                    Log.d("Image", image.getByteCount() + "字节");
                    return image;
                }
            case PicQuality.MIDDLE:
                sizeM = 4;
                if (size < sizeM * 1024) {
                    // 小于sizeM
                    return bitmap;
                } else {
                    return getImage(bitmap, sizeM);
                }
            case PicQuality.THUMBNAIL:
                sizeM = 3;
                return getImage(bitmap, sizeM);
            default:
                return null;
        }
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param bitmap
     * @param sizeM  图片大小（以MB为单位）
     * @return
     */
    public static Bitmap getImage(Bitmap bitmap, int sizeM) {
        Bitmap bmp = bitmap;
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 获取这个图片的宽和高
        do {
            float width = bmp.getWidth();
            float height = bmp.getHeight();
            // 缩放图片动作
            matrix.postScale(0.8F, 0.8F);
            bmp = Bitmap.createBitmap(bmp, 0, 0, (int) width,
                    (int) height, matrix, true);
        } while (bmp.getByteCount() > sizeM * 1024 * 1024);
        return bmp;
    }
}
