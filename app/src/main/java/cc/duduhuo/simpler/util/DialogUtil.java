package cc.duduhuo.simpler.util;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.listener.OnDialogOpListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 16:02
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class DialogUtil {
    /**
     * 让Dialog从底部出现/宽度全屏
     *
     * @param dialog
     * @param rootView
     */
    public static void setBottom(Dialog dialog, View rootView) {
        Window window = dialog.getWindow();
        // 在5.0以上手机必须加上这句代码
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.BottomDialogStyle);  //添加动画
        window.setContentView(rootView);//布局
    }

    /**
     * 显示选择操作对话框
     *
     * @param activity
     * @param op1      第一个操作项名称
     * @param op2      第二个操作项名称
     * @param listener 选择结果回调
     */
    public static void showSelectDialog(Activity activity, String op1, String op2, final OnDialogOpListener listener) {
        final AlertDialog dialog = App.getAlertDialogBuilder(activity).create();
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_about_me_info, null);
        TextView tvCancel = ButterKnife.findById(view, R.id.tvCancel);
        TextView tvOp1 = ButterKnife.findById(view, R.id.tvOp1);
        TextView tvOp2 = ButterKnife.findById(view, R.id.tvOp2);
        tvOp1.setText(op1);
        tvOp2.setText(op2);
        dialog.show();
        setBottom(dialog, view);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvOp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onOp1();
                }
            }
        });
        tvOp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onOp2();
                }
            }
        });
    }

}
