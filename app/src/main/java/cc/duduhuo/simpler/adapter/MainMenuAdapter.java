package cc.duduhuo.simpler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseDialogFragment;
import cc.duduhuo.simpler.bean.MenuItem;
import cc.duduhuo.simpler.listener.GroupChangeListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/28 22:25
 * 版本：1.0
 * 描述：主菜单（MainMenuFragment）列表适配器
 * 备注：
 * =======================================================
 */
public class MainMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MenuItem> mMenuItems = new ArrayList<>();
    private BaseDialogFragment mFragment;
    private GroupChangeListener mListener;

    public MainMenuAdapter(BaseDialogFragment fragment, List<MenuItem> menuItems,
                           GroupChangeListener listener) {
        this.mFragment = fragment;
        this.mMenuItems.addAll(menuItems) ;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case MenuItem.TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_title, parent, false);
                holder = new TitleViewHolder(view);
                break;
            case MenuItem.TYPE_MENU:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_menu, parent, false);
                holder = new MenuViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MenuItem menuItem = mMenuItems.get(position);
        switch (getItemViewType(position)) {
            case MenuItem.TYPE_TITLE:
                ((TitleViewHolder) holder).mTvMenuTitle.setText(menuItem.name);
                break;
            case MenuItem.TYPE_MENU:
                MenuViewHolder menuHolder = (MenuViewHolder) holder;
                menuHolder.mTvMenuItem.setText(menuItem.name);
                if (menuItem.resId != -1) {
                    menuHolder.mIvMenuIcon.setVisibility(View.VISIBLE);
                    menuHolder.mIvMenuIcon.setImageResource(menuItem.resId);
                } else {
                    menuHolder.mIvMenuIcon.setVisibility(View.GONE);
                }
                menuHolder.mLlMenuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onGroupChange(menuItem.menuId, menuItem.groupId, menuItem.name);
                        }
                        // 关闭Fragment
                        mFragment.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMenuItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMenuItems.get(position).type;
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMenuTitle)
        TextView mTvMenuTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llMenuItem)
        LinearLayout mLlMenuItem;
        @BindView(R.id.tvMenuItem)
        TextView mTvMenuItem;
        @BindView(R.id.ivMenuIcon)
        ImageView mIvMenuIcon;

        public MenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
