package cc.duduhuo.simpler.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.bean.Draft;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/23 14:59
 * 版本：1.0
 * 描述：草稿列表适配器
 * 备注：
 * =======================================================
 */
public class DraftsAdapter extends RecyclerView.Adapter<DraftsAdapter.ViewHolder> {
    private List<Draft> mDrafts;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    public DraftsAdapter() {
    }

    public void setDrafts(List<Draft> drafts) {
        this.mDrafts = drafts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_draft, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Draft draft = mDrafts.get(position);
        holder.mTvContent.setText(draft.content);
        if (draft.photoList.isEmpty()) {
            holder.mIvPicExist.setVisibility(View.GONE);
        } else {
            holder.mIvPicExist.setVisibility(View.VISIBLE);
        }
        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        holder.mLlItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    mLongClickListener.onItemLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDrafts == null) {
            return 0;
        }
        return mDrafts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llItem)
        LinearLayout mLlItem;
        @BindView(R.id.ivPicExist)
        ImageView mIvPicExist;
        @BindView(R.id.tvContent)
        TextView mTvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
