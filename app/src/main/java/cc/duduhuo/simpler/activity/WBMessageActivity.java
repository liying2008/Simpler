package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.RemindCount;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.listener.impl.RemindOp;
import cc.duduhuo.simpler.util.NumberFormatter;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/24 14:14
 * 版本：1.0
 * 描述：消息界面
 * 备注：
 * =======================================================
 */
public class WBMessageActivity extends BaseActivity {
    private static final String TAG = "WBMessageActivity";
    private static final int REQUEST_CODE_FOLLOWER = 0;
    private static final int REQUEST_CODE_CMT = 1;
    private static final int REQUEST_CODE_DM = 2;
    private static final int REQUEST_CODE_MSGBOX = 3;
    private static final int REQUEST_CODE_STATUS_AT_ME = 4;
    private static final int REQUEST_CODE_CMT_AT_ME = 5;
    @BindView(R.id.tvFollowersCount)
    TextView mTvFollowersCount;
    @BindView(R.id.tvCmtsCount)
    TextView mTvCmtsCount;
    @BindView(R.id.tvDmsCount)
    TextView mTvDmsCount;
    @BindView(R.id.tvMsgBoxCount)
    TextView mTvMsgBoxCount;
    @BindView(R.id.tvStatusAtMeCount)
    TextView mTvStatusAtMeCount;
    @BindView(R.id.tvCmtAtMeCount)
    TextView mTvCmtAtMeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_message);
        ButterKnife.bind(this);

        queryUnreadCount();
    }

    private void queryUnreadCount() {
        RemindOp remindOp = new RemindOp(this);
        remindOp.onUnreadCount(Long.parseLong(BaseConfig.sUid));
        remindOp.setOnRemindOpResultListener(new RemindOp.OnRemindOpResultListener() {
            @Override
            public void onUnreadCountSuccess(RemindCount count) {
                if (count.follower > 0) {
                    // 有新粉丝
                    mTvFollowersCount.setVisibility(View.VISIBLE);
                    mTvFollowersCount.setText(NumberFormatter.formatUnreadCount(count.follower));
                }
                if (count.cmt > 0) {
                    // 有新评论
                    mTvCmtsCount.setVisibility(View.VISIBLE);
                    mTvCmtsCount.setText(NumberFormatter.formatUnreadCount(count.cmt));
                }
                if (count.dm > 0) {
                    // 有新私信
                    mTvDmsCount.setVisibility(View.VISIBLE);
                    mTvDmsCount.setText(NumberFormatter.formatUnreadCount(count.dm));
                }
                if (count.msgbox > 0) {
                    // 有新未关注人私信
                    mTvMsgBoxCount.setVisibility(View.VISIBLE);
                    mTvMsgBoxCount.setText(NumberFormatter.formatUnreadCount(count.msgbox));
                }
                if (count.mention_status > 0) {
                    // 有提及我的微博
                    mTvStatusAtMeCount.setVisibility(View.VISIBLE);
                    mTvStatusAtMeCount.setText(NumberFormatter.formatUnreadCount(count.mention_status));
                }
                if (count.mention_cmt > 0) {
                    // 有提及我的评论
                    mTvCmtAtMeCount.setVisibility(View.VISIBLE);
                    mTvCmtAtMeCount.setText(NumberFormatter.formatUnreadCount(count.mention_cmt));
                }
            }

            @Override
            public void onUnreadCountFailure(String msg) {
                AppToast.showToast("获取未读消息数目失败");
            }

            @Override
            public void onSetCountSuccess() {
                // no op
            }

            @Override
            public void onSetCountFailure(String msg) {
                // no op
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, WBMessageActivity.class);
        return intent;
    }

    @OnClick(R.id.rlFollowers)
    void viewFollowers() {
        startActivityForResult(WBFollowersActivity.newIntent(this, Long.parseLong(BaseConfig.sUid),
                BaseConfig.sAccount.name), REQUEST_CODE_FOLLOWER);
    }

    @OnClick(R.id.rlCmts)
    void viewCmts() {
        startActivityForResult(CmtMentionActivity.newIntent(this, CmtMentionActivity.TYPE_TO_ME), REQUEST_CODE_CMT);
    }

    @OnClick(R.id.rlDms)
    void viewDms() {
        startActivityForResult(WebWBActivity.newIntent(this, false), REQUEST_CODE_DM);
    }

    @OnClick(R.id.rlMsgBox)
    void viewMsgBox() {
        startActivityForResult(WebWBActivity.newIntent(this, false), REQUEST_CODE_DM);
    }

    @OnClick(R.id.rlStatusAtMe)
    void viewStatusAtMe() {
        startActivityForResult(StatusMentionActivity.newIntent(this), REQUEST_CODE_STATUS_AT_ME);
    }

    @OnClick(R.id.rlCmtAtMe)
    void viewCmtAtMe() {
        startActivityForResult(CmtMentionActivity.newIntent(this, CmtMentionActivity.TYPE_MENTION), REQUEST_CODE_CMT_AT_ME);
    }

    @OnClick(R.id.rlCmtByMe)
    void viewCmtByMe() {
        startActivity(CmtMentionActivity.newIntent(this, CmtMentionActivity.TYPE_BY_ME));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOLLOWER) {
                mTvFollowersCount.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_CMT) {
                mTvCmtsCount.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_DM) {
                mTvDmsCount.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_MSGBOX) {
                mTvMsgBoxCount.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_STATUS_AT_ME) {
                mTvStatusAtMeCount.setVisibility(View.GONE);
            } else if (requestCode == REQUEST_CODE_CMT_AT_ME) {
                mTvCmtAtMeCount.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
