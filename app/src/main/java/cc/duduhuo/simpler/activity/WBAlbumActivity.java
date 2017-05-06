package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.StatusList;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.AlbumsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class WBAlbumActivity extends BaseActivity {
    /** Intent传值的键（用户id） */
    private static final String INTENT_UID = "uid";
    /** Intent传值的键（用户友好名称） */
    private static final String INTENT_NAME = "name";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvPhotos)
    RecyclerView mRvPhotos;

    private AlbumsAdapter mAdapter;
    private long mMaxId = 0;
    private StatusesAPI mSApi;
    private long mUid;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            queryPhotos(0, true);
            mSwipeRefresh.setRefreshing(true);
        }
    };

    private DSwipeRefresh.OnLoadingListener mLoadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mMaxId > 0) {
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                queryPhotos(mMaxId, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_album);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUid = intent.getLongExtra(INTENT_UID, 0L);
        String name = intent.getStringExtra(INTENT_NAME);
        mTvTitle.setText(getString(R.string.title_album, name));

        mAdapter = new AlbumsAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvPhotos, mAdapter, 3);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mSwipeRefresh.setOnLoadingListener(mLoadingListener);

        queryPhotos(0, true);
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long uid, String name) {
        Intent intent = new Intent(context, WBAlbumActivity.class);
        intent.putExtra(INTENT_UID, uid);
        intent.putExtra(INTENT_NAME, name);
        return intent;
    }

    private void queryPhotos(long maxId, final boolean refresh) {
        if (mSApi == null) {
            mSApi = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }

        mSApi.userTimeline(mUid, 0, maxId, 20, 1, false, StatusesAPI.FEATURE_PICTURE, true, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                StatusList statusList = StatusList.parse(s);
                if (statusList == null || statusList.statusList == null || statusList.statusList.isEmpty()) {
                    mMaxId = 0;
                    mAdapter.setFooterInfo(getString(R.string.no_more_data));
                    return;
                }
                List<String> once = new ArrayList<>();
                for (int i = 0; i < statusList.statusList.size(); i++) {
                    ArrayList<String> picUrls = statusList.statusList.get(i).pic_urls;
                    if (picUrls != null && !picUrls.isEmpty()) {
                        once.addAll(picUrls);
                    }
                }

                if (refresh) {
                    mAdapter.setAlbums(once);
                } else {
                    mAdapter.addAlbums(once);
                }
                mMaxId = statusList.statusList.get(statusList.statusList.size() - 1).id - 1;
                mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("暂无法查看个人相册");
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

}
