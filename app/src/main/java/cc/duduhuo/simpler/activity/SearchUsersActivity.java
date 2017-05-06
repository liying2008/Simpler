package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
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
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class SearchUsersActivity extends BaseActivity {
    /** Intent传值的键（搜索关键字） */
    private static final String INTENT_KEYWORD = "keyword";
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvUsers)
    RecyclerView mRvUsers;
    /** 用户列表适配器 */
    private UserListAdapter mAdapter;
    private String mKeyword;
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            searchUser(mKeyword);
            mSwipeRefresh.setRefreshing(true);
        }
    };
    private SearchAPI mSApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mKeyword = intent.getStringExtra(INTENT_KEYWORD);
        mAdapter = new UserListAdapter(this, false);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvUsers, mAdapter);
        mSwipeRefresh.setOnRefreshListener(refreshListener);

        searchUser(mKeyword);
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, String keyword) {
        Intent intent = new Intent(context, SearchUsersActivity.class);
        intent.putExtra(INTENT_KEYWORD, keyword);
        return intent;
    }

    /**
     * 搜索相关用户
     * @param keyword
     */
    private void searchUser(String keyword) {
        if (mSApi == null) {
            mSApi = new SearchAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mSApi.users(keyword, 50, 1, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        List<User> users = new ArrayList<User>(50);
                        JSONObject obj = new JSONObject(s);
                        JSONArray array = obj.optJSONArray("users");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                users.add(User.parse(array.getJSONObject(i)));
                            }
                        } else {
                            AppToast.showToast(R.string.search_no_result);
                            return;
                        }
                        mAdapter.setData(users);
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
                AppToast.showToast(R.string.search_failure);
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

}
