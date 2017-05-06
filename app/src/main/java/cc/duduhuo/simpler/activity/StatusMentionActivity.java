package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

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
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.RemindOp;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/25 20:52
 * 版本：1.0
 * 描述：@我的微博界面
 * 备注：
 * =======================================================
 */
public class StatusMentionActivity extends BaseActivity {
    private static final String TAG = "StatusMentionActivity";
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvStatuses)
    RecyclerView mRvStatuses;
    private StatusListAdapter mAdapter;
    private long mCursor = 0;
    private StatusesAPI mStatusesAPI;
    private boolean mIsResultOk = false;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // 刷新微博，加载最新微博
            loadStatuses(0, true);
            mSwipeRefresh.setRefreshing(true);
        }
    };

    private DSwipeRefresh.OnLoadingListener mLoadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mCursor > 0) {
                // 加载更早微博
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                loadStatuses(mCursor, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_mention);
        ButterKnife.bind(this);

        // 创建适配器
        mAdapter = new StatusListAdapter(this, null);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvStatuses, mAdapter);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mSwipeRefresh.setOnLoadingListener(mLoadingListener);
        loadStatuses(0, true);  // 加载最新微博
        mSwipeRefresh.setRefreshing(true);

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, StatusMentionActivity.class);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 加载用户微博
     */
    private void loadStatuses(long cursor, final boolean refresh) {
        if (mStatusesAPI == null) {
            mStatusesAPI = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }

        mStatusesAPI.mentions(0, cursor, 20, 1, 0, 0, 0, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!mIsResultOk) {
                    mIsResultOk = true;
                    setResult(RESULT_OK);
                    RemindOp remindOp = new RemindOp(StatusMentionActivity.this);
                    remindOp.onSetCount("mention_status");
                }

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
        List<Status> statusList = new ArrayList<>(20);
        try {
            statuses = new JSONObject(s);
            JSONArray jsonArray = statuses.optJSONArray("statuses");
            if (jsonArray != null && jsonArray.length() > 0) {
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
                // 更新mCursor
                mCursor = statuses.optLong("next_cursor", 0L);
                if (mCursor > 0) {
                    mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                } else {
                    // 表示没有更多数据了
                    mAdapter.setFooterInfo(getString(R.string.no_more_data));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppToast.showToast(R.string.resolve_result_failure);
        }
    }

}
