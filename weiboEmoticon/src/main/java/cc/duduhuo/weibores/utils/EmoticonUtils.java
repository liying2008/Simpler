package cc.duduhuo.weibores.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.duduhuo.weibores.R;
import cc.duduhuo.weibores.entities.Emoticon;
import cc.duduhuo.weibores.style.VerticalImageSpan;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 作者：https://github.com/teambition/yykEmoji
 * 日期：2017/3/13 19:13
 * 版本：1.0
 * 描述：Emoticon（微博表情）工具类
 * 备注：
 * =======================================================
 */
public class EmoticonUtils {
    /**
     * 表情图片资源数组
     */
    public static final int[] EmoticonResArray = {
            R.drawable.dl_weixiao,
            // miss
            R.drawable.dl_huaxin,
            R.drawable.dl_chijing,
            R.drawable.d_zuiyou,
            R.drawable.d_hehe,
            R.drawable.d_keai,
            R.drawable.d_taikaixin,
            R.drawable.d_guzhang,
            R.drawable.d_xixi,
            R.drawable.d_haha,
            R.drawable.d_xiaoku,
            R.drawable.d_jiyan,
            R.drawable.d_chanzui,
            R.drawable.d_landelini,
            R.drawable.d_heixian,
            R.drawable.d_wabishi,
            R.drawable.d_heng,
            R.drawable.d_nu,
            R.drawable.d_zhuakuang,
            R.drawable.d_weiqu,
            R.drawable.d_kelian,
            R.drawable.d_shiwang,
            R.drawable.d_beishang,
            R.drawable.d_lei,
            R.drawable.d_haixiu,
            R.drawable.d_wu,
            R.drawable.d_aini,
            R.drawable.d_qinqin,
            R.drawable.d_huaxin,
            R.drawable.d_tian,
            R.drawable.d_qian,
            R.drawable.d_doge,
            R.drawable.d_miao,
            R.drawable.d_erha,
            R.drawable.d_ku,
            R.drawable.d_huaixiao,
            R.drawable.d_yinxian,
            R.drawable.d_touxiao,
            R.drawable.d_sikao,
            R.drawable.d_yiwen,
            R.drawable.d_yun,
            R.drawable.d_shayan,
            R.drawable.d_shuai,
            R.drawable.d_kulou,
            R.drawable.d_xu,
            R.drawable.d_bizui,
            R.drawable.d_han,
            R.drawable.d_chijing,
            R.drawable.d_ganmao,
            R.drawable.d_shengbing,
            R.drawable.d_tu,
            R.drawable.d_baibai,
            R.drawable.d_bishi,
            R.drawable.d_zuohengheng,
            R.drawable.d_youhengheng,
            R.drawable.d_numa,
            R.drawable.d_dalian,
            R.drawable.d_ding,
            R.drawable.d_dahaqi,
            R.drawable.d_kun,
            R.drawable.d_shuijiao,
            R.drawable.f_hufen,
            R.drawable.d_baobao,
            R.drawable.d_tanshou,
            R.drawable.l_xin,
            R.drawable.l_shangxin,
            R.drawable.w_xianhua,
            R.drawable.d_nanhaier,
            R.drawable.d_nvhaier,
            R.drawable.h_woshou,
            R.drawable.h_zuoyi,
            R.drawable.h_zan,
            R.drawable.h_ye,
            R.drawable.h_good,
            R.drawable.h_ruo,
            R.drawable.h_buyao,
            R.drawable.h_ok,
            R.drawable.h_haha,
            R.drawable.h_lai,
            R.drawable.h_quantou,
            R.drawable.h_jiayou,
            R.drawable.d_xiongmao,
            R.drawable.d_tuzi,
            R.drawable.d_zhutou,
            R.drawable.d_shenshou,
            R.drawable.d_aoteman,
            R.drawable.w_taiyang,
            R.drawable.w_yueliang,
            R.drawable.w_fuyun,
            R.drawable.w_xiayu,
            R.drawable.w_shachenbao,
            R.drawable.w_weifeng,
            R.drawable.o_feiji,
            R.drawable.o_zhaoxiangji,
            R.drawable.o_huatong,
            R.drawable.o_yinyue,
            R.drawable.f_geili,
            R.drawable.f_v5,
            R.drawable.o_lazhu,
            R.drawable.o_weiguan,
            R.drawable.o_ganbei,
            R.drawable.o_dangao,
            R.drawable.o_liwu,
            R.drawable.f_xi,
            R.drawable.o_zhong,
            R.drawable.d_feizao,
            R.drawable.o_lvsidai,
            R.drawable.o_weibo,
            R.drawable.d_lang,
            R.drawable.lxh_xiudada,
            R.drawable.lxh_haoaio,
            R.drawable.lxh_toule,
            R.drawable.lxh_zana,
            R.drawable.lxh_xiaohaha,
            R.drawable.lxh_haoxihuan,
            R.drawable.lxh_qiuguanzhu,
            R.drawable.lxh_oye,
    };

    /**
     * 表情描述数组
     */
    public static final String[] EmoticonTextArray = {
            "[哆啦A梦微笑]",
            //"[哆啦A梦汗]",
            "[哆啦A梦花心]",
            "[哆啦A梦吃惊]",
            "→_→",
            "[微笑]",
            "[可爱]",
            "[太开心]",
            "[鼓掌]",
            "[嘻嘻]",
            "[哈哈]",
            "[笑cry]",
            "[挤眼]",
            "[馋嘴]",
            "[白眼]",
            "[黑线]",
            "[挖鼻]",
            "[哼]",
            "[怒]",
            "[抓狂]",
            "[委屈]",
            "[可怜]",
            "[失望]",
            "[悲伤]",
            "[泪]",
            "[害羞]",
            "[污]",
            "[爱你]",
            "[亲亲]",
            "[色]",
            "[舔屏]",
            "[钱]",
            "[doge]",
            "[喵喵]",
            "[二哈]",
            "[酷]",
            "[坏笑]",
            "[阴险]",
            "[偷笑]",
            "[思考]",
            "[疑问]",
            "[晕]",
            "[傻眼]",
            "[衰]",
            "[骷髅]",
            "[嘘]",
            "[闭嘴]",
            "[汗]",
            "[吃惊]",
            "[感冒]",
            "[生病]",
            "[吐]",
            "[拜拜]",
            "[鄙视]",
            "[左哼哼]",
            "[右哼哼]",
            "[怒骂]",
            "[打脸]",
            "[顶]",
            "[哈欠]",
            "[困]",
            "[睡]",
            "[互粉]",
            "[抱抱]",
            "[摊手]",
            "[心]",
            "[伤心]",
            "[鲜花]",
            "[男孩儿]",
            "[女孩儿]",
            "[握手]",
            "[作揖]",
            "[赞]",
            "[耶]",
            "[good]",
            "[弱]",
            "[NO]",
            "[ok]",
            "[haha]",
            "[来]",
            "[拳头]",
            "[加油]",
            "[熊猫]",
            "[兔子]",
            "[猪头]",
            "[草泥马]",
            "[奥特曼]",
            "[太阳]",
            "[月亮]",
            "[浮云]",
            "[下雨]",
            "[沙尘暴]",
            "[微风]",
            "[飞机]",
            "[照相机]",
            "[话筒]",
            "[音乐]",
            "[给力]",
            "[威武]",
            "[蜡烛]",
            "[围观]",
            "[干杯]",
            "[蛋糕]",
            "[礼物]",
            "[喜]",
            "[钟]",
            "[肥皂]",
            "[绿丝带]",
            "[围脖]",
            "[浪]",
            "[羞嗒嗒]",
            "[好爱哦]",
            "[偷乐]",
            "[赞啊]",
            "[笑哈哈]",
            "[好喜欢]",
            "[求关注]",
            "[噢耶]",
    };

    static {
        emoticonList = generateEmoticons();
    }

    /**
     * Emoticon列表
     */
    private static ArrayList<Emoticon> emoticonList;

    public static ArrayList<Emoticon> getEmoticonList() {
        if (emoticonList == null) {
            emoticonList = generateEmoticons();
        }
        return emoticonList;
    }

    private static ArrayList<Emoticon> generateEmoticons() {
        ArrayList<Emoticon> list = new ArrayList<>();
        for (int i = 0; i < EmoticonResArray.length; i++) {
            Emoticon emoticon = new Emoticon();
            emoticon.setImageUri(EmoticonResArray[i]);
            emoticon.setContent(EmoticonTextArray[i]);
            list.add(emoticon);
        }
        return list;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        return zoomImage(bitmap, reqWidth, reqHeight);
    }

    /***
     * 图片的缩放方法
     *
     * @param bitmap 源图片资源
     * @param newWidth 缩放后宽度
     * @param newHeight 缩放后高度
     * @return 处理后的Bitmap对象
     */
    private static Bitmap zoomImage(Bitmap bitmap, double newWidth,
                                    double newHeight) {
        // 获取这个图片的宽和高
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, (int) width,
                (int) height, matrix, true);
        return bmp;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    /**
     * 处理带表情的文本（显示表情符号）
     *
     * @param editText
     * @param content
     * @param context
     */
    public static void handleEmoticonText(EditText editText, String content, Context context) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoticon> iterator;
        Emoticon emoticon = null;
        while (m.find()) {
            iterator = emoticonList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoticon = iterator.next();
                if (tempText.equals(emoticon.getContent())) {
                    //转换为Span并设置Span的大小
                    sb.setSpan(new VerticalImageSpan(context,
                                    decodeSampledBitmapFromResource(context.getResources(),
                                            emoticon.getImageUri(), dip2px(context, 16), dip2px(context, 16))),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        editText.setText(sb);
        editText.setSelection(sb.length());
    }

}
