package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.WBCommentActivity;
import cc.duduhuo.simpler.activity.WBStatusDetailActivity;
import cc.duduhuo.simpler.adapter.common.StatusDataSetter;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.util.UserVerify;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/24 21:45
 * 版本：1.0
 * 描述：收到的评论、@我的评论和我发出的评论列表适配器
 * 备注：
 * =======================================================
 */
public class MentionCmtsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<Comment> mComments = new ArrayList<>(1);
    private String mFooterInfo = "";
    private StatusDataSetter mDataSetter;
    /** 当前时间（毫秒） */
    private static long sNow = 0;

    public MentionCmtsAdapter(Activity activity) {
        this.mActivity = activity;
        mDataSetter = new StatusDataSetter(activity, null);
        sNow = System.currentTimeMillis();
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mComments.size());
    }

    public void setComments(List<Comment> comments) {
        if (comments != null) {
            mComments.clear();
            mComments.addAll(comments);
            notifyDataSetChanged();
        }
    }

    public void addComments(List<Comment> comments) {
        if (comments != null) {
            int start = mComments.size();
            mComments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_mention_cmt, parent, false);
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
            final Comment comment = mComments.get(position);
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            if (comment.user == null) {
                // 评论被删除
                itemHolder.tvGone.setVisibility(View.VISIBLE);
                itemHolder.llComment.setVisibility(View.GONE);
                mDataSetter.formatContent(true, comment.id, comment.text, itemHolder.tvGone);
            } else {
                itemHolder.tvGone.setVisibility(View.GONE);
                itemHolder.llComment.setVisibility(View.VISIBLE);
                itemHolder.llReplied.setVisibility(View.VISIBLE);
                itemHolder.llRepliedGone.setVisibility(View.GONE);
                // 头像
                Glide.with(mActivity).load(comment.user.avatar_large).into(itemHolder.civHead);
                // 用户认证
                UserVerify.verify(comment.user, itemHolder.ivAvatarVip, null);
                String username = comment.user.screen_name;
                if (!TextUtils.isEmpty(comment.user.remark)) {
                    username = comment.user.remark;
                }
                itemHolder.tvUser.setText(username);
                itemHolder.tvSource.setText(Html.fromHtml(comment.source).toString());

                itemHolder.tvCreatedAt.setText(NumberFormatter.dateTransfer(sNow, comment.created_at));
                // 处理评论正文
                mDataSetter.formatContent(true, comment.id, comment.text, itemHolder.tvText);

                if (comment.reply_comment != null) {
                    // 被回复的原评论
                    Comment replyComment = comment.reply_comment;
                    if (replyComment.user == null) {
                        // 原评论被删除
                        itemHolder.llReplied.setVisibility(View.GONE);
                        itemHolder.llRepliedGone.setVisibility(View.VISIBLE);
                        // 处理原评论正文
                        mDataSetter.formatContent(true, replyComment.id, replyComment.text, itemHolder.tvGoneInfo);
                    } else {
                        itemHolder.tvRepliedUser.setText("@" + replyComment.user.screen_name);
                        // 处理原评论正文
                        mDataSetter.formatContent(true, replyComment.id, replyComment.text, itemHolder.tvRepliedText);
                    }
                } else {
                    // 被回复的原微博
                    Status replyStatus = comment.status;
                    if (replyStatus.user == null) {
                        // 原微博被删除
                        itemHolder.llReplied.setVisibility(View.GONE);
                        itemHolder.llRepliedGone.setVisibility(View.VISIBLE);
                        // 处理原微博正文
                        mDataSetter.formatContent(false, replyStatus.id, replyStatus.text, itemHolder.tvGoneInfo);
                    } else {
                        itemHolder.tvRepliedUser.setText("@" + replyStatus.user.screen_name);
                        // 处理原微博正文
                        mDataSetter.formatContent(false, replyStatus.id, replyStatus.text, itemHolder.tvRepliedText);
                    }
                }
                if (comment.status != null) {
                    itemHolder.tvReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 回复评论
                            mActivity.startActivity(WBCommentActivity.newIntent(mActivity,
                                    comment.status.id, comment.id, comment.user.screen_name, comment.text));
                        }
                    });

                    if (comment.status.user != null) {
                        itemHolder.llComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 进入原微博
                                Intent intent = WBStatusDetailActivity.newIntent(mActivity,
                                        comment.status.id, itemHolder.getAdapterPosition(), comment.status.user.screen_name,
                                        comment.status.text, false);
                                mActivity.startActivity(intent);
                            }
                        });
                    }
                }
            }
        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mComments.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvGone)
        TextView tvGone;
        @BindView(R.id.llComment)
        LinearLayout llComment;
        @BindView(R.id.civHead)
        ImageView civHead;
        @BindView(R.id.tvReply)
        TextView tvReply;
        @BindView(R.id.tvUser)
        TextView tvUser;
        @BindView(R.id.tvSource)
        TextView tvSource;
        @BindView(R.id.tvCreatedAt)
        TextView tvCreatedAt;
        @BindView(R.id.ivAvatarVip)
        ImageView ivAvatarVip;
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.llReplied)
        LinearLayout llReplied;
        @BindView(R.id.tvRepliedUser)
        TextView tvRepliedUser;
        @BindView(R.id.tvRepliedText)
        TextView tvRepliedText;
        @BindView(R.id.llRepliedGone)
        LinearLayout llRepliedGone;
        @BindView(R.id.tvGoneInfo)
        TextView tvGoneInfo;

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
