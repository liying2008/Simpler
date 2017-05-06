package cc.duduhuo.simpler.task;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import com.sina.weibo.sdk.openapi.legacy.ShortUrlAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.activity.WBLoginActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.StatusAnalyzeListener;
import cc.duduhuo.simpler.listener.StatusItemListener;
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
 * 描述：微博文本特殊元素解析任务类
 * 备注：
 * =======================================================
 */
public class StatusAnalyzeTask extends AsyncTask<String, Void, SpannableStringBuilder> {
    // 微博元素正则表达式
    private static final String AT = "@[\\u4e00-\\u9fa5a-zA-Z0-9_-]{2,30}";// @人
    private static final String TOPIC = "#[^#]+#";// ##话题
    private static final String EMOTICON = "\\[(\\S+?)\\]";// 表情
    private static final String URL = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";// url
    private Context mContext;
    private StatusItemListener mItemListener;
    private StatusAnalyzeListener mAnalyzeListener;
    private static final String sWebLink = "网页链接 >";
    private static final String sVideoLink = "视频链接 >";
    private static final String sMiaoPaiLink = "秒拍视频 >";
    private static final String sPhotoLink = "查看图片 >";
    private ShortUrlAPI mUrlAPI;

    private static final Pattern patternAT = Pattern.compile(AT);
    private static final Pattern patternTOPIC = Pattern.compile(TOPIC);
    private static final Pattern patternEMOTICON = Pattern.compile(EMOTICON);
    private static final Pattern patternURL = Pattern.compile(URL);

    private StatusAnalyzeTask(Context context, StatusItemListener itemListener) {
        this.mContext = context;
        this.mItemListener = itemListener;
        if (mUrlAPI == null) {
            // 短链API
            mUrlAPI = new ShortUrlAPI(context, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
    }

    public static StatusAnalyzeTask getInstance(Context context, StatusItemListener itemListener) {
        return new StatusAnalyzeTask(context, itemListener);
    }

    public void setStatusAnalyzeListener(StatusAnalyzeListener analyzeListener) {
        this.mAnalyzeListener = analyzeListener;
    }

    @Override
    protected SpannableStringBuilder doInBackground(String... params) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(params[0]);

        Matcher matcherAT = patternAT.matcher(ssb);
        Matcher matcherTOPIC = patternTOPIC.matcher(ssb);
        Matcher matcherEMOTICON = patternEMOTICON.matcher(ssb);
        Matcher matcherURL = patternURL.matcher(ssb);
        while (matcherAT.find()) {
            final String at = matcherAT.group();
            // AT
            if (!TextUtils.isEmpty(at)) {
                int start = matcherAT.start();
                int end = start + at.length();
                CustomClickableSpan ccsAt = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_AT, at) {
                    @Override
                    public void onClick(View widget) {
                        mItemListener.onItemAtListener(widget, at);
                    }
                };
                ssb.setSpan(ccsAt, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        while (matcherTOPIC.find()) {
            final String topic = matcherTOPIC.group();
            // 话题
            if (!TextUtils.isEmpty(topic)) {
                int start = matcherTOPIC.start();
                int end = start + topic.length();
                CustomClickableSpan ccsTopic = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_TOPIC, topic) {
                    @Override
                    public void onClick(View widget) {
                        mItemListener.onItemTopicListener(widget, topic);
                    }
                };
                ssb.setSpan(ccsTopic, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        while (matcherEMOTICON.find()) {
            final String emoticon = matcherEMOTICON.group();
            // 表情
            if (!TextUtils.isEmpty(emoticon)) {
                int start = matcherEMOTICON.start();
                int end = start + emoticon.length();
                Iterator<Emoticon> iterator;
                Emoticon e = null;
                iterator = EmoticonUtils.getEmoticonList().iterator();
                while (iterator.hasNext()) {
                    e = iterator.next();
                    if (emoticon.equals(e.getContent())) {
                        //转换为Span并设置Span的大小
                        ssb.setSpan(new VerticalImageSpan(mContext, EmoticonUtils.decodeSampledBitmapFromResource(
                                mContext.getResources(), e.getImageUri(), EmoticonUtils.dip2px(mContext, 16),
                                EmoticonUtils.dip2px(mContext, 16))), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        while (matcherURL.find()) {
            final String url = matcherURL.group();
            // URL
            if (!TextUtils.isEmpty(url)) {
                final int start = matcherURL.start();
                final int end = start + url.length();
                if (url.startsWith("http://t.cn/")) {
                    // 短链转长链
                    try {
                        String s = mUrlAPI.expandSync(new String[]{url});
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                if (jsonObject.isNull("error")) {
                                    JSONArray array = jsonObject.getJSONArray("urls");
                                    if (array != null && array.length() > 0) {
                                        JSONObject obj = (JSONObject) array.get(0);
                                        final String longUrl = obj.getString("url_long");
//                                        Log.d("Url", longUrl);
                                        if (longUrl.startsWith("http://video.weibo.com")) {
                                            // 微博视频
                                            CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
                                                @Override
                                                public void onClick(View widget) {
                                                    mItemListener.onItemVideoLinkListener(widget, longUrl);
                                                }
                                            };
                                            ssb = ssb.replace(start, end, sVideoLink);
                                            ssb.setSpan(ccsLink, start, start + sVideoLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            matcherURL = patternURL.matcher(ssb);
                                        } else if (longUrl.startsWith("http://www.miaopai.com") || longUrl.startsWith("http://miaopai.com")) {
                                            // 秒拍视频
                                            final String miaoPaiUrl = longUrl.replace("http://miaopai.com", "http://www.miaopai.com");
                                            CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, miaoPaiUrl) {
                                                @Override
                                                public void onClick(View widget) {
                                                    mItemListener.onItemMiaoPaiLinkListener(widget, miaoPaiUrl);
                                                }
                                            };
                                            ssb = ssb.replace(start, end, sMiaoPaiLink);
                                            ssb.setSpan(ccsLink, start, start + sMiaoPaiLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            matcherURL = patternURL.matcher(ssb);
                                        } else if (longUrl.startsWith("http://photo.weibo.com")) {
                                            // 微博图片
                                            CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
                                                @Override
                                                public void onClick(View widget) {
                                                    mItemListener.onItemPhotoLinkListener(widget, longUrl);
                                                }
                                            };
                                            ssb = ssb.replace(start, end, sPhotoLink);
                                            ssb.setSpan(ccsLink, start, start + sPhotoLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            matcherURL = patternURL.matcher(ssb);
                                        } else {
                                            // 网页链接
                                            CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, longUrl) {
                                                @Override
                                                public void onClick(View widget) {
                                                    mItemListener.onItemWebLinkListener(widget, longUrl);
                                                }
                                            };
                                            ssb = ssb.replace(start, end, sWebLink);
                                            ssb.setSpan(ccsLink, start, start + sWebLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            matcherURL = patternURL.matcher(ssb);
                                        }
                                    }
                                } else {
                                    int errorCode = jsonObject.optInt("error_code");
                                    if (errorCode == 10006 || errorCode == 21332) {
                                        // 授权过期
                                        AppToast.showToast("应用授权过期，请重新授权");
                                        if (!BaseConfig.sTokenExpired) {
                                            BaseConfig.sTokenExpired = true;
                                            App.getInstance().finishAllActivities();
                                            mContext.startActivity(WBLoginActivity.newIntent(mContext));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomClickableSpan ccsLink = new CustomClickableSpan(mContext, CustomClickableSpan.TYPE_URL, url) {
                        @Override
                        public void onClick(View widget) {
                            mItemListener.onItemWebLinkListener(widget, url);
                        }
                    };
                    ssb = ssb.replace(start, end, sWebLink);
                    ssb.setSpan(ccsLink, start, start + sWebLink.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    matcherURL = patternURL.matcher(ssb);
                }
            }
        }
        return ssb;
    }
    @Override
    protected void onPostExecute(SpannableStringBuilder ssb) {
        super.onPostExecute(ssb);
        if (mAnalyzeListener != null) {
            mAnalyzeListener.onSpannableStringComplete(ssb);
        }
    }
}
