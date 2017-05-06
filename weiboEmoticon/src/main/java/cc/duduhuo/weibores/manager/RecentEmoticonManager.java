package cc.duduhuo.weibores.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;


/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/teambition/yykEmoji
 * 日期：2017/4/10 22:13
 * 版本：1.0
 * 描述：最近表情管理类
 * 备注：
 * =======================================================
 */
public class RecentEmoticonManager {
    public static final String PREFERENCE_NAME = "recentEmoticon";//"preference";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private RecentEmoticonManager(Context context) {
        mPreferences = context.getSharedPreferences(RecentEmoticonManager.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static RecentEmoticonManager make(Context context) {
        return new RecentEmoticonManager(context);
    }


    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public RecentEmoticonManager putString(String key, String value) {
        mEditor.putString(key, value).apply();
        return this;
    }


    public RecentEmoticonManager putCollection(String key, Collection collection) throws IOException {
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(collection);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String collectionString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        // 关闭objectOutputStream
        objectOutputStream.close();
        return putString(key, collectionString);
    }

    public Collection getCollection(String key) throws IOException, ClassNotFoundException {
        String collectionString = getString(key);
        if (TextUtils.isEmpty(collectionString) || TextUtils.isEmpty(collectionString.trim())) {
            return null;
        }
        byte[] mobileBytes = Base64.decode(collectionString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Collection collection = (Collection) objectInputStream.readObject();
        objectInputStream.close();
        return collection;
    }
}
