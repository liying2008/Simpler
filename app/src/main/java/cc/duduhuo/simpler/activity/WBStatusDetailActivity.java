package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.CommentListAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.FavoriteOp;
import cc.duduhuo.simpler.listener.impl.ToComment;
import cc.duduhuo.simpler.listener.impl.ToRepost;
import cc.duduhuo.simpler.view.DSwipeRefresh;

public class WBStatusDetailActivity extends BaseActivity {
    private static final String TAG = "WBStatusDetailActivity";
    /** ntent传值的键（微博id） */
    private static final String INTENT_SID = "sid";
    private static final String INTENT_POSITION = "position";
    private static final String INTENT_SCREEN_NAME= "screen_ame";
    private static final String INTENT_STATUS= "status";
    private static final String INTENT_RETWEETED= "retweeted";
    @BindView(R.id.tvComment)
    TextView mTvComment;
    @BindView(R.id.tvRepost)
    TextView mTvRepost;
    @BindView(R.id.cbFavorite)
    CheckBox mCbFavorite;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvComments)
    RecyclerView mRvComments;
    private CommentListAdapter mAdapter;
    // 微博Id
    private long mId;
    private String mScreenName; // 当前微博发布者昵称
    private String mStatus; // 当前微博文本
    private static long mMaxId = 0;
    private int mPosition;
    private boolean mRetweeted;

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadComments(0, true, true);
        }
    };
    private DSwipeRefresh.OnLoadingListener loadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mMaxId != 0) {
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                loadComments(mMaxId, false, false);
            }
        }
    };
    // 收藏按钮
    private View.OnClickListener favoriteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFavoriteOp == null) {
                mFavoriteOp = new FavoriteOp(WBStatusDetailActivity.this);
                mFavoriteOp.setOnFavoriteOpResultListener(new FavoriteOp.OnFavoriteOpResultListener() {
                    @Override
                    public void onCreateSuccess() {
                        AppToast.showToast(R.string.marked);
                    }

                    @Override
                    public void onCreateFailure(String msg) {
                        AppToast.showToast(getString(R.string.mark_failure_prefix, msg));
                        mCbFavorite.setChecked(false);
                    }

                    @Override
                    public void onDestroySuccess() {
                        AppToast.showToast(R.string.unmarked);
                    }

                    @Override
                    public void onDestroyFailure(String msg) {
                        AppToast.showToast(getString(R.string.unmark_failure_prefix, msg));
                        mCbFavorite.setChecked(true);
                    }
                });
            }
            boolean checked = mCbFavorite.isChecked();
            if (checked) {
                mFavoriteOp.onCreate(mId);
            } else {
                mFavoriteOp.onDestroy(mId);
            }
        }
    };
    private CommentsAPI mCAPI;
    private FavoriteOp mFavoriteOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_status_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = intent.getLongExtra(INTENT_SID, 0L);
        mPosition = intent.getIntExtra(INTENT_POSITION, 0);
        mScreenName = intent.getStringExtra(INTENT_SCREEN_NAME);
        mStatus = intent.getStringExtra(INTENT_STATUS);
        mRetweeted = intent.getBooleanExtra(INTENT_RETWEETED, false);

        mAdapter = new CommentListAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvComments, mAdapter);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mSwipeRefresh.setOnLoadingListener(loadingListener);
        mCbFavorite.setOnClickListener(favoriteOnClickListener);
        mTvComment.setOnClickListener(new ToComment(this, mId));
        mTvRepost.setOnClickListener(new ToRepost(this, mId, mScreenName, mStatus, mRetweeted));

        loadComments(0, true, true);
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long sid, int position, String screenName,
                                   String status, boolean retweeted) {
        Intent intent = new Intent(context, WBStatusDetailActivity.class);
        intent.putExtra(INTENT_SID, sid);
        intent.putExtra(INTENT_POSITION, position);
        intent.putExtra(INTENT_SCREEN_NAME, screenName);
        intent.putExtra(INTENT_STATUS, status);
        intent.putExtra(INTENT_RETWEETED, retweeted);
        return intent;
    }

    /**
     * 加载评论
     *
     * @param maxId           下次加载的maxId
     * @param refreshComments 是否刷新评论
     * @param refreshStatus   是否刷新微博
     */
    private void loadComments(long maxId, final boolean refreshComments, final boolean refreshStatus) {
        if (mCAPI == null) {
            mCAPI = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mCAPI.show(mId, 0, maxId, 30, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
//                Log.d(TAG, s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        // 有可能出现以下情况：
                        // {"comments":[],"hasvisible":false,"previous_cursor":0,"next_cursor":0,"total_number":0,"interval":0}
                        int totalNumber = jsonObject.getInt("total_number");
                        if (totalNumber == 0) {
                            // 没有评论
                            mAdapter.setFooterInfo(getString(R.string.no_comment_yet));
                            return;
                        }
                        if (refreshStatus) {
                            // 刷新微博
                            JSONObject statusObj = jsonObject.getJSONObject("status");
                            if (statusObj != null) {
                                Status status = Status.parse(statusObj);
                                mAdapter.setStatus(status);
                            }
                        }
                        mMaxId = jsonObject.getLong("max_id");
                        if (mMaxId == 0) {
                            // 表示没有更多数据了
                            mAdapter.setFooterInfo(getString(R.string.no_more_data));
                        } else {
                            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                        }
                        CommentList commentList = CommentList.parse(s);
                        if (commentList != null) {
                            if (refreshComments) {
                                // 刷新评论
                                if (commentList.commentList != null && commentList.commentList.size() > 0) {
                                    mAdapter.setComments(commentList.commentList);
                                    // 刷新后，滚动到第一条评论
                                    mSwipeRefresh.getLayoutManager().scrollToPosition(1);
                                }
                            } else {
                                if (commentList.commentList != null && commentList.commentList.size() > 0) {
                                    mAdapter.addComments(commentList.commentList);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
            }
        });
    }


    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @OnClick(R.id.tvRefresh)
    void refresh() {
        loadComments(0, true, false);
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(WBStatusDetailActivity.class);
        super.onDestroy();
    }
}
