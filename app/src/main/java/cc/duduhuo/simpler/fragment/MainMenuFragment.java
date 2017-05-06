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

public class MainMenuFragment extends BaseDialogFragment {
    /** 菜单功能项列表 */
    @BindView(R.id.rvMenuList)
    RecyclerView mRvMenuList;
    /** 主菜单列表适配器 */
    private MainMenuAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<MenuItem> mMenuItems = new ArrayList<>();

    private static WeakReference<MainMenuFragment> mInstance;
    private GroupChangeListener mListener;
    private GroupAPI mGApi;

    public MainMenuFragment() {
    }

    public void setGroupChangeListener(GroupChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 调用BaseDialogFragment中的设置
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        ButterKnife.bind(this, view);
        mInstance = new WeakReference<MainMenuFragment>(this);

        mLayoutManager = new LinearLayoutManager(mActivity);
        mRvMenuList.setLayoutManager(mLayoutManager);

        mMenuItems.clear();
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "全部微博", "", R.drawable.ic_option_list_timeline, MenuItem.MENU_ID_ALL_STATUS));
        mMenuItems.add(new MenuItem("功能列表"));
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "热门", "", R.drawable.ic_option_list_hot, MenuItem.MENU_ID_HOT));
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "消息", "", R.drawable.ic_option_list_message, MenuItem.MENU_ID_MESSAGE));
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "搜索", "", R.drawable.ic_option_list_search, MenuItem.MENU_ID_SEARCH));
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "我的收藏", "", R.drawable.ic_option_list_star, MenuItem.MENU_ID_FAVORITE));
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "草稿箱", "", R.drawable.ic_option_list_draft, MenuItem.MENU_ID_DRAFT));
        mMenuItems.add(new MenuItem("分组列表"));
        // 加载分组
        loadGroups();
        return view;
    }

    /**
     * 加载分组
     */
    private void loadGroups() {
        mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, "朋友圈", "", R.drawable.ic_option_list_group,
                MenuItem.MENU_ID_FRIEND_STATUS));
        if (BaseConfig.sGroups != null && !BaseConfig.sGroups.isEmpty()) {
            for (Group group : BaseConfig.sGroups) {
                mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, group.name, group.idStr,
                        R.drawable.ic_option_list_group, MenuItem.MENU_ID_GROUP));
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
                            mMenuItems.add(new MenuItem(MenuItem.TYPE_MENU, group.name, group.idStr,
                                    R.drawable.ic_option_list_group, MenuItem.MENU_ID_GROUP));
                        }
                        // 使用获取到的数据实例化适配器
                        mAdapter = new MainMenuAdapter(MainMenuFragment.this, mMenuItems, mListener);
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
     * 得到MainMenuFragment的实例
     *
     * @return MainMenuFragment的实例
     */
    public static MainMenuFragment getInstance() {
        return mInstance.get();
    }

    @OnClick(R.id.tvCancel)
    void close() {
        this.dismiss();
    }
}
