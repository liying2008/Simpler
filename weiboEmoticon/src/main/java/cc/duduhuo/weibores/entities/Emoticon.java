package cc.duduhuo.weibores.entities;

import java.io.Serializable;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/teambition/yykEmoji
 * 日期：2017/3/13 19:13
 * 版本：1.0
 * 描述：Emoticon（微博表情）实体类
 * 备注：
 * =======================================================
 */
public class Emoticon implements Serializable {
    private int imageUri;
    private String content;

    public int getImageUri() {
        return imageUri;
    }

    public void setImageUri(int imageUri) {
        this.imageUri = imageUri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Emoticon) {
            Emoticon e = (Emoticon) obj;
            if (e.content.equals(content)) {
                return true;
            }
        }
        return false;
    }
}
