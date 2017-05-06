package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.SearchTopicsActivity;
import cc.duduhuo.simpler.bean.Topic;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/18 20:07
 * 版本：1.0
 * 描述：话题列表适配器
 * 备注：
 * =======================================================
 */
public class TopicsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<Topic> mTopics = new ArrayList<>(1);
    private String mFooterInfo = "";

    public TopicsAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void setTopics(List<Topic> topics) {
        if (topics != null) {
            this.mTopics.clear();
            this.mTopics.addAll(topics);
            notifyDataSetChanged();
        }
    }

    public void addTopics(List<Topic> topics) {
        if (topics != null) {
            int start = this.mTopics.size();
            this.mTopics.addAll(topics);
            notifyItemRangeInserted(start, topics.size());
        }
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mTopics.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_hot_topic, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            final Topic topic = mTopics.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.mTvTopic.setText(topic.topic);
            itemHolder.mTvDesc1.setText(topic.desc1);
            itemHolder.mTvDesc2.setText(topic.desc2);
            Glide.with(mActivity).load(topic.picUrl).into(itemHolder.mIvPic);
            itemHolder.mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startActivity(SearchTopicsActivity.newIntent(mActivity, topic.name));
                }
            });
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        if (mTopics == null) {
            return 0;
        }
        return mTopics.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rlItem)
        RelativeLayout mRlItem;
        @BindView(R.id.ivPic)
        ImageView mIvPic;
        @BindView(R.id.tvTopic)
        TextView mTvTopic;
        @BindView(R.id.tvDesc1)
        TextView mTvDesc1;
        @BindView(R.id.tvDesc2)
        TextView mTvDesc2;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFooter)
        TextView mTvFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
