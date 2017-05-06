package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/10 14:02
 * 版本：1.0
 * 描述：微博上传的图片的点击监听
 * 备注：
 * =======================================================
 */
public interface PostPicClickListener {
    /**
     * 点击添加图片
     */
    void onAdd();

    /**
     * 点击删除图片
     *
     * @param position
     */
    void onDelete(int position);
}
