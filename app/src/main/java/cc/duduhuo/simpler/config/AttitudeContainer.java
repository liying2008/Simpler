package cc.duduhuo.simpler.config;

import android.support.v4.util.ArrayMap;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/27 21:25
 * 版本：1.0
 * 描述：记录已经点过的赞
 * 备注：
 * =======================================================
 */
public class AttitudeContainer {
    /**
     * 点赞的微博ID和Attitude ID
     */
    public static ArrayMap<Long, Long> sHeartContainer = new ArrayMap<>();
}
