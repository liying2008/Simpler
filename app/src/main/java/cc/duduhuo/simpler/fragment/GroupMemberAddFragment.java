package cc.duduhuo.simpler.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 18:57
 * 版本：1.0
 * 描述：将关注的人添加到指定分组
 * 备注：
 * =======================================================
 */
public class GroupMemberAddFragment extends BaseDialogFragment {
    @BindView(R.id.rvGroups)
    RecyclerView mRvGroups;

    private GroupMemberAddAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Long> mGroupId;
    private OnAddGroupsListener mListener;

    public GroupMemberAddFragment() {

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
            mAdapter.setGroups(BaseConfig.sGroups);
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
        }
    }

    @OnClick(R.id.tvCancel)
    void close() {
        this.dismiss();
    }

    @OnClick(R.id.tvConfirm)
    void confirm() {
        AppToast.showToast(R.string.in_operation);
        if (mListener != null) {
            mListener.onAddGroups(mGroupId);
        }
        this.dismiss();
    }

    public void setOnAddGroupsListener(OnAddGroupsListener listener) {
        this.mListener = listener;
    }

    public interface OnAddGroupsListener {
        void onAddGroups(List<Long> groupIds);
    }
}
