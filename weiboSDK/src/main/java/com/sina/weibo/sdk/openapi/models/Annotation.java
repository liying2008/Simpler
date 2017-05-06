package com.sina.weibo.sdk.openapi.models;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/22 15:05
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class Annotation {
    /*
    "annotations": [
                {
                    "place": {
                        "lon": 123.42167,
                        "poiid": "B2094450D66EA7FD4398",
                        "title": "东北大学浑南校区",
                        "type": "checkin",
                        "lat": 41.65364
                    },
                    "client_mblogid": "f9a83ead-14e7-4a0e-a233-bc6f792cf35c"
                },
                {
                    "mapi_request": true
                }
            ],
     */
    public double lon;
    public String poiid;
    public String title;
    public String type;
    public double lat;
    public String client_mblogid;
    public boolean mapi_request;

    public static Annotation parse(JSONArray annotationArray) {
        Annotation annotation = null;
        if (annotationArray != null && annotationArray.length() > 0) {
            annotation = new Annotation();
            int length = annotationArray.length();
            if (length == 2) {
                JSONObject ob = (JSONObject) annotationArray.opt(0);
                JSONObject place = ob.optJSONObject("place");
                if (place != null) {
                    annotation.lon = place.optDouble("lon");
                    annotation.lat = place.optDouble("lat");
                    annotation.poiid = place.optString("poiid");
                    annotation.title = place.optString("title");
                    annotation.type = place.optString("type");
                }
                annotation.client_mblogid = ob.optString("client_mblogid");

                JSONObject request = (JSONObject) annotationArray.opt(1);
                annotation.mapi_request = request.optBoolean("mapi_request");
            }
        }
        return annotation;
    }
    @Override
    public String toString() {
        return "Annotation{" +
                "lon=" + lon +
                ", poiid='" + poiid + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", lat=" + lat +
                ", client_mblogid='" + client_mblogid + '\'' +
                ", mapi_request=" + mapi_request +
                '}';
    }
}
