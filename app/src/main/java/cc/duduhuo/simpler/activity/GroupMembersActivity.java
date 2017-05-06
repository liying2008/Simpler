package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.UserList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.GroupMembersAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 15:47
 * 版本：1.0
 * 描述：分组好友界面
 * 备注：
 * =======================================================
 */
public class GroupMembersActivity extends BaseActivity {
    /** Intent传值的键（分组id） */
    private static final String INTENT_GID = "gid";
    /** Intent传值的键（分组名称） */
    private static final String INTENT_GROUP_NAME = "group_name";
    /** Intent传值的键（该分组在RecyclerView中的位置） */
    public static final String INTENT_POSITION = "position";
    /** Intent传值的键（删除的分组好友个数） */
    public static final String INTENT_MEMBER_DEL = "member_del";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvMembers)
    RecyclerView mRvMembers;

    private GroupMembersAdapter mAdapter;
    private long mGid;
    /** 当前查询结果游标 */
    private int mCursor = 0;
    private GroupAPI mGApi;

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            queryMembers(mGid, 0, true);
            mSwipeRefresh.setRefreshing(true);
        }
    };
    private DSwipeRefresh.OnLoadingListener loadingListener = new DSwipeRefresh.OnLoadingListener() {
        @Override
        public void onLoading() {
            if (mCursor > 0) {
                mAdapter.setFooterInfo(getString(R.string.data_loading));
                queryMembers(mGid, mCursor, false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mGid = intent.getLongExtra(INTENT_GID, 0L);
        int position = intent.getIntExtra(INTENT_POSITION, 0);
        String groupName = intent.getStringExtra(INTENT_GROUP_NAME);
        mTvTitle.setText(groupName);
        mAdapter = new GroupMembersAdapter(this, mGid, position);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvMembers, mAdapter);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mSwipeRefresh.setOnLoadingListener(loadingListener);

        queryMembers(mGid, 0, true);  // 查询分组好友
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context, long gid, String groupName, int position) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra(INTENT_GID, gid);
        intent.putExtra(INTENT_GROUP_NAME, groupName);
        intent.putExtra(INTENT_POSITION, position);
        return intent;
    }

    /**
     * 查询分组成员
     *
     * @param gid     分组id
     * @param cursor  游标
     * @param refresh 是否刷新列表
     */
    private void queryMembers(long gid, final int cursor, final boolean refresh) {
        if (mGApi == null) {
            mGApi = new GroupAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.members(gid, 50, cursor, new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                UserList userList = UserList.parse(s);
                if (userList != null && userList.users != null) {
                    if (refresh) {
                        mAdapter.setData(userList.users);
                    } else {
                        mAdapter.addData(userList.users);
                    }
                    mCursor = userList.next_cursor;
                    if (mCursor > 0) {
                        mAdapter.setFooterInfo(getString(R.string.pull_up_to_load_more));
                    } else {
                        mAdapter.setFooterInfo(getString(R.string.no_more_data));
                    }
                } else {
                    AppToast.showToast("该分组下没有好友");
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取分组好友失败");
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

}
