package cc.duduhuo.simpler.style;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import cc.duduhuo.simpler.R;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/2 19:49
 * 版本：1.0
 * 描述：自定义可点击Span
 * 备注：
 * =======================================================
 */
public abstract class CustomClickableSpan extends ClickableSpan {
    private Context context;
    private String msg;
    private int type;
    public static final int TYPE_AT = 0x0000;
    public static final int TYPE_TOPIC = 0x0001;
    public static final int TYPE_URL = 0x0002;

    public CustomClickableSpan(Context context, int type, String msg) {
        this.context = context;
        this.type = type;
        this.msg = msg;
    }

    @Override
    public void onClick(View widget) {
    }

    /**
     * 在这里设置需要的颜色和是否需要下滑线
     *
     * @param ds
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        if (type == TYPE_AT) {
            ds.setColor(ContextCompat.getColor(context, R.color.status_color_at));
        } else if (type == TYPE_TOPIC) {
            ds.setColor(ContextCompat.getColor(context, R.color.status_color_topic));
        } else if (type == TYPE_URL) {
            ds.setColor(ContextCompat.getColor(context, R.color.status_color_link));
        }
        ds.setUnderlineText(false);
    }
}
