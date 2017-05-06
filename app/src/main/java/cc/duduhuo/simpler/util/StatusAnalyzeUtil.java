package cc.duduhuo.simpler.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.StatusItemListener;
import cc.duduhuo.simpler.listener.impl.ShortUrlOp;
import cc.duduhuo.simpler.style.CustomClickableSpan;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.style.VerticalImageSpan;
import cc.duduhuo.weibores.entities.Emoticon;
import cc.duduhuo.weibores.utils.EmoticonUtils;


/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/2 19:42
 * 版本：1.0
 * 描述：微博文本特殊元素解析工具类
 * 备注：暂时废弃
 * =======================================================
 */
public class StatusAnalyzeUtil {
//    // 微博元素正则表达式
//    private static final String AT = "@[\\u4e00-\\u9fa5a-zA-Z0-9_-]{2,30}";// @人
//    private static final String TOPIC = "#[^#]+#";// ##话题
//    private static final String EMOTICON = "\\[(\\S+?)\\]";// 表情
//    private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
//    private Context mContext;
//    private StatusItemListener mItemListener;
//    private static final String sWebLink = "网页链接 >";
//    private static final String sVideoLink = "视频链接 >";
//    private static final String sMiaoPaiLink = "秒拍视频 >";
//    private static final String sPhotoLink = "查看图片 >";
//    private ShortUrlAPI mUrlAPI;
//
//    private Pattern patternAT = Pattern.compile(AT);
//    private Pattern patternTOPIC = Pattern.compile(TOPIC);
//    private Pattern patternEMOTICON = Pattern.compile(EMOTICON);
//    private Pattern patternURL = Pattern.compile(URL);
//
//    public StatusAnalyzeUtil(Context context, StatusItemListener itemListener) {
//        this.mContext = context;
//        this.mItemListener = itemListener;
//        // 短链API
//        mUrlAPI = new ShortUrlAPI(context, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
//    }
//
//    public SpannableStringBuilder analyze(final String text) {
//        final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
//
//        Matcher matcherAT = patternAT.matcher(ssb);
//        while (matcherAT.find()) {
//            final String at = matcherAT.group();
//
//            // AT
//            if (!TextUtils.isEmpty(at)) {
//                int start = matcherAT.start();
//                int end = start + at.length();
//                CustomClickableSpan ccsAt = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_AT, at) {
//                    @Override
//                    public void onClick(View widget) {
//                        mItemListener.onItemAtListener(widget, at);
//                    }
//                };
//                ssb.setSpan(ccsAt, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//
//        }
//
//        Matcher matcherTOPIC = patternTOPIC.matcher(ssb);
//        while (matcherTOPIC.find()) {
//            final String topic = matcherTOPIC.group();
//
//            // 话题
//            if (!TextUtils.isEmpty(topic)) {
//                int start = matcherTOPIC.start();
//                int end = start + topic.length();
//                CustomClickableSpan ccsTopic = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_TOPIC, topic) {
//                    @Override
//                    public void onClick(View widget) {
//                        mItemListener.onItemTopicListener(widget, topic);
//                    }
//
//                };
//                ssb.setSpan(ccsTopic, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//
//        Matcher matcherEMOTICON = patternEMOTICON.matcher(ssb);
//        while (matcherEMOTICON.find()) {
//            final String emoticon = matcherEMOTICON.group();
//
//            // 表情
//            if (!TextUtils.isEmpty(emoticon)) {
//                int start = matcherEMOTICON.start();
//                int end = start + emoticon.length();
//
//                Iterator<Emoticon> iterator;
//                Emoticon e = null;
//                iterator = EmoticonUtils.getEmoticonList().iterator();
//                while (iterator.hasNext()) {
//                    e = iterator.next();
//                    if (emoticon.equals(e.getContent())) {
//                        //转换为Span并设置Span的大小
//                        ssb.setSpan(new VerticalImageSpan(mContext, EmoticonUtils.decodeSampledBitmapFromResource(
//                                mContext.getResources(), e.getImageUri(), EmoticonUtils.dip2px(mContext, 16),
//                                EmoticonUtils.dip2px(mContext, 16))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//            }
//        }
//
//        final ArrayList<Matcher> matcherURL = new ArrayList<>(1);
//        matcherURL.add(patternURL.matcher(ssb));
//        while (matcherURL.get(0).find()) {
//            final String url = matcherURL.get(0).group();
////            Log.d("Url", url);
//            // URL
//            if (!TextUtils.isEmpty(url)) {
//                final int start = matcherURL.get(0).start();
//                final int end = start + url.length();
//
//                if (url.startsWith("http://t.cn/")) {
//                    // 短链转长链
//                    ShortUrlOp shortUrlOp = new ShortUrlOp(mUrlAPI);
//                    shortUrlOp.expand(url);
//                    shortUrlOp.setOnShortUrlOpResultListener(new ShortUrlOp.OnShortUrlOpResultListener() {
//                        @Override
//                        public void onSuccess(final String longUrl) {
//
//                            if (longUrl.startsWith("http://video.weibo.com")) {
//                                // 微博视频
//                                CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
//                                    @Override
//                                    public void onClick(View widget) {
//                                        mItemListener.onItemVideoLinkListener(widget, longUrl);
//                                    }
//                                };
//
//                                SpannableStringBuilder temp = ssb.replace(start, end, sVideoLink);
//                                ssb.clear();
//                                ssb.append(temp);
//                                ssb.setSpan(ccsLink, start, start + sVideoLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                matcherURL.clear();
//                                matcherURL.add(patternURL.matcher(ssb));
//                            } else if (longUrl.startsWith("http://www.miaopai.com") || longUrl.startsWith("http://miaopai.com")) {
//                                // 秒拍视频
//                                final String miaoPaiUrl = longUrl.replace("http://miaopai.com", "http://www.miaopai.com");
//                                CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, miaoPaiUrl) {
//                                    @Override
//                                    public void onClick(View widget) {
//                                        mItemListener.onItemMiaoPaiLinkListener(widget, miaoPaiUrl);
//                                    }
//                                };
//
//                                SpannableStringBuilder temp = ssb.replace(start, end, sMiaoPaiLink);
//                                ssb.clear();
//                                ssb.append(temp);
//                                ssb.setSpan(ccsLink, start, start + sMiaoPaiLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                matcherURL.clear();
//                                matcherURL.add(patternURL.matcher(ssb));
//                            } else if (longUrl.startsWith("http://photo.weibo.com")) {
//                                // 微博图片
//                                CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
//                                    @Override
//                                    public void onClick(View widget) {
//                                        mItemListener.onItemPhotoLinkListener(widget, longUrl);
//                                    }
//                                };
//
//                                SpannableStringBuilder temp = ssb.replace(start, end, sPhotoLink);
//                                ssb.clear();
//                                ssb.append(temp);
//                                ssb.setSpan(ccsLink, start, start + sPhotoLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                matcherURL.clear();
//                                matcherURL.add(patternURL.matcher(ssb));
//                            } else {
//                                // 网页链接
//                                CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
//                                    @Override
//                                    public void onClick(View widget) {
//                                        mItemListener.onItemWebLinkListener(widget, longUrl);
//                                    }
//                                };
//
//                                SpannableStringBuilder temp = ssb.replace(start, end, sWebLink);
//                                ssb.clear();
//                                ssb.append(temp);
//                                ssb.setSpan(ccsLink, start, start + sWebLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                matcherURL.clear();
//                                matcherURL.add(patternURL.matcher(ssb));
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String msg) {
//                            /*
//                                    {
//                                        "error": "Error: Parameter value is not valid!",
//                                        "error_code": 21506,
//                                        "request": "/2/sinaurl/public/expand.json"
//                                    }
//                             */
//                        }
//                    });
//                } else {
//                    CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, url) {
//                        @Override
//                        public void onClick(View widget) {
//                            mItemListener.onItemWebLinkListener(widget, url);
//                        }
//                    };
//
//                    SpannableStringBuilder temp = ssb.replace(start, end, sWebLink);
//                    ssb.clear();
//                    ssb.append(temp);
//                    ssb.setSpan(ccsLink, start, start + sWebLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    matcherURL.clear();
//                    matcherURL.add(patternURL.matcher(ssb));
//                }
//            }
//        }
//        return ssb;
//    }
}
