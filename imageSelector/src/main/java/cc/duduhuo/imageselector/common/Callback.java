package cc.duduhuo.imageselector.common;

import java.io.File;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 19:50
 * 版本：1.0
 * 描述：图片选择器回调接口
 * 备注：
 * =======================================================
 */
public interface Callback {

    void onSingleImageSelected(String path);

    void onImageSelected(String path);

    void onImageUnselected(String path);

    void onCameraShot(File imageFile);

    void onPreviewChanged(int select, int sum, boolean visible);
}
