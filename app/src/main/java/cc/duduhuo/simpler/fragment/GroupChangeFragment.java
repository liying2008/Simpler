package cc.duduhuo.simpler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.Group;
import com.sina.weibo.sdk.openapi.models.GroupList;

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
import cc.duduhuo.simpler.adapter.GroupMemberAddAdapter;
import cc.duduhuo.simpler.base.BaseDialogFragment;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.impl.GroupMemberMgrOp;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 21:38
 * 版本：1.0
 * 描述：更改分组Fragment
 * 备注：
 * =======================================================
 */
public class GroupChangeFragment extends BaseDialogFragment {
    public static final String BUNDLE_UID = "uid";
    @BindView(R.id.rvGroups)
    RecyclerView mRvGroups;
    private GroupMemberAddAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Long> mGroupId;
    private GroupMemberMgrOp mGroupMemberMgrOp;
    private GroupAPI mGApi;
    private long mUid;
    private List<Group> mOldGroups = new ArrayList<>(1);

    public GroupChangeFragment() {
        mGroupMemberMgrOp = new GroupMemberMgrOp(getContext());
        mGroupMemberMgrOp.setOnGroupMemberAddListener(new MyGroupMemberAddListener());
        mGroupMemberMgrOp.setOnGroupMemberDestroyListener(new MyGroupMemberDestroyListener());
    }

    public static GroupChangeFragment newInstance(long uid) {
        GroupChangeFragment fragment = new GroupChangeFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUid = bundle.getLong(BUNDLE_UID, 0L);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 调用BaseDialogFragment中的设置
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_group_member_add, container, false);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(mActivity);
        mRvGroups.setLayoutManager(mLayoutManager);
        mAdapter = new GroupMemberAddAdapter();
        mRvGroups.setAdapter(mAdapter);
        mGroupId = new ArrayList<>();
        // 加载分组
        loadGroups();
        return view;
    }

    private void loadGroups() {
        if (BaseConfig.sGroups.size() > 0) {
            mAdapter.setOnSelectListener(new GroupMemberAddAdapter.OnSelectListener() {
                @Override
                public void onSelect(long gid, boolean checked) {
                    if (checked) {
                        mGroupId.add(gid);
                    } else {
                        mGroupId.remove(gid);
                    }
                }
            });
            if (mGApi == null) {
                mGApi = new GroupAPI(getContext(), SinaConsts.APP_KEY, BaseConfig.sAccessToken);
            }

            mGApi.listed(String.valueOf(mUid), new RequestListener() {
                @Override
                public void onComplete(String s) {
                    try {
                        JSONArray jsonArray = new JSONArray(s);
                        JSONObject object = jsonArray.optJSONObject(0);
                        if (object != null) {
                            GroupList groupList = GroupList.parse(object);
                            mOldGroups = groupList.groupList;
                            mAdapter.setGroups(BaseConfig.sGroups, groupList.groupList);
                        } else {
                            mAdapter.setGroups(BaseConfig.sGroups);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mAdapter.setGroups(BaseConfig.sGroups);
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    e.printStackTrace();
                    mAdapter.setGroups(BaseConfig.sGroups);
                }
            });
        }
    }

    @OnClick(R.id.tvCancel)
    void close() {
        this.dismiss();
    }

    @OnClick(R.id.tvConfirm)
    void confirm() {
        this.dismiss();
        // 旧分组是否为空
        boolean oldEmpty = false;
        if (mGroupId.isEmpty()) {
            return;
        }

        if (mOldGroups.isEmpty()) {
            oldEmpty = true;
        }
        // 用来保存新、旧分组重复的位置
        List<Integer> oldIndex = new ArrayList<>(mOldGroups.size());
        List<Integer> newIndex = new ArrayList<>(mGroupId.size());

        // 剔除新分组和旧分组重复的分组
        if (!oldEmpty) {
            for (int i = 0; i < mGroupId.size(); i++) {
                for (int j = 0; j < mOldGroups.size(); j++) {
                    if (mOldGroups.get(j).id == mGroupId.get(i)) {
                        oldIndex.add(j);
                        newIndex.add(i);
                    }
                }
            }
        }
        if (mOldGroups.size() != oldIndex.size() || mGroupId.size() != newIndex.size()) {
            // 需要操作
            AppToast.showToast(R.string.in_operation);
        } else {
            return;
        }

        if (!mOldGroups.isEmpty()) {
            // 删除取消的（旧）分组
            for (int i = 0; i < mOldGroups.size(); i++) {
                if (oldIndex.contains(i)) {
                    continue;
                }
                mGroupMemberMgrOp.onDestroy(mUid, mOldGroups.get(i).id, 0);
            }
            // 添加新增的分组
            for (int i = 0; i < mGroupId.size(); i++) {
                if (newIndex.contains(i)) {
                    continue;
                }
                mGroupMemberMgrOp.onAdd(mUid, mGroupId.get(i));
            }
        }
    }

    private class MyGroupMemberAddListener implements GroupMemberMgrOp.OnGroupMemberAddListener {
        private int mCount = 0;

        @Override
        public void onSuccess(String groupName) {
            AppToast.showToast("已添加到 [" + groupName + "] 分组");
        }

        @Override
        public void onFailure() {
            mCount++;
            AppToast.showToast(mCount + "个分组添加失败");
        }
    }

    private class MyGroupMemberDestroyListener implements GroupMemberMgrOp.OnGroupMemberDestroyListener {
        private int mCount = 0;

        @Override
        public void onSuccess(int position, String groupName) {
            AppToast.showToast("已从 [" + groupName + "] 分组中移出");
        }

        @Override
        public void onFailure() {
            mCount++;
            AppToast.showToast("从" + mCount + "个分组中移出失败");
        }
    }
}
