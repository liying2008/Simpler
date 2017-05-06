package cc.duduhuo.simpler.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.WBStatusDetailActivity;
import cc.duduhuo.simpler.adapter.common.StatusDataSetter;
import cc.duduhuo.simpler.adapter.common.StatusItemViewHolder;
import cc.duduhuo.simpler.bean.weibo.Card;
import cc.duduhuo.simpler.bean.weibo.CardGroup;
import cc.duduhuo.simpler.bean.weibo.MBlog;
import cc.duduhuo.simpler.config.AttitudeContainer;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.PicQuality;
import cc.duduhuo.simpler.listener.impl.ToComment;
import cc.duduhuo.simpler.listener.impl.ToRepost;
import cc.duduhuo.simpler.listener.impl.ViewUserOp;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.util.UserVerify;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/28 15:31
 * 版本：1.0
 * 描述：H5接口微博列表适配器
 * 备注：
 * =======================================================
 */
public class MBlogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MBlogListAdapter";
    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOT = 0x0001;
    private Activity mActivity;
    private List<CardGroup> mCardGroups = new ArrayList<>(1);
    private List<Card> mCards = new ArrayList<>(1);
    private String mFooterInfo = "";
    private StatusDataSetter mDataSetter;
    /** 是否作为热门微博适配器 */
    private boolean mHot;

    public MBlogListAdapter(Activity activity, boolean hot) {
        this.mActivity = activity;
        this.mHot = hot;
        mDataSetter = new StatusDataSetter(mActivity, null);
    }

    /**
     * 设置footerView信息
     *
     * @param footerInfo footerView信息
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(mCardGroups.size());
    }

    /**
     * 加载更多微博（搜索微博）
     *
     * @param cardGroups
     */
    public void addMBlogs(List<CardGroup> cardGroups) {
        if (cardGroups != null) {
            int start = mCardGroups.size();
            mCardGroups.addAll(cardGroups);
            notifyItemRangeInserted(start, cardGroups.size());
        }
    }

    /**
     * 加载更多微博（热门微博）
     *
     * @param cards
     */
    public void addMBlogs(List<Card> cards, boolean hot) {
        if (cards != null) {
            int start = mCards.size();
            mCards.addAll(cards);
            notifyItemRangeInserted(start, cards.size());
        }
    }

    /**
     * 刷新微博（搜索微博）
     *
     * @param cardGroups
     */
    public void setMBlogs(List<CardGroup> cardGroups) {
        if (cardGroups != null) {
            mCardGroups.clear();
            mCardGroups.addAll(cardGroups);
            notifyDataSetChanged();
        }
    }

    /**
     * 刷新微博（热门微博）
     *
     * @param cards
     */
    public void setMBlogs(List<Card> cards, boolean hot) {
        if (cards != null) {
            mCards.clear();
            mCards.addAll(cards);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_status, parent, false);
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
            final MBlog mblog;
            if (mHot) {
                mblog = mCards.get(position).mblog;
            } else {
                mblog = mCardGroups.get(position).mblog;
            }
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            // 设置微博字体大小
            itemHolder.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, BaseSettings.sSettings.fontSize);
            itemHolder.tvRetweetedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, BaseSettings.sSettings.fontSize - 2);

            if (mblog.user == null) {
                // 微博被删除
                itemHolder.tvGone.setVisibility(View.VISIBLE);
                itemHolder.llStatus.setVisibility(View.GONE);
                mDataSetter.formatContent(false, Long.parseLong(mblog.id), mblog.text, itemHolder.tvGone);
            } else {
                itemHolder.tvGone.setVisibility(View.GONE);
                itemHolder.llStatus.setVisibility(View.VISIBLE);
                itemHolder.mTvDel.setVisibility(View.GONE);
                itemHolder.tvOp.setVisibility(View.GONE);
                itemHolder.llRetweetedGone.setVisibility(View.GONE);

                // 微博发布者头像
                Glide.with(mActivity).load(mblog.user.profile_image_url).into(itemHolder.civHead);
                // 用户认证
                UserVerify.verify(mblog.user, itemHolder.ivAvatarVip, null);
                String username = mblog.user.screen_name;
                itemHolder.tvUser.setText(username);
                itemHolder.tvSource.setText(mblog.source);
                itemHolder.tvCreatedAt.setText(mblog.created_at);
                // 处理微博正文
                mDataSetter.formatContent(false, Long.parseLong(mblog.id), Html.fromHtml(mblog.text).toString(), itemHolder.tvText);
                itemHolder.tvAttitudesCount.setText(NumberFormatter.formatWBCount(mblog.attitudes_count, 60000));
                if (AttitudeContainer.sHeartContainer.containsKey(Long.parseLong(mblog.id))) {
                    Drawable drawable = mActivity.getResources().getDrawable(R.drawable.ic_like_press);
                    // 必须设置图片大小，否则不显示
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    itemHolder.tvAttitudesCount.setCompoundDrawables(drawable, null, null, null);
                } else {
                    Drawable drawable = mActivity.getResources().getDrawable(R.drawable.ic_like);
                    // 必须设置图片大小，否则不显示
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    itemHolder.tvAttitudesCount.setCompoundDrawables(drawable, null, null, null);
                }
                itemHolder.tvRepostsCount.setText(NumberFormatter.formatWBCount(mblog.reposts_count, 60000));
                itemHolder.tvCommentsCount.setText(NumberFormatter.formatWBCount(mblog.comments_count, 60000));

                itemHolder.ivSinglePic.setVisibility(View.GONE);
                itemHolder.ivGifTag.setVisibility(View.GONE);
                itemHolder.ivLongTag.setVisibility(View.GONE);
                itemHolder.ivRetweetedSinglePic.setVisibility(View.GONE);
                itemHolder.ivRetweetedGifTag.setVisibility(View.GONE);
                itemHolder.ivRetweetedLongTag.setVisibility(View.GONE);
                itemHolder.tvVisibleGroup.setVisibility(View.GONE);

                // 加载微博图片
                if (mblog.pics != null) {
                    // 微博有图片，加载微博图片
                    if (BaseSettings.sSettings.picQuality == PicQuality.NO_PIC) {
                        // 不加载图片
                        itemHolder.llPics.setVisibility(View.GONE);
                    } else if (BaseSettings.sSettings.picQuality == PicQuality.INTELLIGENT) {
                        if (NetWorkUtils.isWifiByType(mActivity)) {
                            // 当前设备接入Wifi
                            mDataSetter.loadStatusPics(mblog.pics, itemHolder, false);
                        } else {
                            // 不加载图片
                            itemHolder.llPics.setVisibility(View.GONE);
                        }
                    } else {
                        mDataSetter.loadStatusPics(mblog.pics, itemHolder, false);
                    }
                } else {
                    // 微博无图片
                    itemHolder.llPics.setVisibility(View.GONE);
                }
                // 被转发的原微博
                final MBlog retweetedStatus = mblog.retweeted_status;
                if (retweetedStatus != null) {
                    itemHolder.llRetweetedStatus.setVisibility(View.VISIBLE);
                    if (retweetedStatus.user == null) {
                        // 原微博被作者删除
                        itemHolder.llRetweetedStatus.setVisibility(View.GONE);
                        itemHolder.llRetweetedGone.setVisibility(View.VISIBLE);
                        // 处理原微博正文
                        mDataSetter.formatContent(false, Long.parseLong(retweetedStatus.id), retweetedStatus.text, itemHolder.tvGoneInfo);
                    } else {
// 原微博存在
                        itemHolder.tvRetweetedUser.setText(mActivity.getString(R.string.at_user_screen_name, retweetedStatus.user.screen_name));
                        // 处理原微博正文
                        mDataSetter.formatContent(false, Long.parseLong(retweetedStatus.id),
                                Html.fromHtml(retweetedStatus.text).toString(), itemHolder.tvRetweetedText);
                        // 原微博的评论数/转发数/表态数
                        itemHolder.tvRetweetedCommentsCount.setText(NumberFormatter.formatWBCount(retweetedStatus.comments_count, 60000));
                        itemHolder.tvRetweetedRepostsCount.setText(NumberFormatter.formatWBCount(retweetedStatus.reposts_count, 60000));
                        itemHolder.tvRetweetedAttitudesCount.setText(NumberFormatter.formatWBCount(retweetedStatus.attitudes_count, 60000));
                        if (AttitudeContainer.sHeartContainer.containsKey(Long.parseLong(retweetedStatus.id))) {
                            itemHolder.tvRetweetedAttitudesCount.setTextColor(ContextCompat.getColor(mActivity,
                                    R.color.colorPrimary));
                        } else {
                            itemHolder.tvRetweetedAttitudesCount.setTextColor(ContextCompat.getColor(mActivity,
                                    R.color.retweeted_count_text_color));
                        }
                        // 点击事件
                        itemHolder.llRetweetedComments.setOnClickListener(new ToComment(mActivity, Long.parseLong(retweetedStatus.id)));
                        itemHolder.llRetweetedReposts.setOnClickListener(new ToRepost(mActivity,
                                Long.parseLong(retweetedStatus.id), mblog.user.screen_name, mblog.text, false));
                        itemHolder.llRetweetedAttitudes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 点赞/取消赞
                                Long aid = AttitudeContainer.sHeartContainer.get(Long.parseLong(retweetedStatus.id));
                                if (aid == null) {
                                    // 需要点赞
                                    mDataSetter.mAttitudeOp.onCreate(Long.parseLong(retweetedStatus.id), "heart", itemHolder.tvRetweetedAttitudesCount);
                                } else {
                                    // 需要取消赞
                                    mDataSetter.mAttitudeOp.onDestroy(Long.parseLong(retweetedStatus.id), aid, itemHolder.tvRetweetedAttitudesCount);
                                }
                            }
                        });
                        // 加载原微博图片
                        if (retweetedStatus.pics != null) {
                            // 原微博有图片，加载微博图片
                            if (BaseSettings.sSettings.picQuality == PicQuality.NO_PIC) {
                                // 不加载图片
                                itemHolder.llRetweetedPics.setVisibility(View.GONE);
                            } else if (BaseSettings.sSettings.picQuality == PicQuality.INTELLIGENT) {
                                if (NetWorkUtils.isWifiByType(mActivity)) {
                                    mDataSetter.loadStatusPics(retweetedStatus.pics, itemHolder, true);
                                } else {
                                    // 不加载图片
                                    itemHolder.llRetweetedPics.setVisibility(View.GONE);
                                }
                            } else {
                                mDataSetter.loadStatusPics(retweetedStatus.pics, itemHolder, true);
                            }
                        } else {
                            // 原微博无图片
                            itemHolder.llRetweetedPics.setVisibility(View.GONE);
                        }
                        // 点击原微博进入原微博详情界面
                        itemHolder.llRetweetedStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = WBStatusDetailActivity.newIntent(mActivity,
                                        Long.parseLong(retweetedStatus.id), itemHolder.getAdapterPosition(), mblog.user.screen_name,
                                        mblog.text, false);
                                mActivity.startActivity(intent);
                            }
                        });
                        // 查看原微博用户主页
                        itemHolder.tvRetweetedUser.setOnClickListener(new ViewUserOp(mActivity, retweetedStatus.user.screen_name));
                    }
                } else {
                    itemHolder.llRetweetedStatus.setVisibility(View.GONE);
                }
                // 点击微博进入微博详情界面
                itemHolder.llStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = WBStatusDetailActivity.newIntent(mActivity, Long.parseLong(mblog.id),
                                itemHolder.getAdapterPosition(), mblog.user.screen_name, mblog.text, mblog.retweeted_status != null);
                        mActivity.startActivity(intent);
                    }
                });
                // 点击事件
                itemHolder.tvCommentsCount.setOnClickListener(new ToComment(mActivity, Long.parseLong(mblog.id)));
                itemHolder.tvRepostsCount.setOnClickListener(new ToRepost(mActivity, Long.parseLong(mblog.id),
                        mblog.user.screen_name, mblog.text, mblog.retweeted_status != null));
                itemHolder.tvAttitudesCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点赞/取消赞
                        Long aid = AttitudeContainer.sHeartContainer.get(Long.parseLong(mblog.id));
                        if (aid == null) {
                            // 需要点赞
                            mDataSetter.mAttitudeOp.onCreate(Long.parseLong(mblog.id), "heart", itemHolder.tvAttitudesCount);
                        } else {
                            // 需要取消赞
                            mDataSetter.mAttitudeOp.onDestroy(Long.parseLong(mblog.id), aid, itemHolder.tvAttitudesCount);
                        }
                    }
                });
                // 点击微博条目用户名
                itemHolder.tvUser.setOnClickListener(new ViewUserOp(mActivity, mblog.user.screen_name));
                // 点击微博条目头像
                itemHolder.civHead.setOnClickListener(new ViewUserOp(mActivity, mblog.user.screen_name));
            }

        } else if (getItemViewType(position) == TYPE_FOOT) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mTvFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        if (mHot) {
            return mCards.size() + 1;
        } else {
            return mCardGroups.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        }
        return TYPE_ITEM;
    }

    private class ItemViewHolder extends StatusItemViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
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
