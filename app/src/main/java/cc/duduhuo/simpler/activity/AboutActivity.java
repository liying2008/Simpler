package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.listener.OnDialogOpListener;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.tvVersion)
    TextView mTvVersion;
    @BindView(R.id.tvUpdateDate)
    TextView mTvUpdateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化数据
     */
    private void init() {
        mTvVersion.setText(getString(R.string.version_name_prefix, App.getInstance().getVersionName()));
        mTvUpdateDate.setText(App.getInstance().getUpdateDate());
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }

    /**
     * 分享应用
     */
    @OnClick(R.id.rlShare)
    void share() {
        String content = getString(R.string.share_description, getString(R.string.app_name));
        content += "\n\n——发送自 " + getString(R.string.app_name);
        CommonUtils.shareText(this, content);
    }

    @OnClick(R.id.rlOpenSource)
    void openSourceAddr() {
        final String url = "https://github.com/liying2008/Simpler";
        DialogUtil.showSelectDialog(this, "复制地址", "在浏览器中打开", new OnDialogOpListener() {
            @Override
            public void onOp1() {
                CommonUtils.copyText(AboutActivity.this, url);
                AppToast.showToast(R.string.copied);
            }

            @Override
            public void onOp2() {
                CommonUtils.openBrowser(AboutActivity.this, url);
            }
        });
    }

    @OnClick(R.id.rlAboutAuthor)
    void aboutAuthor() {
        startActivity(AboutMeActivity.newIntent(this));
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 开源相关
     */
    @OnClick(R.id.tvThirdOpen)
    void thirdOpen() {
        startActivity(ThirdOpenActivity.newIntent(this));
    }
}
