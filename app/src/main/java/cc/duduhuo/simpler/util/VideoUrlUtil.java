package cc.duduhuo.simpler.util;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/5 21:56
 * 版本：1.0
 * 描述：视频链接解析工具
 * 备注：
 * =======================================================
 */
public class VideoUrlUtil {
    /**
     * 转换前：http://www.miaopai.com/show/n9QkGYFwWfCU0tNWU4LQDUzHWgVHlM-h.html    <br />
     * 转换后：http://gslb.miaopai.com/stream/n9QkGYFwWfCU0tNWU4LQDUzHWgVHlM-h.mp4  <br />
     *
     * @param miaoPaiUrl 秒拍视频页面地址
     * @return
     */
    public static String getRealVideoPath(String miaoPaiUrl) {
        // 暂不支持：http://m.miaopai.com/show/channerWbH5/1034:9518ff44a47d60897f175901129e27f7
//        Log.d("Path", miaoPaiUrl);
        int end = miaoPaiUrl.lastIndexOf(".");
        String sub = miaoPaiUrl.substring(28, end);
        String realPath = "http://gslb.miaopai.com/stream/" + sub + ".mp4";
        return realPath;
    }
}
