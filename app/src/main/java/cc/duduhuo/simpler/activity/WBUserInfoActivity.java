package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.UserInfoAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.KeyValue;
import cc.duduhuo.simpler.util.UserUtil;

public class WBUserInfoActivity extends BaseActivity {
    /** Intent传值的键（用户User对象） */
    private static final String INTENT_USER = "user";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.rvInfo)
    RecyclerView mRvInfo;

    private UserInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_user_info);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvInfo.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        User user = intent.getParcelableExtra(INTENT_USER);
        List<KeyValue> keyValues = UserUtil.analyzeUser(user);
        mTvTitle.setText(getString(R.string.title_info, user.name));
        mAdapter = new UserInfoAdapter(this, keyValues);
        mRvInfo.setAdapter(mAdapter);
    }

    public static Intent newIntent(Context context, Parcelable user) {
        Intent intent = new Intent(context, WBUserInfoActivity.class);
        intent.putExtra(INTENT_USER, user);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
