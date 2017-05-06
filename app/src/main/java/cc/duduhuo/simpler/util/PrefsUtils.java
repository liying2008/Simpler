package cc.duduhuo.simpler.util;

import cc.duduhuo.simpler.app.App;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/3 13:17
 * 版本：1.0
 * 描述：SharedPreferences工具类
 * 备注：
 * =======================================================
 */
public class PrefsUtils {
    
    private PrefsUtils() {
        throw new AssertionError();
    }

    /**
     * put string preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public static void putString(String key, String value) {
        App.getInstance().editor.putString(key, value).apply();
    }

    /**
     * get string preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    public static String getString(String key, String defValue) {
        return App.getInstance().prefs.getString(key, defValue);
    }

    /**
     * put int preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public static void putInt(String key, int value) {
        App.getInstance().editor.putInt(key, value).apply();
    }

    /**
     * get int preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a int
     */
    public static int getInt(String key, int defValue) {
        return App.getInstance().prefs.getInt(key, defValue);
    }

    /**
     * put long preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public static void putLong(String key, long value) {
        App.getInstance().editor.putLong(key, value).apply();
    }

    /**
     * get long preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    public static long getLong(String key, long defValue) {
        return App.getInstance().prefs.getLong(key, defValue);
    }

    /**
     * put float preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public static void putFloat(String key, float value) {
        App.getInstance().editor.putFloat(key, value).apply();
    }

    /**
     * get float preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a float
     */
    public static float getFloat(String key, float defValue) {
        return App.getInstance().prefs.getFloat(key, defValue);
    }

    /**
     * put boolean preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    public static void putBoolean(String key, boolean value) {
        App.getInstance().editor.putBoolean(key, value).apply();
    }

    /**
     * get boolean preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a boolean
     */
    public static boolean getBoolean(String key, boolean defValue) {
        return App.getInstance().prefs.getBoolean(key, defValue);
    }

    /**
     * 清空preference
     * <b>谨慎</b>
     */
    public static void clear() {
        App.getInstance().editor.clear().apply();
    }
}
