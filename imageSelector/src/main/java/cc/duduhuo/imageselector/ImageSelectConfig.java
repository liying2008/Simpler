package cc.duduhuo.imageselector;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;

import cc.duduhuo.imageselector.utils.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 13:52
 * 版本：1.0
 * 描述：图片选择器配置器类
 * 备注：
 * =======================================================
 */
public class ImageSelectConfig {
    /** 是否需要裁剪 */
    public boolean needCrop;
    /** 是否多选 */
    public boolean multiSelect = false;
    /** 是否记住上次的选中记录(只对多选有效) */
    public boolean rememberSelected = true;
    /** 最多选择图片数 */
    public int maxNum = 9;
    /** 第一个item是否显示相机 */
    public boolean needCamera;
    /** 返回图标资源 */
    public int backResId = -1;
    /** 拍照存储路径 */
    public String filePath;
    /** 自定义图片加载器 */
    public ImageLoader loader;
    /** 裁剪输出大小 */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;

    public ImageSelectConfig(Builder builder) {
        this.needCrop = builder.needCrop;
        this.multiSelect = builder.multiSelect;
        this.rememberSelected = builder.rememberSelected;
        this.maxNum = builder.maxNum;
        this.needCamera = builder.needCamera;
        this.backResId = builder.backResId;
        this.filePath = builder.filePath;
        this.loader = builder.loader;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
    }

    public static class Builder implements Serializable {

        private boolean needCrop = false;
        private boolean multiSelect = true;
        private boolean rememberSelected = true;
        private int maxNum = 9;
        private boolean needCamera = true;
        private int backResId = -1;
        private String filePath;
        private ImageLoader loader;

        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 400;
        private int outputY = 400;

        public Builder(ImageLoader loader) {
            this.loader = loader;

            if (FileUtils.isSdCardAvailable()) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Camera";
            }
            else {
                filePath = Environment.getRootDirectory().getAbsolutePath() + File.separator + "Camera";
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder rememberSelected(boolean rememberSelected) {
            this.rememberSelected = rememberSelected;
            return this;
        }

        public Builder maxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder needCamera(boolean needCamera) {
            this.needCamera = needCamera;
            return this;
        }

        public Builder backResId(int backResId) {
            this.backResId = backResId;
            return this;
        }

        private Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder cropSize(int aspectX, int aspectY, int outputX, int outputY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
            return this;
        }

        public ImageSelectConfig build() {
            return new ImageSelectConfig(this);
        }
    }
}
