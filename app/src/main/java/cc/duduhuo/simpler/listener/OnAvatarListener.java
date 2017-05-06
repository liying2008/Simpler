package cc.duduhuo.simpler.listener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/3 21:34
 * 版本：1.0
 * 描述：用户头像相关实现接口
 * 备注：
 * =======================================================
 */
public interface OnAvatarListener {
    /**
     * 更新当前登录用户的头像
     *
     * @param path 图片路径
     */
    void upload(String path);
}
