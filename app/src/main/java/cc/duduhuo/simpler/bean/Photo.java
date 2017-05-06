package cc.duduhuo.simpler.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/7 20:45
 * 版本：1.0
 * 描述：图片实体类
 * 备注：
 * =======================================================
 */
public class Photo implements Parcelable {
    /** 图片路径 */
    public String path;

    public Photo() {
    }

    public Photo(String path) {
        this.path = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
    }

    protected Photo(Parcel in) {
        this.path = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
