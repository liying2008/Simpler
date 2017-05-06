package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.OptionsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.dialog.OptionsDialog;
import cc.duduhuo.simpler.bean.Option;
import cc.duduhuo.simpler.service.UnreadService;
import cc.duduhuo.simpler.util.OptionUtil;
import cc.duduhuo.simpler.util.SettingsUtil;

public class NotificationSettingsActivity extends BaseActivity {
    @BindView(R.id.switchMessage)
    SwitchCompat mSwitchMessage;
    @BindView(R.id.switchPrivateLetter)
    SwitchCompat mSwitchPrivateLetter;
    @BindView(R.id.tvNotifyInterval)
    TextView mTvNotifyInterval;
    @BindView(R.id.rlNotifyInterval)
    RelativeLayout mRlNotifyInterval;
    @BindView(R.id.tvNotifyIntervalMsg)
    TextView mTvNotifyIntervalMsg;

    private OptionsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        dialog = new OptionsDialog(this);
        mSwitchMessage.setOnCheckedChangeListener(new CheckChangeListener());
        mSwitchPrivateLetter.setOnCheckedChangeListener(new CheckChangeListener());
        init();
    }

    private void init() {
        mSwitchMessage.setChecked(BaseSettings.sSettings.messageNotification);
        mSwitchPrivateLetter.setChecked(BaseSettings.sSettings.privateLetterNotification);
        if (BaseSettings.sSettings.notifyInterval == 30) {
            mTvNotifyInterval.setText("半分钟");
        } else {
            mTvNotifyInterval.setText((BaseSettings.sSettings.notifyInterval / 60) + "分钟");
        }

        if (!BaseSettings.sSettings.messageNotification && !BaseSettings.sSettings.privateLetterNotification) {
            disableNotifyInterval();
            BaseSettings.sNotifyEnable = false;
        } else {
            enableNotifyInterval();
            BaseSettings.sNotifyEnable = true;
        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NotificationSettingsActivity.class);
        return intent;
    }

    private void enableNotifyInterval() {
        mRlNotifyInterval.setClickable(true);
        mTvNotifyIntervalMsg.setTextColor(0xff555555);
    }

    private void disableNotifyInterval() {
        mRlNotifyInterval.setClickable(false);
        mTvNotifyIntervalMsg.setTextColor(0xffAAAAAA);
    }

    @OnClick(R.id.rlNotifyInterval)
    void setNotifyInterval() {
        final List<Option> options = OptionUtil.getNotifyIntervalOptions();
        dialog.setTitle("通知时间间隔");
        dialog.setOptions(options);
        dialog.show();
        dialog.setOnOptionItemSelectListener(new OptionsAdapter.OnOptionItemSelectListener() {
            @Override
            public void onSelect(int position) {
                mTvNotifyInterval.setText(options.get(position).name);
                SettingsUtil.updateNotifyInterval((int) options.get(position).value);
                UnreadService.updateAlarm();    // 更新Service Alarm
                dialog.dismiss();
            }
        });

    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    private class CheckChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.switchMessage) {
                // 消息通知
                SettingsUtil.updateMessageNotification(isChecked);
                if (isChecked) {
                    if (!BaseSettings.sNotifyEnable) {
                        // 启用消息通知
                        UnreadService.startService();
                        enableNotifyInterval();
                    }
                } else {
                    if (!mSwitchPrivateLetter.isChecked() && BaseSettings.sNotifyEnable) {
                        // 停止消息通知
                        UnreadService.stopService();
                        disableNotifyInterval();
                    }
                }
            } else if (buttonView.getId() == R.id.switchPrivateLetter) {
                // 私信通知
                SettingsUtil.updatePrivateLetterNotification(isChecked);
                if (isChecked) {
                    if (!BaseSettings.sNotifyEnable) {
                        // 启用消息通知
                        UnreadService.startService();
                        enableNotifyInterval();
                    }
                } else {
                    if (!mSwitchMessage.isChecked() && BaseSettings.sNotifyEnable) {
                        // 停止消息通知
                        UnreadService.stopService();
                        disableNotifyInterval();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog = null;
    }
}
