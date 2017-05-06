package cc.duduhuo.simpler.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/26 14:24
 * 版本：1.0
 * 描述：Bitmap、byte[]、Drawable、InputStream相互转化工具类
 * 备注：
 * =======================================================
 */
public class DrawableUtil {

    /**
     * 将byte[]转换成InputStream
     *
     * @param bytes
     * @return
     */
    public static InputStream bytes2InputStream(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    /**
     * 将InputStream转换成byte[]（方式1）
     *
     * @param inStream
     * @return
     */
    public static final byte[] inputStream2Bytes(InputStream inStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 将Bitmap转换成InputStream
     *
     * @param bm
     * @param quality Hint to the compressor, 0-100. 0 meaning compress for
     *                small size, 100 meaning compress for max quality. Some
     *                formats, like PNG which is lossless, will ignore the
     *                quality setting
     * @return
     */
    public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * 将InputStream转换成Bitmap
     *
     * @param inStream
     * @return
     */
    public static Bitmap inputStream2Bitmap(InputStream inStream) {
        return BitmapFactory.decodeStream(inStream);
    }

    /**
     * Drawable转换成InputStream
     *
     * @param drawable
     * @return
     */
    public static InputStream drawable2InputStream(Drawable drawable) {
        Bitmap bitmap = drawable2Bitmap(drawable);
        return bitmap2InputStream(bitmap, 100);
    }

    /**
     * InputStream转换成Drawable
     *
     * @param inStream
     * @return
     */
    public static Drawable inputStream2Drawable(InputStream inStream) {
        Bitmap bitmap = inputStream2Bitmap(inStream);
        return bitmap2Drawable(bitmap);
    }

    /**
     * Drawable转换成byte[]
     *
     * @param drawable
     * @return
     */
    public static byte[] drawable2Bytes(Drawable drawable) {
        Bitmap bitmap = drawable2Bitmap(drawable);
        return bitmap2Bytes(bitmap);
    }

    /**
     * byte[]转换成Drawable
     *
     * @param bytes
     * @return
     */
    public static Drawable bytes2Drawable(byte[] bytes) {
        Bitmap bitmap = bytes2Bitmap(bytes);
        return bitmap2Drawable(bitmap);
    }

    /**
     * Bitmap转换成byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte[]转换成Bitmap
     *
     * @param bytes
     * @return
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    /**
     * Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转换成Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        return bd;
    }
}
