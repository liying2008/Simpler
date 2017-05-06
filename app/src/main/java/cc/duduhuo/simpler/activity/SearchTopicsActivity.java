package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.SearchAPI;
import com.sina.weibo.sdk.openapi.models.Status;

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
import cc.duduhuo.simpler.adapter.StatusListAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class SearchTopicsActivity extends BaseActivity {
    private static final String TAG = "SearchTopicsActivity";
    /** Intent传值的键（搜索关键字） */
    private static final String INTENT_KEYWORD = "keyword";
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private StatusListAdapter mAdapter;
    private int mPage = 1;
    private int mRefreshCount;
    private String mKeyword;
    private SearchAPI mSApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_topics);
        ButterKnife.bind(this);

        // 每次刷新微博数
        mRefreshCount = BaseSettings.sSettings.refreshCount;
        Intent intent = getIntent();
        mKeyword = intent.getStringExtra(INTENT_KEYWORD);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新微博，加载最新微博
                loadStatuses(1, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        // 创建适配器
        mAdapter = new StatusListAdapter(this, null);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mPage != 0) {
                    // 加载更早微博
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadStatuses(mPage, false);
                }
            }
        });
        loadStatuses(1, true);  // 加载最新微博
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, String keyword) {
        Intent intent = new Intent(context, SearchTopicsActivity.class);
        intent.putExtra(INTENT_KEYWORD, keyword);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 加载用户微博
     */
    private void loadStatuses(int page, final boolean refresh) {
        if (mSApi == null) {
            mSApi = new SearchAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mSApi.topics(mKeyword, mRefreshCount, page, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        List<Status> statuses = new ArrayList<Status>(mRefreshCount);
                        JSONObject obj = new JSONObject(s);
                        JSONArray array = obj.optJSONArray("statuses");
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                statuses.add(Status.parse(array.getJSONObject(i)));
                            }
                        } else {
                            AppToast.showToast(R.string.search_no_result);
                            return;
                        }
                        if (refresh) {
                            mAdapter.setStatuses(statuses);
                            mSwipeRefresh.getLayoutManager().scrollToPosition(0);
                        } else {
                            mAdapter.addStatuses(statuses);
                        }
                        // 更新mPage
                        long totalNumber = obj.optLong("total_number");
                        long totalPage = (long) Math.ceil((double) totalNumber / mRefreshCount);
//                        Log.d(TAG, totalNumber + ", " + totalPage);
                        if (mPage >= totalPage) {
                            // 表示没有更多数据了
                            mPage = 0;
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        } else {
                            // 更新mPage
                            mPage++;
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
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
                AppToast.showToast(R.string.search_failure);
            }
        });
    }

}
