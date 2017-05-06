package cc.duduhuo.imageselector.bean;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/smuyyh/ImageSelector
 * 日期：2017/4/9 18:13
 * 版本：1.0
 * 描述：图片实体类
 * 备注：
 * =======================================================
 */
public class Image {

    public String path;
    public String name;
    public long time;

    public boolean isCamera = false;

    public Image(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public Image(){
        isCamera = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            Image oI = (Image) o;
            if (oI.path.equalsIgnoreCase((this.path))) {
                return true;
            }
        }
        return false;
    }
}