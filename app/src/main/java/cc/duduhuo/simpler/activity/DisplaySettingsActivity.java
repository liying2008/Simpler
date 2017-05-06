package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.OptionsAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.dialog.OptionsDialog;
import cc.duduhuo.simpler.bean.Option;
import cc.duduhuo.simpler.config.PicQuality;
import cc.duduhuo.simpler.util.OptionUtil;
import cc.duduhuo.simpler.util.SettingsUtil;

public class DisplaySettingsActivity extends BaseActivity {
    @BindView(R.id.tvCount)
    TextView mTvCount;
    @BindView(R.id.tvAutoRefresh)
    TextView mTvAutoRefresh;
    @BindView(R.id.tvPicQuality)
    TextView mTvPicQuality;
    @BindView(R.id.tvUploadQuality)
    TextView mTvUploadQuality;
    @BindView(R.id.tvFontSize)
    TextView mTvFontSize;

    private OptionsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);
        ButterKnife.bind(this);
        dialog = new OptionsDialog(this);
        init();
    }

    private void init() {
        mTvCount.setText(String.valueOf(BaseSettings.sSettings.refreshCount));
        if (BaseSettings.sSettings.autoRefresh) {
            mTvAutoRefresh.setText("是");
        } else {
            mTvAutoRefresh.setText("否");
        }
        switch (BaseSettings.sSettings.picQuality) {
            case PicQuality.NO_PIC:
                mTvPicQuality.setText("无图");
                break;
            case PicQuality.INTELLIGENT:
                mTvPicQuality.setText("智能无图");
                break;
            case PicQuality.THUMBNAIL:
                mTvPicQuality.setText("小图");
                break;
            case PicQuality.MIDDLE:
                mTvPicQuality.setText("中图");
                break;
            case PicQuality.ORIGINAL:
                mTvPicQuality.setText("原始大图");
                break;
        }
        switch (BaseSettings.sSettings.uploadQuality) {
            case PicQuality.THUMBNAIL:
                mTvUploadQuality.setText("小图");
                break;
            case PicQuality.MIDDLE:
                mTvUploadQuality.setText("中图");
                break;
            case PicQuality.ORIGINAL:
                mTvUploadQuality.setText("原始大图");
                break;
        }
        mTvFontSize.setText(BaseSettings.sSettings.fontSize + "pt");
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DisplaySettingsActivity.class);
        return intent;
    }

    @OnClick(R.id.rlStatusCount)
    void selectCount() {
        final List<Option> options = OptionUtil.getRefreshCountOptions();
        dialog.setTitle("每次刷新微博数");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvCount.setText(options.get(position).name);
                SettingsUtil.updateRefreshCount((int) options.get(position).value);
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rlAutoRefresh)
    void selectAutoRefresh() {
        final List<Option> options = OptionUtil.getAutoRefreshOptions();
        dialog.setTitle("每次打开自动刷新微博");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvAutoRefresh.setText(options.get(position).name);
                SettingsUtil.updateAutoRefresh((boolean) options.get(position).value);
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rlPicQuality)
    void selectPicQuality() {
        final List<Option> options = OptionUtil.getPicQualityOptions();
        dialog.setTitle("微博图片显示质量");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvPicQuality.setText(options.get(position).name);
                SettingsUtil.updatePicQuality((int) options.get(position).value);
                dialog.dismiss();
                if (App.getInstance().mSettingsChangeListener != null) {
                    App.getInstance().mSettingsChangeListener.onPicQualityChange();
                }
            }
        });
    }

    @OnClick(R.id.rlUploadQuality)
    void selectUploadQuality() {
        final List<Option> options = OptionUtil.getUploadQualityOptions();
        dialog.setTitle("图片上传质量");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvUploadQuality.setText(options.get(position).name);
                SettingsUtil.updateUploadQuality((int) options.get(position).value);
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rlFontSize)
    void selectFontSize() {
        final List<Option> options = OptionUtil.getFontSizeOptions();
        dialog.setTitle("微博字体大小");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvFontSize.setText(options.get(position).name);
                SettingsUtil.updateFontSize((int) options.get(position).value);
                dialog.dismiss();
                if (App.getInstance().mSettingsChangeListener != null) {
                    App.getInstance().mSettingsChangeListener.onTextSizeChange((int) options.get(position).value);
                }
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog = null;
    }
}
