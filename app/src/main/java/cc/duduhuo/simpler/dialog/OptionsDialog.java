package cc.duduhuo.simpler.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.OptionsAdapter;
import cc.duduhuo.simpler.bean.Option;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/9 15:46
 * 版本：1.0
 * 描述：选择应用默认使用的浏览器
 * 备注：
 * =======================================================
 */
public class OptionsDialog extends AlertDialog {
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.rvOptions)
    RecyclerView mRvOptions;

    private String mTitle;
    private List<Option> mOptions;
    private Context mContext;
    private OptionsAdapter mAdapter;

    public OptionsDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_option_list);
        ButterKnife.bind(this);

        Window window = getWindow();
        // 在5.0以上手机必须加上这句代码
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.BottomDialogStyle);  //添加动画

        mRvOptions.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void setOptions(List<Option> options) {
        this.mOptions = options;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setOnOptionItemSelectListener(OptionsAdapter.OnOptionItemSelectListener listener) {
        mAdapter.setOnOptionItemSelect(listener);
    }

    @OnClick(R.id.tvCancel)
    void close() {
        this.dismiss();
    }

    @Override
    public void show() {
        super.show();
        mTvTitle.setText(mTitle);
        mAdapter = new OptionsAdapter(mOptions);
        mRvOptions.setAdapter(mAdapter);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
