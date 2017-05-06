package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.Group;
import com.sina.weibo.sdk.openapi.models.GroupList;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.GroupsAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.view.DSwipeRefresh;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 15:16
 * 版本：1.0
 * 描述：管理分组界面
 * 备注：
 * =======================================================
 */
public class GroupManagerActivity extends BaseActivity {
    public static final int REQUEST_GROUP_EDIT = 0;
    public static final int REQUEST_GROUP_ADD = 1;
    public static final int REQUEST_MEMBER_DEL = 2;
    @BindView(R.id.swipeRefresh)
    DSwipeRefresh mSwipeRefresh;
    @BindView(R.id.rvGroups)
    RecyclerView mRvGroups;

    private GroupAPI mGApi;
    private GroupsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manager);
        ButterKnife.bind(this);

        mAdapter = new GroupsAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvGroups, mAdapter);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryGroups();
            }
        });
        queryGroups();
        mSwipeRefresh.setRefreshing(true);
    }

    private void queryGroups() {
        if (mGApi == null) {
            mGApi = new GroupAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.groups(new RequestListener() {
            @Override
            public void onComplete(String s) {
                mSwipeRefresh.setRefreshing(false);
                // Log.d("Group", s);
                GroupList groupList = GroupList.parse(s);
                if (groupList != null && groupList.groupList != null) {
                    BaseConfig.sGroups = groupList.groupList;
                    mAdapter.setGroups(groupList.groupList);
                    mAdapter.setFooterInfo(getString(R.string.loaded_all));
                } else {
                    AppToast.showToast("获取分组失败");
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                mSwipeRefresh.setRefreshing(false);
                e.printStackTrace();
                AppToast.showToast("获取分组失败");
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, GroupManagerActivity.class);
        return intent;
    }

    /** 新增分组 */
    @OnClick(R.id.tvAdd)
    void addGroup() {
        startActivityForResult(GroupEditActivity.newIntent(this, GroupEditActivity.TYPE_ADD), REQUEST_GROUP_ADD);
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GROUP_EDIT) {
                int position = data.getIntExtra(GroupEditActivity.INTENT_POSITION, 0);
                String name = data.getStringExtra(GroupEditActivity.INTENT_NAME);
                String desc = data.getStringExtra(GroupEditActivity.INTENT_DESC);
                mAdapter.updateGroupItem(position, name, desc);
                BaseConfig.sGroups.get(position).name = name;
                BaseConfig.sGroups.get(position).description = desc;
            } else if (requestCode == REQUEST_GROUP_ADD) {
                long listId = data.getLongExtra(GroupEditActivity.INTENT_GID, 0L);
                mGApi.showGroup(listId, new RequestListener() {
                    @Override
                    public void onComplete(String s) {
                        try {
                            JSONObject obj = new JSONObject(s);
                            Group group = Group.parse(obj);
                            mAdapter.addGroup(group);
                            BaseConfig.sGroups.add(group);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        e.printStackTrace();
                    }
                });
            } else if (requestCode == REQUEST_MEMBER_DEL) {
                int position = data.getIntExtra(GroupMembersActivity.INTENT_POSITION, 0);
                int count = data.getIntExtra(GroupMembersActivity.INTENT_MEMBER_DEL, 0);
                mAdapter.updateGroupItem(position, count);
            }
        }
    }
}
