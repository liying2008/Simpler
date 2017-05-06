package cc.duduhuo.imageselector.common;


import cc.duduhuo.imageselector.bean.Folder;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 19:51
 * 版本：1.0
 * 描述：更改图片文件夹回调接口
 * 备注：
 * =======================================================
 */
public interface OnFolderChangeListener {
    /**
     * 文件夹改变
     *
     * @param position
     * @param folder
     */
    void onChange(int position, Folder folder);

    /**
     * 文件夹选中
     *
     * @param position
     * @param folder
     */
    void onSelect(int position, Folder folder);
}
