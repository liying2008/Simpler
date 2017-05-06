package cc.duduhuo.simpler.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/22 20:07
 * 版本：1.0
 * 描述：微博草稿
 * 备注：
 * =======================================================
 */
public class Draft implements Parcelable{
    public static final String ID = "_id";
    public static final String UID = "uid";
    public static final String CONTENT = "content";
    public static final String PHOTO_LIST = "photo_list";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IS_ENABLE_GEO = "is_enable_geo";
    public static final String IS_LOCATION = "is_location";
    public static final String ADDR_STR = "addr_str";
    public static final String MENU_ID = "menu_id";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";

    /** 主键 */
    public int id;
    /** 用户Id */
    public String uid;
    /** 草稿内容 */
    public String content;
    /** 照片列表 */
    public List<Photo> photoList;
    /** 纬度 */
    public double latitude;
    /** 经度 */
    public double longitude;
    /** 是否上传定位（默认不上传） */
    public boolean isEnableGeo;
    /** 是否已经获取过位置信息 */
    public boolean isLocation;
    /** 地址信息 */
    public String addrStr;
    /** 微博可见性菜单Id */
    public int menuId;
    /** 分组Id */
    public String groupId;
    /** 分组名称 */
    public String groupName;

    public Draft() {
    }

    public Draft(String uid, String content, List<Photo> photoList, double latitude, double longitude, boolean isEnableGeo, boolean isLocation, String addrStr, int menuId, String groupId, String groupName) {
        this.uid = uid;
        this.content = content;
        this.photoList = photoList;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isEnableGeo = isEnableGeo;
        this.isLocation = isLocation;
        this.addrStr = addrStr;
        this.menuId = menuId;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Draft(int id, String uid, String content, List<Photo> photoList, double latitude, double longitude, boolean isEnableGeo, boolean isLocation, String addrStr, int menuId, String groupId, String groupName) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.photoList = photoList;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isEnableGeo = isEnableGeo;
        this.isLocation = isLocation;
        this.addrStr = addrStr;
        this.menuId = menuId;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    /**
     * 将PhotoList转为String（存储在数据库中）
     * @param photoList
     * @return
     */
    public static String photoList2String(List<Photo> photoList) {
        if (photoList == null || photoList.isEmpty()) {
            return "";
        }
        String list = "";
        for (int i = 0; i < photoList.size(); i++) {
            list += photoList.get(i).path + "\n";
        }
        return list;
    }

    /**
     * 将String转为PhotoList（从数据库中取出）
     * @param photoStr
     * @return
     */
    public static List<Photo> string2PhotoList(String photoStr) {
        if (TextUtils.isEmpty(photoStr)) {
            return new ArrayList<>(1);
        }
        List<Photo> photos = new ArrayList<>(9);
        String[] photoArr = photoStr.split("\n");
        for (int i = 0; i < photoArr.length; i++) {
            photos.add(new Photo(photoArr[i]));
        }
        return photos;
    }

    /**
     * 默认数据
     */
    public static final class Default {
        public static final String CONTENT = "";
        public static final List<Photo> PHOTO_LIST = null;
        public static final double LATITUDE = 0.0D;
        public static final double LONGITUDE = 0.0D;
        public static final boolean IS_ENABLE_GEO = false;
        public static final boolean IS_LOCATION = false;
        public static final String ADDR_STR = "";
        public static final int MENU_ID = MenuItem.MENU_ID_ALL_VISIBLE;
        public static final String GROUP_ID = "";
        public static final String GROUP_NAME = "所有人可见";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.content);
        dest.writeList(this.photoList);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeByte(this.isEnableGeo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocation ? (byte) 1 : (byte) 0);
        dest.writeString(this.addrStr);
        dest.writeInt(this.menuId);
        dest.writeString(this.groupId);
        dest.writeString(this.groupName);
    }

    protected Draft(Parcel in) {
        this.id = in.readInt();
        this.uid = in.readString();
        this.content = in.readString();
        this.photoList = new ArrayList<Photo>();
        in.readList(this.photoList, Photo.class.getClassLoader());
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.isEnableGeo = in.readByte() != 0;
        this.isLocation = in.readByte() != 0;
        this.addrStr = in.readString();
        this.menuId = in.readInt();
        this.groupId = in.readString();
        this.groupName = in.readString();
    }

    public static final Creator<Draft> CREATOR = new Creator<Draft>() {
        @Override
        public Draft createFromParcel(Parcel source) {
            return new Draft(source);
        }

        @Override
        public Draft[] newArray(int size) {
            return new Draft[size];
        }
    };
}
