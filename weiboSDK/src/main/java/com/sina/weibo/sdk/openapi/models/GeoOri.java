package com.sina.weibo.sdk.openapi.models;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/13 18:36
 * 版本：1.0
 * 描述：微博原始Geo字段
 * 备注：
 * =======================================================
 */
public class GeoOri {
    /** 类型 */
    public String type;
    /** 经度 */
    public double longitude;
    /** 纬度 */
    public double latitude;

    public static GeoOri parse(JSONObject object) {
        GeoOri geoOri = null;
        if (object != null) {
            geoOri = new GeoOri();
            geoOri.type = object.optString("type");
            JSONArray array = object.optJSONArray("coordinates");
            geoOri.latitude = array.optDouble(0);
            geoOri.longitude = array.optDouble(1);
        }
        return geoOri;
    }

    @Override
    public String toString() {
        return "GeoOri{" +
                "type='" + type + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
