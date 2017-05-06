package cc.duduhuo.simpler.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.Group;
import com.sina.weibo.sdk.openapi.models.GroupList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.MainMenuAdapter;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.base.BaseDialogFragment;
import cc.duduhuo.simpler.bean.MenuItem;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.GroupChangeListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/7 23:02
 * 版本：1.0
 * 描述：选择微博可见分组
 * 备注：
 * =======================================================
 */
public class GroupSelectFragment extends BaseDialogFragment {

    /** 菜单功能项列表 */
    @BindView(R.id.rvMenuList)
    RecyclerView mRvMenuList;
    /** 主菜单列表适配器 */
    private MainMenuAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<MenuItem> mMenuItems = new ArrayList<>();

    private static WeakReference<GroupSelectFragment> mInstance;
    private GroupChangeListener mListener;
    private GroupAPI mGApi;

    public GroupSelectFragment() {

    }

    public void setGroupChangeListener(GroupChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 调用BaseDialogFragment中的设置
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_group_select, container, false);
        ButterKnife.bind(this, view);
        mInstance = new WeakReference<GroupSelectFragment>(this);

        mMenuItems.clear();
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "所有人可见", "", -1, MenuItem.MENU_ID_ALL_VISIBLE));
        mMenuItems.add(new MenuItem("指定分组可见"));

        mLayoutManager = new LinearLayoutManager(mActivity);
        mRvMenuList.setLayoutManager(mLayoutManager);
        // 加载分组
        loadGroups();
        return view;
    }

    /**
     * 加载分组
     */
    private void loadGroups() {
        if (BaseConfig.sGroups != null && !BaseConfig.sGroups.isEmpty()) {
            for (Group group : BaseConfig.sGroups) {
                mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, group.name, group.idStr, -1, MenuItem.MENU_ID_GROUP));
            }
            // 使用获取到的数据实例化适配器
            mAdapter = new MainMenuAdapter(this, mMenuItems, mListener);
            mRvMenuList.setAdapter(mAdapter);
        } else {
            // 加载分组
            if (mGApi == null) {
                mGApi = new GroupAPI(mActivity, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
            }
            AppToast.showToast(R.string.querying_groups);
            mGApi.groups(new RequestListener() {
                @Override
                public void onComplete(String s) {
                    GroupList groupList = GroupList.parse(s);
                    if (groupList != null && groupList.groupList != null) {
                        BaseConfig.sGroups = groupList.groupList;
                        for (Group group : BaseConfig.sGroups) {
                            mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, group.name, group.idStr, -1, MenuItem.MENU_ID_GROUP));
                        }
                        // 使用获取到的数据实例化适配器
                        mAdapter = new MainMenuAdapter(GroupSelectFragment.this, mMenuItems, mListener);
                        mRvMenuList.setAdapter(mAdapter);
                    } else {
                        AppToast.showToast("获取分组失败");
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    e.printStackTrace();
                    AppToast.showToast("获取分组失败");
                }
            });
        }
    }

    /**
     * 得到GroupSelectFragment的实例
     *
     * @return GroupSelectFragment的实例
     */
    public static GroupSelectFragment getInstance() {
        return mInstance.get();
    }

    @OnClick(R.id.tvCancel)
    void close() {
        this.dismiss();
    }
}
