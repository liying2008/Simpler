package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
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

public class WBStatusesActivity extends BaseActivity {
    private static final String TAG = "WBStatusesActivity";
    /** Intent传值的键（用户id） */
    private static final String INTENT_UID = "uid";
    /** Intent传值的键（用户友好名称） */
    private static final String INTENT_NAME = "name";

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private StatusListAdapter mAdapter;
    private static long mMaxId = 0;
    private StatusesAPI mStatusesAPI;
    private long mUid;
    private int mRefreshCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_statuses);
        ButterKnife.bind(this);

        // 每次刷新微博数
        mRefreshCount = BaseSettings.sSettings.refreshCount;
        Intent intent = getIntent();
        mUid = intent.getLongExtra(INTENT_UID, 0L);
        String name = intent.getStringExtra(INTENT_NAME);

        mTvTitle.setText(getString(R.string.title_statuses, name));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新微博，加载最新微博
                loadStatuses(0, true);
                mSwipeRefresh.setRefreshing(true);
            }
        });
        // 创建适配器
        mAdapter = new StatusListAdapter(WBStatusesActivity.this, null);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                if (mMaxId != 0) {
                    // 加载更早微博
                    mAdapter.setFooterInfo(getString(R.string.data_loading));
                    loadStatuses(mMaxId, false);
                }
            }
        });
        loadStatuses(0, true);  // 加载最新微博
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long uid, String name) {
        Intent intent = new Intent(context, WBStatusesActivity.class);
        intent.putExtra(INTENT_UID, uid);
        intent.putExtra(INTENT_NAME, name);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 加载用户微博
     */
    private void loadStatuses(long maxId, final boolean refresh) {
        if (mStatusesAPI == null) {
            mStatusesAPI = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }

        mStatusesAPI.userTimeline(mUid, 0, maxId, mRefreshCount, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                Log.d(TAG, s);
                if (!TextUtils.isEmpty(s)) {
                    formatStatusStr(s, refresh);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code");
                    if (errorCode == 10002) {
                        AppToast.showToast("服务暂不可用");
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                AppToast.showToast("暂无法获取微博信息");
            }
        });
    }

    /**
     * 格式化微博JSON字符串
     *
     * @param s       json字符串
     * @param refresh 是否刷新微博
     */
    private void formatStatusStr(String s, boolean refresh) {
        mSwipeRefresh.setRefreshing(false);
        JSONObject statuses = null;
        List<Status> statusList = new ArrayList<>(mRefreshCount);
        try {
            statuses = new JSONObject(s);
            JSONArray jsonArray = statuses.getJSONArray("statuses");
            if (!jsonArray.isNull(0)) {
                // 表示有数据
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject o = (JSONObject) jsonArray.get(i);
                    Status status = Status.parse(o);
                    statusList.add(status);
                }
                if (refresh) {
                    mAdapter.setStatuses(statusList);
                    mSwipeRefresh.getLayoutManager().scrollToPosition(0);
                } else {
                    mAdapter.addStatuses(statusList);
                }
                // 更新mMaxId
                mMaxId = statusList.get(statusList.size() - 1).id - 1;
                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
            } else {
                // 表示没有更多数据了
                mMaxId = 0;
                mAdapter.setFooterInfo(getString(R.string.no_more_data));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // 重新加载微博
            loadStatuses(mMaxId, true);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(WBStatusesActivity.class);
        super.onDestroy();
    }
}
