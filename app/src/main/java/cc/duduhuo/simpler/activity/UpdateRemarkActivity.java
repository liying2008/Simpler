package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 10:49
 * 版本：1.0
 * 描述：更新好友备注
 * 备注：
 * =======================================================
 */
public class UpdateRemarkActivity extends BaseActivity {
    private static final String TAG = "UpdateRemarkActivity";
    public static final String INTENT_NEW_REMARK = "remark";
    private static final String INTENT_UID = "uid";
    private static final String INTENT_REMARK = "remark";
    private static final String INTENT_NAME = "name";

    @BindView(R.id.etRemark)
    EditText mEtRemark;

    private long mUid;
    private String mName;
    private FriendshipsAPI mFApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_remark);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUid = intent.getLongExtra(INTENT_UID, 0L);
        mName = intent.getStringExtra(INTENT_NAME);
        String remark = intent.getStringExtra(INTENT_REMARK);
        mEtRemark.setText(remark);
        mEtRemark.setSelection(remark.length());
    }

    public static Intent newIntent(Context context, long uid, String remark, String name) {
        Intent intent = new Intent(context, UpdateRemarkActivity.class);
        intent.putExtra(INTENT_UID, uid);
        intent.putExtra(INTENT_REMARK, remark);
        intent.putExtra(INTENT_NAME, name);
        return intent;
    }

    @OnClick(R.id.tvOk)
    void update() {
        if (mFApi == null) {
            mFApi = new FriendshipsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        final String remark;
        if ("".equals(mEtRemark.getText().toString())) {
            if (TextUtils.isEmpty(mName)) {
                AppToast.showToast(R.string.remark_cannot_null);
                return;
            }
            remark = mName;
        } else {
            remark = mEtRemark.getText().toString();
        }
        AppToast.showToast(R.string.remark_updating);
        mFApi.remarkUpdate(mUid, remark, new RequestListener() {
            @Override
            public void onComplete(String s) {
                User user = User.parse(s);
                if (user != null && !"".equals(user.screen_name)) {
                    // 更新成功
                    AppToast.showToast(R.string.remark_update_success);
                    Intent data = new Intent();
                    data.putExtra(INTENT_NEW_REMARK, remark);
                    setResult(RESULT_OK, data);
                    finish();
                    return;
                }
                AppToast.showToast(R.string.remark_update_failure);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                AppToast.showToast(R.string.remark_update_failure);
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
