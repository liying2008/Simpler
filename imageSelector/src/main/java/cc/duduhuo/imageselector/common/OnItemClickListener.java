package cc.duduhuo.imageselector.common;


import cc.duduhuo.imageselector.bean.Image;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 16:59
 * 版本：1.0
 * 描述：Item点击监听
 * 备注：
 * =======================================================
 */
public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
