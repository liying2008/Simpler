package cc.duduhuo.simpler.listener;
import android.text.SpannableStringBuilder;
/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/2 19:57
 * 版本：1.0
 * 描述：微博文本经特殊元素分析后转为SpannableStringBuilder
 * 备注：
 * =======================================================
 */
public interface StatusAnalyzeListener {
    void onSpannableStringComplete(SpannableStringBuilder ssb);
}
