package cc.duduhuo.simpler.util;

import android.text.TextUtils;

import com.sina.weibo.sdk.openapi.models.GeoOri;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/13 20:11
 * 版本：1.0
 * 描述：地理信息解析器
 * 备注：
 * =======================================================
 */
public class GeoParser {
    private static final String TAG = "GeoParser";

    /**
     * 根据微博原始geo信息获取地址信息
     *
     * @param geoOri 微博原始Geo信息
     */
    public void getAddress(GeoOri geoOri, OnLocationListener locationListener) {
        if (geoOri != null) {
            getAddress(geoOri.latitude, geoOri.longitude, locationListener);
        }
    }

    /**
     * 根据经纬度获取地址信息
     *
     * @param latitude  纬度
     * @param longitude 经度
     */
    public void getAddress(double latitude, double longitude, final OnLocationListener locationListener) {
        String url = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0&ak=" + Constants.BAIDU_LBS_AK;
        HttpGetTask task = new HttpGetTask(false, new HttpListener() {
            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    String jsonStr = response.substring(29, response.length() - 1);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        int status = jsonObject.optInt("status");
                        if (status == 0) {
                            // 正常
                            JSONObject object = jsonObject.optJSONObject("result");
                            String formattedAddress = object.optString("formatted_address");
                            if (locationListener != null) {
                                locationListener.onGetAddress(formattedAddress);
                            }
                        } else {
                            // 异常
                            if (locationListener != null) {
                                locationListener.onFailure(status);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (locationListener != null) {
                            locationListener.onError(e.getMessage());
                        }
                    }

                } else {
                    if (locationListener != null) {
                        locationListener.onError("返回结果空");
                    }
                }
            }

            @Override
            public void onFailure() {
                if (locationListener != null) {
                    locationListener.onError("请求失败");
                }
            }
        });
        task.execute(url, null);
    }

    /**
     * geo转location回调
     */
    public interface OnLocationListener {
        /**
         * 得到结果
         *
         * @param formattedAddress 地址信息
         */
        void onGetAddress(String formattedAddress);

        /**
         * 状态码错误
         *
         * @param status 状态码
         */
        void onFailure(int status);

        /**
         * 其他错误
         *
         * @param message 错误信息
         */
        void onError(String message);
    }
}
