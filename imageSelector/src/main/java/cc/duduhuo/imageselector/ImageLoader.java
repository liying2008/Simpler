package cc.duduhuo.imageselector;

import android.content.Context;
import android.widget.ImageView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 19:58
 * 版本：1.0
 * 描述：图片加载接口
 * 备注：
 * =======================================================
 */
public interface ImageLoader {
    /**
     * 开发者可根据项目中使用的图片加载框架加载图片
     * @param context
     * @param path
     * @param imageView
     */
    void displayImage(Context context, String path, ImageView imageView);
}