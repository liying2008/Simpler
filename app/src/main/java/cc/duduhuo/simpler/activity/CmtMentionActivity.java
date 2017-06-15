package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.MentionCmtsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.RemindOp;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/24 20:16
 * 版本：1.0
 * 描述：提到我或我发出的评论列表界面
 * 备注：
 * =======================================================
 */
public class CmtMentionActivity extends BaseActivity {
    private static final String TAG = "CmtMentionActivity";
    /** Intent传值的键（评论类型） */
    private static final String INTENT_TYPE = "type";
    /** 收到的评论 */
    public static final int TYPE_TO_ME = 0;
    /** 我发出的评论 */
    public static final int TYPE_BY_ME = 1;
    /** @ 到我的评论 */
    public static final int TYPE_MENTION = 2;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvCmts)
    RecyclerView mRvCmts;
    private MentionCmtsAdapter mAdapter;
    /** 当前评论类型 */
    private int mType;
    /** 当前结果游标 */
    private long mCursor;
    private boolean mIsResultOk = false;

    private CommentsAPI mCApi;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (mType == TYPE_TO_ME) {
                queryToMe(0, true);
            } else if (mType == TYPE_BY_ME) {
                queryByMe(0, true);
            } else if (mType == TYPE_MENTION) {
                queryMention(0, true);
            }
            mSwipeRefresh.setRefreshing(true);
        }
    };
    private DSwipeRefresh.OnLoadingListener mLoadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mCursor > 0) {
                if (mType == TYPE_TO_ME) {
                    queryToMe(mCursor, false);
                } else if (mType == TYPE_BY_ME) {
                    queryByMe(mCursor, false);
                } else if (mType == TYPE_MENTION) {
                    queryMention(mCursor, false);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmt_mention);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mType = intent.getIntExtra(INTENT_TYPE, TYPE_TO_ME);
        if (mType == TYPE_TO_ME) {
            mTvTitle.setText("收到的评论");
        } else if (mType == TYPE_BY_ME) {
            mTvTitle.setText("发出的评论");
        } else if (mType == TYPE_MENTION) {
            mTvTitle.setText("提到我的评论");
        }

        mAdapter = new MentionCmtsAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvCmts, mAdapter);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);
        mSwipeRefresh.setOnLoadingListener(mLoadingListener);
        if (mType == TYPE_TO_ME) {
            queryToMe(0, true);
        } else if (mType == TYPE_BY_ME) {
            queryByMe(0, true);
        } else if (mType == TYPE_MENTION) {
            queryMention(0, true);
        }
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, int type) {
        Intent intent = new Intent(context, CmtMentionActivity.class);
        intent.putExtra(INTENT_TYPE, type);
        return intent;
    }

    /**
     * 收到的评论
     *
     * @param cursor
     * @param refresh
     */
    private void queryToMe(long cursor, final boolean refresh) {
        if (mCApi == null) {
            mCApi = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mCApi.toME(0, cursor, 20, 1, 0, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                formatComments(s, refresh);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取数据失败");
            }
        });
    }

    /**
     * 发出的评论
     *
     * @param cursor
     * @param refresh
     */
    private void queryByMe(long cursor, final boolean refresh) {
        if (mCApi == null) {
            mCApi = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mCApi.byME(0, cursor, 20, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                formatComments(s, refresh);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取数据失败");
            }
        });
    }

    /**
     * 提到我
     *
     * @param cursor
     * @param refresh
     */
    private void queryMention(long cursor, final boolean refresh) {
        if (mCApi == null) {
            mCApi = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mCApi.mentions(0, cursor, 20, 1, 0, 0, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                formatComments(s, refresh);
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取数据失败");
            }
        });
    }

    /**
     * 格式化评论
     *
     * @param s
     * @param refresh 是否刷新
     */
    private void formatComments(String s, boolean refresh) {
        if (!mIsResultOk) {
            mIsResultOk = true;     // 已经查看了数据，返回之后，清除badge
            setResult(RESULT_OK);
            RemindOp remindOp = new RemindOp(this);
            if (mType == TYPE_TO_ME) {
                remindOp.onSetCount("cmt");
            } else if (mType == TYPE_MENTION) {
                remindOp.onSetCount("mention_cmt");
            }
        }

        CommentList commentList = CommentList.parse(s);
        if (commentList == null || commentList.commentList == null || commentList.commentList.isEmpty()) {
            // 没有数据
            mAdapter.setFooterInfo(getString(R.string.no_comment_yet));
            return;
        }

        if (refresh) {
            mAdapter.setComments(commentList.commentList);
            mSwipeRefresh.getLayoutManager().scrollToPosition(0);
        } else {
            mAdapter.addComments(commentList.commentList);
        }
        mCursor = commentList.next_cursor;
        if (mCursor > 0) {
            mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
        } else {
            mAdapter.setFooterInfo(getString(R.string.no_more_data));
        }
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(CmtMentionActivity.class);
        super.onDestroy();
    }
}
