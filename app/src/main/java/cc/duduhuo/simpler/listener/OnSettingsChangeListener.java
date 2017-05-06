package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/17 0:11
 * 版本：1.0
 * 描述：设置项改变监听
 * 备注：
 * =======================================================
 */
public interface OnSettingsChangeListener {
    /**
     * 字体大小改变
     * @param size
     */
    void onTextSizeChange(int size);

    /**
     * 微博图片质量改变
     */
    void onPicQualityChange();

    /**
     * 收藏状态改变
     */
    void onFavoriteStateChange(int position, boolean favorite);
}
