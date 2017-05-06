package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.UserListAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.FriendshipOp;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class WBFriendsActivity extends BaseActivity {
    private static final String TAG = "WBFriendsActivity";
    /** Intent传值的键（用户id） */
    private static final String INTENT_UID = "uid";
    /** Intent传值的键（用户友好名称） */
    private static final String INTENT_NAME = "name";
    /** 页面标题 */
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvFriends)
    RecyclerView mRvFriends;
    /** 用户列表适配器 */
    private UserListAdapter mAdapter;
    /** 用户Id */
    private long mUid;
    /** 当前查询结果游标 */
    private int mCursor = 0;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            queryFriends(mUid, 0, true);
            mSwipeRefresh.setRefreshing(true);
        }
    };
    private DSwipeRefresh.OnLoadingListener loadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mCursor != 0) {
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                queryFriends(mUid, mCursor, false);
            }
        }
    };
    private FriendshipsAPI mFAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_friends);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUid = intent.getLongExtra(INTENT_UID, 0L);
        String name = intent.getStringExtra(INTENT_NAME);
        mTvTitle.setText(getString(R.string.title_friends, name));
        mAdapter = new UserListAdapter(WBFriendsActivity.this, Long.parseLong(BaseConfig.sUid) == mUid);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvFriends, mAdapter);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mSwipeRefresh.setOnLoadingListener(loadingListener);

        queryFriends(mUid, 0, true);  // 查询粉丝信息
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long uid, String name) {
        Intent intent = new Intent(context, WBFriendsActivity.class);
        intent.putExtra(INTENT_UID, uid);
        intent.putExtra(INTENT_NAME, name);
        return intent;
    }

    /**
     * 查询关注信息
     *
     * @param uid     用户id
     * @param cursor  游标
     * @param refresh 是否刷新列表
     */
    private void queryFriends(long uid, final int cursor, final boolean refresh) {
        if (mFAPI == null) {
            mFAPI = new FriendshipsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
            FriendshipOp friendshipOp = new FriendshipOp(this, mFAPI);
            friendshipOp.setOnFriendshipOpResultListener(new MyFriendshipOp());
            mAdapter.setOnFriendshipListener(friendshipOp);
        }
        mFAPI.friends(uid, 50, cursor, true, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                List<User> friends = new ArrayList<>(50);
                if (!TextUtils.isEmpty(s)) {
//                    Log.d(TAG, s);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(s);
                        JSONArray array = jsonObject.getJSONArray("users");
                        int length = array.length();
//                        Log.d(TAG, "数量：" + length);
                        if (length > 0) {
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                User user = User.parse(obj);
                                friends.add(user);
                            }
                            // 下一页的游标
                            mCursor = jsonObject.getInt("next_cursor");
//                            Log.e(TAG, mCursor + "");
                            if (mCursor == 0) {
                                // 表示没有更多数据了
                                mAdapter.setFooterInfo(getString(R.string.no_more_data));
                            } else {
                                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                            }
                        }
                        if (refresh) {
                            mAdapter.setData(friends);
                        } else {
                            mAdapter.addData(friends);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppToast.showToast(R.string.resolve_result_failure);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast(R.string.failed_to_get_friends_info);
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    private class MyFriendshipOp implements FriendshipOp.OnFriendshipOpResultListener {

        @Override
        public void onCreateSuccess(int position, String screenName) {
            AppToast.showToast(getString(R.string.pay_attention_prefix, screenName));
            mAdapter.createSuccess(position);
        }

        @Override
        public void onCreateFailure(int position, String screenName, String msg) {
            AppToast.showToast(getString(R.string.pay_attention_failure_prefix, msg));
        }

        @Override
        public void onDestroySuccess(int position, String screenName) {
            AppToast.showToast(getString(R.string.not_pay_attention_prefix, screenName));
            mAdapter.destroySuccess(position);
        }

        @Override
        public void onDestroyFailure(int position, String screenName, String msg) {
            AppToast.showToast(getString(R.string.not_pay_attention_failure_prefix, msg));
        }

        @Override
        public void onAddToGroupFailure(String msg) {
            AppToast.showToast(getString(R.string.add_to_group_failure_prefix, msg));
        }
    }
}
