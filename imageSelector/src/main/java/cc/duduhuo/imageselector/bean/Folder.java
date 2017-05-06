package cc.duduhuo.imageselector.bean;

import java.util.List;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 19:49
 * 版本：1.0
 * 描述：图片文件夹实体类
 * 备注：
 * =======================================================
 */
public class Folder {

    public String name;
    public String path;
    public Image cover;
    public List<Image> images;

    public boolean isAll = false;

    public Folder() {

    }

    public Folder(boolean isAll) {
        this.isAll = isAll;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Folder) {
            Folder oF = (Folder) o;
            if (oF.path.equalsIgnoreCase(this.path)) {
                return true;
            }
        }
        return false;
    }
}