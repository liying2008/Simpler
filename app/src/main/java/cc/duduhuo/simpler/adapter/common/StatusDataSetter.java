package cc.duduhuo.simpler.adapter.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.Visible;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.activity.PhotoViewActivity;
import cc.duduhuo.simpler.activity.SearchTopicsActivity;
import cc.duduhuo.simpler.activity.VideoPlayerActivity;
import cc.duduhuo.simpler.activity.WebWBActivity;
import cc.duduhuo.simpler.activity.WBStatusDetailActivity;
import cc.duduhuo.simpler.activity.WBUserHomeActivity;
import cc.duduhuo.simpler.activity.WebViewActivity;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.config.AttitudeContainer;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.config.Browser;
import cc.duduhuo.simpler.config.PicQuality;
import cc.duduhuo.simpler.listener.StatusAnalyzeListener;
import cc.duduhuo.simpler.listener.StatusItemListener;
import cc.duduhuo.simpler.listener.impl.AttitudeOp;
import cc.duduhuo.simpler.listener.impl.DelStatusOp;
import cc.duduhuo.simpler.listener.impl.FavoriteOp;
import cc.duduhuo.simpler.listener.impl.ToComment;
import cc.duduhuo.simpler.listener.impl.ToRepost;
import cc.duduhuo.simpler.listener.impl.ViewUserOp;
import cc.duduhuo.simpler.net.HttpGetTask;
import cc.duduhuo.simpler.net.HttpListener;
import cc.duduhuo.simpler.task.StatusAnalyzeTask;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.GeoParser;
import cc.duduhuo.simpler.util.NetWorkUtils;
import cc.duduhuo.simpler.util.NumberFormatter;
import cc.duduhuo.simpler.util.UserVerify;
import cc.duduhuo.simpler.util.VideoUrlUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 9:23
 * 版本：1.0
 * 描述：微博Item内容分发器
 * 备注：
 * =======================================================
 */
public class StatusDataSetter {
    private Activity mActivity;
    /** 当前时间（毫秒） */
    private static long sNow = 0;
    private StatusListener mStatusListener;
    private GeoParser mGeoParser;
    public static String mPicQuality;
    private StatusAnalyzeTask mAnalyzeTask;
    private DelStatusOp mDelStatusOp;
    private DelStatusOp.OnDelStatusOpListener mDelStatusOpListener;
    public AttitudeOp mAttitudeOp;
    private ArrayMap<Long, SpannableStringBuilder> mTextContent = new ArrayMap<>();
    private ArrayMap<Long, SpannableStringBuilder> mCommentContent = new ArrayMap<>();

    public StatusDataSetter(Activity activity, DelStatusOp.OnDelStatusOpListener delStatusOpListener) {
        this.mActivity = activity;
        this.mDelStatusOpListener = delStatusOpListener;
        sNow = System.currentTimeMillis();
        setPicQuality();
        mAttitudeOp = new AttitudeOp(mActivity);
        mAttitudeOp.setAttitudeOpResultListener(new MyAttitudeOpResultListener());
    }

    public void setPicQuality() {
        switch (BaseSettings.sSettings.picQuality) {
            case PicQuality.THUMBNAIL:
                mPicQuality = "thumbnail";
                break;
            case PicQuality.MIDDLE:
                mPicQuality = "bmiddle";
                break;
            case PicQuality.ORIGINAL:
                mPicQuality = "large";
                break;
            default:
                mPicQuality = "bmiddle";
                break;
        }
    }

    /**
     * 向Item中加载微博数据
     *
     * @param position        item的position
     * @param status          微博
     * @param itemHolder      StatusItemViewHolder
     * @param canDetail       是否可以点击微博条目进入微博详情界面
     * @param isCommentHeader 是否是评论列表的头部
     */
    public void loadStatusData(int position, final Status status, final StatusItemViewHolder itemHolder,
                               boolean canDetail, boolean isCommentHeader) {
        if (status.user == null) {
            // 微博被删除
            itemHolder.tvGone.setVisibility(View.VISIBLE);
            itemHolder.llStatus.setVisibility(View.GONE);
            formatContent(false, status.id, status.text, itemHolder.tvGone);
        } else {
            itemHolder.tvGone.setVisibility(View.GONE);
            itemHolder.llStatus.setVisibility(View.VISIBLE);

            itemHolder.llRetweetedGone.setVisibility(View.GONE);
            // 微博发布者头像
            Glide.with(mActivity).load(status.user.avatar_large).into(itemHolder.civHead);
//        Log.d("verified", status.user.screen_name + ", " + status.user.verified + ", " + status.user.verified_type);
            // 用户认证
            UserVerify.verify(status.user, itemHolder.ivAvatarVip, null);
            String username = status.user.screen_name;
            if (!TextUtils.isEmpty(status.user.remark)) {
                username = status.user.remark;
            }
            itemHolder.tvUser.setText(username);
            itemHolder.tvSource.setText(Html.fromHtml(status.source).toString());

            if (isCommentHeader) {
                // 微博详情界面，不显示删除按钮，不显示可见分组，不显示Op按钮，不显示微博数据（赞、转发、评论数）
                itemHolder.mTvDel.setVisibility(View.GONE);
                itemHolder.tvVisibleGroup.setVisibility(View.GONE);
                itemHolder.tvOp.setVisibility(View.GONE);
                itemHolder.rlCount.setVisibility(View.GONE);
            } else {
                if (status.user.idstr.equals(BaseConfig.sUid)) {
                    // 自己发的微博，显示删除按钮
                    itemHolder.mTvDel.setVisibility(View.VISIBLE);
                    // 处理微博可见性显示文本
                    if (status.visible != null) {
                        dealVisibleGroup(status.visible, itemHolder.tvVisibleGroup);
                    } else {
                        itemHolder.tvVisibleGroup.setVisibility(View.GONE);
                    }
                } else {
                    itemHolder.mTvDel.setVisibility(View.GONE);
                    itemHolder.tvVisibleGroup.setVisibility(View.GONE);
                }
                itemHolder.tvOp.setVisibility(View.VISIBLE);
                itemHolder.rlCount.setVisibility(View.VISIBLE);
            }

            itemHolder.tvCreatedAt.setText(NumberFormatter.dateTransfer(sNow, status.created_at));
            // 处理微博正文
            formatContent(false, status.id, status.text, itemHolder.tvText);

            itemHolder.tvAttitudesCount.setText(NumberFormatter.formatWBCount(status.attitudes_count, 60000));
            if (AttitudeContainer.sHeartContainer.containsKey(status.id)) {
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
            itemHolder.tvRepostsCount.setText(NumberFormatter.formatWBCount(status.reposts_count, 60000));
            itemHolder.tvCommentsCount.setText(NumberFormatter.formatWBCount(status.comments_count, 60000));

            itemHolder.ivSinglePic.setVisibility(View.GONE);
            itemHolder.ivGifTag.setVisibility(View.GONE);
            itemHolder.ivLongTag.setVisibility(View.GONE);
            itemHolder.ivRetweetedSinglePic.setVisibility(View.GONE);
            itemHolder.ivRetweetedGifTag.setVisibility(View.GONE);
            itemHolder.ivRetweetedLongTag.setVisibility(View.GONE);

            // 加载微博图片
            if (status.pic_urls != null) {
                // 微博有图片，加载微博图片
                if (BaseSettings.sSettings.picQuality == PicQuality.NO_PIC) {
                    // 不加载图片
                    itemHolder.llPics.setVisibility(View.GONE);
                } else if (BaseSettings.sSettings.picQuality == PicQuality.INTELLIGENT) {
                    if (NetWorkUtils.isWifiByType(mActivity)) {
                        // 当前设备接入Wifi
                        loadStatusPics(status.pic_urls, itemHolder, false);
                    } else {
                        // 不加载图片
                        itemHolder.llPics.setVisibility(View.GONE);
                    }
                } else {
                    loadStatusPics(status.pic_urls, itemHolder, false);
                }
            } else {
                // 微博无图片
                itemHolder.llPics.setVisibility(View.GONE);
            }
            // 被转发的原微博
            final Status retweetedStatus = status.retweeted_status;
            if (retweetedStatus != null) {
                itemHolder.llRetweetedStatus.setVisibility(View.VISIBLE);
                if (retweetedStatus.user == null) {
                    // 原微博被作者删除
                    itemHolder.llRetweetedStatus.setVisibility(View.GONE);
                    itemHolder.llRetweetedGone.setVisibility(View.VISIBLE);
                    // 处理原微博正文
                    formatContent(false, retweetedStatus.id, retweetedStatus.text, itemHolder.tvGoneInfo);
                } else {
                    // 原微博存在
                    itemHolder.tvRetweetedUser.setText(mActivity.getString(R.string.at_user_screen_name, retweetedStatus.user.screen_name));
                    // 处理原微博正文
                    formatContent(false, retweetedStatus.id, retweetedStatus.text, itemHolder.tvRetweetedText);
                    // 原微博的评论数/转发数/表态数
                    itemHolder.tvRetweetedCommentsCount.setText(NumberFormatter.formatWBCount(retweetedStatus.comments_count, 60000));
                    itemHolder.tvRetweetedRepostsCount.setText(NumberFormatter.formatWBCount(retweetedStatus.reposts_count, 60000));
                    itemHolder.tvRetweetedAttitudesCount.setText(NumberFormatter.formatWBCount(retweetedStatus.attitudes_count, 60000));
                    if (AttitudeContainer.sHeartContainer.containsKey(retweetedStatus.id)) {
                        itemHolder.tvRetweetedAttitudesCount.setTextColor(ContextCompat.getColor(mActivity,
                                R.color.colorPrimary));
                    } else {
                        itemHolder.tvRetweetedAttitudesCount.setTextColor(ContextCompat.getColor(mActivity,
                                R.color.retweeted_count_text_color));
                    }
                    // 点击事件
                    itemHolder.llRetweetedComments.setOnClickListener(new ToComment(mActivity, status.id));
                    itemHolder.llRetweetedReposts.setOnClickListener(new ToRepost(mActivity,
                            status.id, status.user.screen_name, status.text, false));
                    itemHolder.llRetweetedAttitudes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetWorkUtils.isConnectedByState(mActivity)) {
                                // 点赞/取消赞
                                Long aid = AttitudeContainer.sHeartContainer.get(retweetedStatus.id);
                                if (aid == null) {
                                    // 需要点赞
                                    mAttitudeOp.onCreate(retweetedStatus.id, "heart", itemHolder.tvRetweetedAttitudesCount);
                                } else {
                                    // 需要取消赞
                                    mAttitudeOp.onDestroy(retweetedStatus.id, aid, itemHolder.tvRetweetedAttitudesCount);
                                }
                            } else {
                                AppToast.showToast(R.string.network_unavailable);
                            }
                        }
                    });
                    // 加载原微博图片
                    if (retweetedStatus.pic_urls != null) {
                        // 原微博有图片，加载微博图片
                        if (BaseSettings.sSettings.picQuality == PicQuality.NO_PIC) {
                            // 不加载图片
                            itemHolder.llRetweetedPics.setVisibility(View.GONE);
                        } else if (BaseSettings.sSettings.picQuality == PicQuality.INTELLIGENT) {
                            if (NetWorkUtils.isWifiByType(mActivity)) {
                                loadStatusPics(retweetedStatus.pic_urls, itemHolder, true);
                            } else {
                                // 不加载图片
                                itemHolder.llRetweetedPics.setVisibility(View.GONE);
                            }
                        } else {
                            loadStatusPics(retweetedStatus.pic_urls, itemHolder, true);
                        }
                    } else {
                        // 原微博无图片
                        itemHolder.llRetweetedPics.setVisibility(View.GONE);
                    }
                    if (canDetail) {
                        // 点击原微博进入原微博详情界面
                        itemHolder.llRetweetedStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetWorkUtils.isConnectedByState(mActivity)) {
                                    Intent intent = WBStatusDetailActivity.newIntent(mActivity,
                                            retweetedStatus.id, itemHolder.getAdapterPosition(), status.user.screen_name,
                                            status.text, false);
                                    mActivity.startActivity(intent);
                                } else {
                                    AppToast.showToast(R.string.network_unavailable);
                                }
                            }
                        });
                    }
                    // 查看原微博用户主页
                    itemHolder.tvRetweetedUser.setOnClickListener(new ViewUserOp(mActivity, retweetedStatus.user.screen_name));
                }
            } else {
                itemHolder.llRetweetedStatus.setVisibility(View.GONE);
            }
            // Geo
            if (status.geo_ori != null) {
                // 有定位
                if (mGeoParser == null) {
                    mGeoParser = new GeoParser();
                }
                mGeoParser.getAddress(status.geo_ori, new GeoParser.OnLocationListener() {
                    @Override
                    public void onGetAddress(String formattedAddress) {
                        itemHolder.tvPoi.setVisibility(View.VISIBLE);
                        itemHolder.tvPoi.setText(formattedAddress);
                    }

                    @Override
                    public void onFailure(int status) {
                        itemHolder.tvPoi.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        itemHolder.tvPoi.setVisibility(View.GONE);
                    }
                });
            } else {
                // 没有定位
                itemHolder.tvPoi.setVisibility(View.GONE);
            }

            if (canDetail) {
                // 点击微博进入微博详情界面
                itemHolder.llStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetWorkUtils.isConnectedByState(mActivity)) {
                            Intent intent = WBStatusDetailActivity.newIntent(mActivity, status.id,
                                    itemHolder.getAdapterPosition(), status.user.screen_name, status.text, status.retweeted_status != null);
                            mActivity.startActivity(intent);
                        } else {
                            AppToast.showToast(R.string.network_unavailable);
                        }
                    }
                });
            }
            // 点击事件
            itemHolder.tvCommentsCount.setOnClickListener(new ToComment(mActivity, status.id));
            itemHolder.tvRepostsCount.setOnClickListener(new ToRepost(mActivity, status.id,
                    status.user.screen_name, status.text, status.retweeted_status != null));
            itemHolder.tvAttitudesCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点赞/取消赞
                    if (NetWorkUtils.isConnectedByState(mActivity)) {
                        Long aid = AttitudeContainer.sHeartContainer.get(status.id);
                        if (aid == null) {
                            // 需要点赞
                            mAttitudeOp.onCreate(status.id, "heart", itemHolder.tvAttitudesCount);
                        } else {
                            // 需要取消赞
                            mAttitudeOp.onDestroy(status.id, aid, itemHolder.tvAttitudesCount);
                        }
                    } else {
                        AppToast.showToast(R.string.network_unavailable);
                    }
                }
            });
            // 点击微博条目右侧“更多操作”
            itemHolder.tvOp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (retweetedStatus != null) {
                        // 有原微博
                        showStatusOpWindow(v, status.id, status.text, true, retweetedStatus.id,
                                status.favorited, itemHolder.getAdapterPosition(), status.user.screen_name);
                    } else {
                        // 无原微博
                        showStatusOpWindow(v, status.id, status.text, false, 0L, status.favorited,
                                itemHolder.getAdapterPosition(), status.user.screen_name);
                    }
                }
            });
            // 点击微博条目用户名
            itemHolder.tvUser.setOnClickListener(new ViewUserOp(mActivity, status.user.screen_name));
            // 点击微博条目头像
            itemHolder.civHead.setOnClickListener(new ViewUserOp(mActivity, status.user.screen_name));
            // 点击删除按钮删除微博
            itemHolder.mTvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetWorkUtils.isConnectedByState(mActivity)) {
                        final AlertDialog dialog = App.getAlertDialogBuilder(mActivity).create();
                        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_warning, null);
                        ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确定删除微博？");
                        TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
                        TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
                        tvConfirm.setText("删除");
                        dialog.show();
                        DialogUtil.setBottom(dialog, rootView);
                        tvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (mDelStatusOp == null) {
                                    mDelStatusOp = new DelStatusOp(mActivity);
                                }
                                mDelStatusOp.setOnDelStatusOpListener(mDelStatusOpListener);
                                mDelStatusOp.onDestroy(status.id, itemHolder.getAdapterPosition());
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        AppToast.showToast(R.string.network_unavailable);
                    }
                }
            });
        }
    }

    /**
     * 处理微博可见分组显示文本
     *
     * @param visible
     * @param tvVisibleGroup
     */
    private void dealVisibleGroup(Visible visible, TextView tvVisibleGroup) {
        if (visible.type == Visible.VISIBLE_NORMAL) {
            // 普通微博
            tvVisibleGroup.setVisibility(View.GONE);
        } else if (visible.type == Visible.VISIBLE_PRIVACY) {
            tvVisibleGroup.setVisibility(View.VISIBLE);
            tvVisibleGroup.setText("私密");
        } else if (visible.type == Visible.VISIBLE_FRIEND) {
            tvVisibleGroup.setVisibility(View.VISIBLE);
            tvVisibleGroup.setText("朋友圈");
        } else if (visible.type == Visible.VISIBLE_GROUPED) {
            if (BaseConfig.sGroups != null) {
                for (int i = 0; i < BaseConfig.sGroups.size(); i++) {
                    if (BaseConfig.sGroups.get(i).id == visible.list_id) {
                        tvVisibleGroup.setVisibility(View.VISIBLE);
                        tvVisibleGroup.setText(BaseConfig.sGroups.get(i).name);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 格式化微博或评论正文
     *
     * @param text      文本内容
     * @param tvContent TextView
     */
    public void formatContent(final boolean comment, final long id, String text, final TextView tvContent) {
        SpannableStringBuilder builder;
        if (comment) {
            builder = mCommentContent.get(id);
        } else {
            builder = mTextContent.get(id);
        }
        if (builder != null) {
            tvContent.setText(builder);
        } else {
            if (mStatusListener == null) {
                mStatusListener = new StatusListener();
            }
            mAnalyzeTask = StatusAnalyzeTask.getInstance(mActivity, mStatusListener);
            mAnalyzeTask.setStatusAnalyzeListener(new StatusAnalyzeListener() {
                @Override
                public void onSpannableStringComplete(SpannableStringBuilder ssb) {
                    if (comment) {
                        mCommentContent.put(id, ssb);
                    } else {
                        mTextContent.put(id, ssb);
                    }
                    tvContent.setText(ssb);
                }
            });
            mAnalyzeTask.execute(text);

            tvContent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return textTouchEvent((TextView) v, event);
                }
            });
        }
    }

    private class MyAttitudeOpResultListener implements AttitudeOp.AttitudeOpResultListener {

        @Override
        public void onCreateSuccess(boolean lastHeart, long sid, long aid, TextView tvAttitude) {
            AttitudeContainer.sHeartContainer.put(sid, aid);
            if (lastHeart) {
                AppToast.showToast("上次点过赞了");
            } else {
                String countStr = tvAttitude.getText().toString();
                if (TextUtils.isDigitsOnly(countStr)) {
                    int count = Integer.parseInt(countStr) + 1;
                    tvAttitude.setText(NumberFormatter.formatWBCount(count, 60000));
                }
            }
            Drawable[] compoundDrawables = tvAttitude.getCompoundDrawables();
            if (compoundDrawables[0] != null) {
                Drawable drawable = mActivity.getResources().getDrawable(R.drawable.ic_like_press);
                // 必须设置图片大小，否则不显示
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvAttitude.setCompoundDrawables(drawable, null, null, null);
            } else {
                tvAttitude.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            }
        }

        @Override
        public void onCreateFailure(String msg) {
            AppToast.showToast(msg);
        }

        @Override
        public void onDestroySuccess(long sid, long aid, TextView tvAttitude) {
            AttitudeContainer.sHeartContainer.remove(sid);
            Drawable[] compoundDrawables = tvAttitude.getCompoundDrawables();
            if (compoundDrawables[0] != null) {
                Drawable drawable = mActivity.getResources().getDrawable(R.drawable.ic_like);
                // 必须设置图片大小，否则不显示
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvAttitude.setCompoundDrawables(drawable, null, null, null);
            } else {
                tvAttitude.setTextColor(ContextCompat.getColor(mActivity, R.color.retweeted_count_text_color));
            }
            String countStr = tvAttitude.getText().toString();
            if (TextUtils.isDigitsOnly(countStr)) {
                int count = Integer.parseInt(countStr) - 1;
                tvAttitude.setText(NumberFormatter.formatWBCount(count, 60000));
            }
        }

        @Override
        public void onDestroyFailure(String msg) {
            AppToast.showToast(msg);
        }
    }

    /**
     * 微博文本触摸监听处理
     *
     * @param textView 点击的TextView
     * @param event
     * @return true：点击事件被处理；false：点击事件未被处理，向上冒泡
     */
    private boolean textTouchEvent(TextView textView, MotionEvent event) {
        boolean ret = false;
        CharSequence text = textView.getText();
        Spannable sText = Spannable.Factory.getInstance().newSpannable(text);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();
            x += textView.getScrollX();
            y += textView.getScrollY();
            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int offset = layout.getOffsetForHorizontal(line, x);
            ClickableSpan[] links = sText.getSpans(offset, offset, ClickableSpan.class);
            if (links.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    links[0].onClick(textView);
                }
                ret = true;
            }
        }
        return ret;
    }

    /**
     * 加载微博图片
     *
     * @param picList   图片数组
     * @param holder    StatusItemViewHolder
     * @param retweeted 是否是被转发的微博
     */
    public void loadStatusPics(List<String> picList, final StatusItemViewHolder holder, boolean retweeted) {
        final ImageView ivSinglePic;
        LinearLayout llPics;
        List<LinearLayout> lls;
        final List<ImageView> ivs;
        List<ImageView> ivGifTags;
        final ImageView ivGifTag;
        List<ImageView> ivLongTags;
        final ImageView ivLongTag;
        if (retweeted) {
            ivSinglePic = holder.ivRetweetedSinglePic;
            llPics = holder.llRetweetedPics;
            lls = holder.llRetweeteds;
            ivs = holder.ivRetweeteds;
            ivGifTags = holder.ivRetweetedGifTags;
            ivGifTag = holder.ivRetweetedGifTag;
            ivLongTags = holder.ivRetweetedLongTags;
            ivLongTag = holder.ivRetweetedLongTag;
        } else {
            ivSinglePic = holder.ivSinglePic;
            llPics = holder.llPics;
            lls = holder.lls;
            ivs = holder.ivs;
            ivGifTags = holder.ivGifTags;
            ivGifTag = holder.ivGifTag;
            ivLongTags = holder.ivLongTags;
            ivLongTag = holder.ivLongTag;
        }

        final int picNum = picList.size();
        if (picNum == 1) {
            ivSinglePic.setVisibility(View.VISIBLE);
            llPics.setVisibility(View.GONE);
            final String url = picList.get(0).replace("/thumbnail/", "/" + mPicQuality + "/");

            if (url.endsWith(".gif")) {
                ivGifTag.setVisibility(View.VISIBLE);
            } else {
                ivGifTag.setVisibility(View.GONE);
            }
            Glide.with(mActivity).load(url).asBitmap().placeholder(R.drawable.loading_pic).dontAnimate().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int imageWidth = resource.getWidth();
                    int imageHeight = resource.getHeight();
                    if (imageHeight / imageWidth >= 2) {
                        imageHeight = imageWidth;
                        if (ivGifTag.getVisibility() == View.GONE) {
                            ivLongTag.setVisibility(View.VISIBLE);
                        } else {
                            ivLongTag.setVisibility(View.GONE);
                        }
                    } else {
                        ivLongTag.setVisibility(View.GONE);
                    }
                    ViewGroup.LayoutParams params = ivSinglePic.getLayoutParams();
                    params.width = imageWidth;
                    params.height = imageHeight;
                    ivSinglePic.setLayoutParams(params);
                    ivSinglePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ivSinglePic.setImageBitmap(resource);
                }
            });

            ivSinglePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String originalUrl = url.replace("/" + mPicQuality + "/", "/large/");
                    Intent intent = PhotoViewActivity.newIntent(mActivity, originalUrl);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, "image");
                    ActivityCompat.startActivity(mActivity, intent, options.toBundle());
                }
            });
        } else {
            ivSinglePic.setVisibility(View.GONE);
            llPics.setVisibility(View.VISIBLE);
            lls.get(0).setVisibility(View.VISIBLE);
            lls.get(1).setVisibility(View.VISIBLE);
            lls.get(2).setVisibility(View.VISIBLE);
            // 先把所有ImageView和Tag置为不可见
            for (int i = 0; i < ivs.size(); i++) {
                ivs.get(i).setVisibility(View.INVISIBLE);
                ivGifTags.get(i).setVisibility(View.GONE);
                ivLongTags.get(i).setVisibility(View.GONE);
            }

            if (picNum <= 3) {
                lls.get(1).setVisibility(View.GONE);
                lls.get(2).setVisibility(View.GONE);
            } else if (picNum <= 6) {
                lls.get(2).setVisibility(View.GONE);
            }
            // 多图原图地址List
            final ArrayList<String> picUrls = new ArrayList<String>(picNum);
            for (int i = 0; i < picNum; i++) {
                final String url = picList.get(i).replace("/thumbnail/", "/" + mPicQuality + "/");
                final ImageView iv = ivs.get(i);

                // 将有图片的ImageView置为可见
                iv.setVisibility(View.VISIBLE);
                // 显示图片
                showPics(url, iv, ivGifTags.get(i), ivLongTags.get(i));
                final String originalUrl = url.replace("/" + mPicQuality + "/", "/large/");
                picUrls.add(originalUrl);

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int curPicNum = ivs.indexOf(iv);
                        Intent intent = PhotoViewActivity.newIntent(mActivity, picNum, curPicNum, picUrls);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, "image");
                        ActivityCompat.startActivity(mActivity, intent, options.toBundle());
                    }
                });
            }
        }
    }

    /**
     * 显示图片（用于微博多图图片显示）
     *
     * @param url
     * @param iv
     * @param gifTag
     * @param longTag
     */
    private void showPics(String url, final ImageView iv, final ImageView gifTag, final ImageView longTag) {
        if (url.endsWith(".gif")) {
            gifTag.setVisibility(View.VISIBLE);
        } else {
            gifTag.setVisibility(View.GONE);
        }
        Glide.with(mActivity).load(url).asBitmap().placeholder(R.drawable.loading_pic).
                dontAnimate().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                int imageWidth = resource.getWidth();
                int imageHeight = resource.getHeight();
                if (imageHeight / imageWidth >= 2) {
                    if (gifTag.getVisibility() == View.GONE) {
                        longTag.setVisibility(View.VISIBLE);
                    } else {
                        longTag.setVisibility(View.GONE);
                    }
                } else {
                    longTag.setVisibility(View.GONE);
                }
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageBitmap(resource);
            }
        });
    }

    /**
     * 显示微博操作PopupWindow
     *
     * @param view
     * @param retweeted 是否有原微博
     */
    private void showStatusOpWindow(View view, long statusId, String statusText, boolean retweeted,
                                    long retweetedStatusId, boolean favorited, int position, String screenName) {
        View contentView = LayoutInflater.from(mActivity).inflate(
                R.layout.window_status_op, null);
        // 设置Item的点击事件
        View divider_1 = contentView.findViewById(R.id.divider_1);
        TextView tvMark = (TextView) contentView.findViewById(R.id.tvMark);
        if (favorited) {
            tvMark.setText(R.string.unmark_status);
        } else {
            tvMark.setText(R.string.mark_status);
        }
        LinearLayout llRetweetedStatus = (LinearLayout) contentView.findViewById(R.id.llRetweetedStatus);
        LinearLayout llMarkStatus = (LinearLayout) contentView.findViewById(R.id.llMarkStatus);
        LinearLayout llShareStatus = (LinearLayout) contentView.findViewById(R.id.llShareStatus);
        LinearLayout llCopyStatus = (LinearLayout) contentView.findViewById(R.id.llCopyStatus);

        if (retweeted) {
            llRetweetedStatus.setVisibility(View.VISIBLE);
            divider_1.setVisibility(View.VISIBLE);
        } else {
            llRetweetedStatus.setVisibility(View.GONE);
            divider_1.setVisibility(View.GONE);
        }

        final PopupWindow popupWindow = new PopupWindow(contentView,
                ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT, true);

        OpClickListener listener;
        if (retweeted) {
            listener = new OpClickListener(popupWindow, statusId, statusText, retweetedStatusId,
                    favorited, position, screenName);
        } else {
            listener = new OpClickListener(popupWindow, statusId, statusText, 0L, favorited,
                    position, screenName);
        }

        llRetweetedStatus.setOnClickListener(listener);
        llMarkStatus.setOnClickListener(listener);
        llShareStatus.setOnClickListener(listener);
        llCopyStatus.setOnClickListener(listener);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.popup_transparent_bg));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);
    }

    private class OpClickListener implements View.OnClickListener {
        private PopupWindow mPopupWindow;
        private String mStatusText;
        private long mStatusId;
        private long mRetweetedStatusId;
        private boolean mFavorited;
        private FavoriteOp mFavoriteOp;
        private int mPosition;
        private String mScreenName;

        public OpClickListener(PopupWindow popupWindow, long statusId, String statusText,
                               long retweetedStatusId, boolean favorited, int position, String screenName) {
            this.mPopupWindow = popupWindow;
            this.mStatusId = statusId;
            this.mStatusText = statusText;
            this.mRetweetedStatusId = retweetedStatusId;
            this.mFavorited = favorited;
            this.mPosition = position;
            this.mScreenName = screenName;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llRetweetedStatus:
                    if (NetWorkUtils.isConnectedByState(mActivity)) {
                        Intent intent = WBStatusDetailActivity.newIntent(mActivity, mRetweetedStatusId,
                                mPosition, mScreenName, mStatusText, mRetweetedStatusId != 0L);
                        mActivity.startActivity(intent);
                    } else {
                        AppToast.showToast(R.string.network_unavailable);
                    }
                    break;
                case R.id.llMarkStatus:
                    if (NetWorkUtils.isConnectedByState(mActivity)) {
                        if (mFavoriteOp == null) {
                            mFavoriteOp = new FavoriteOp(mActivity);
                            mFavoriteOp.setOnFavoriteOpResultListener(new MyFavoriteOpResultListener(mPosition));
                        }
                        if (mFavorited) {
                            // 取消收藏
                            mFavoriteOp.onDestroy(mStatusId);
                        } else {
                            // 收藏微博
                            mFavoriteOp.onCreate(mStatusId);
                        }
                    } else {
                        AppToast.showToast(R.string.network_unavailable);
                    }
                    break;
                case R.id.llShareStatus:
                    String url = "http://m.weibo.cn/status/" + mStatusId;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mScreenName + "的微博\n" + url);
                    sendIntent.setType("text/plain");
                    mActivity.startActivity(Intent.createChooser(sendIntent, "分享链接到"));
                    break;
                case R.id.llCopyStatus:
                    CommonUtils.copyText(mActivity, mStatusText);
                    AppToast.showToast("已复制");
                    break;
                default:
                    break;
            }
            mPopupWindow.dismiss();
        }
    }

    private class MyFavoriteOpResultListener implements FavoriteOp.OnFavoriteOpResultListener {
        private int mPosition;

        public MyFavoriteOpResultListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onCreateSuccess() {
            AppToast.showToast(R.string.marked);
            if (App.getInstance().mSettingsChangeListener != null) {
                App.getInstance().mSettingsChangeListener.onFavoriteStateChange(mPosition, true);
            }
        }

        @Override
        public void onCreateFailure(String msg) {
            AppToast.showToast(mActivity.getString(R.string.mark_failure_prefix, msg));
        }

        @Override
        public void onDestroySuccess() {
            AppToast.showToast(R.string.unmarked);
            if (App.getInstance().mSettingsChangeListener != null) {
                App.getInstance().mSettingsChangeListener.onFavoriteStateChange(mPosition, false);
            }
        }

        @Override
        public void onDestroyFailure(String msg) {
            AppToast.showToast(mActivity.getString(R.string.unmark_failure_prefix, msg));
        }
    }

    private class StatusListener implements StatusItemListener {

        StatusListener() {
        }

        @Override
        public void onItemAtListener(View widget, String at) {
            // 打开用户信息Activity
            Intent intent = WBUserHomeActivity.newIntent(mActivity, at.replace("@", ""));
            mActivity.startActivity(intent);
        }

        @Override
        public void onItemTopicListener(View widget, String topic) {
            mActivity.startActivity(SearchTopicsActivity.newIntent(mActivity, topic));
        }

        @Override
        public void onItemWebLinkListener(View widget, String url) {
            openBrowser(url);
        }

        @Override
        public void onItemPhotoLinkListener(View widget, String url) {
            if (TextUtils.isEmpty(BaseConfig.sAccount.cookie)) {
                // 获取Cookie
                mActivity.startActivity(WebWBActivity.newIntent(mActivity, true));
                AppToast.showToast(R.string.get_web_wb_cookie);
            } else {
                HttpGetTask httpTask = new HttpGetTask(true, new HttpListener() {
                    @Override
                    public void onResponse(String response) {
                        if (TextUtils.isEmpty(response)) {
                            // Cookie过期，重新获取Cookie
                            AppToast.showToast(R.string.update_web_wb_cookie);
                            mActivity.startActivity(WebWBActivity.newIntent(mActivity, true));
                        } else {
                            try {
                                Document doc = Jsoup.parse(response);
                                Element element = doc.select("img[src]").get(0);
                                String img = element.attr("src");
                                Intent intent = PhotoViewActivity.newIntent(mActivity, img);
                                mActivity.startActivity(intent);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                AppToast.showToast(R.string.update_web_wb_cookie);
                                mActivity.startActivity(WebWBActivity.newIntent(mActivity, true));
                            }
                        }
                    }

                    @Override
                    public void onFailure() {
                        AppToast.showToast(R.string.cannot_view_pic);
                    }
                });
                httpTask.execute(url, BaseConfig.sAccount.cookie);

            }
        }

        @Override
        public void onItemMiaoPaiLinkListener(View widget, String url) {
            playVideo(url);
        }

        @Override
        public void onItemVideoLinkListener(View widget, final String url) {
            openBrowser(url);
            // 有些视频会跳转到秒拍视频，但跳转后的秒拍视频链接有可能会再次跳转到 p.weibo.com 域名进行播放。
            // 解析视频的播放地址比较麻烦，暂时不处理。
//            MovedTempTester tester = new MovedTempTester(new MovedTempListener() {
//                @Override
//                public void onRedirect(String redirectUrl) {
//                    if (redirectUrl.startsWith("http://m.miaopai.com/show/")) {
//                        // 重定向到秒拍视频
//                        playVideo(redirectUrl);
//                    } else {
//                        // 其他链接，使用浏览器打开
//                        openBrowser(redirectUrl);
//                    }
//                }
//
//                @Override
//                public void onNo() {
//                    // 无重定向
//                    openBrowser(url);
//                }
//            });
//            tester.execute(url);
        }

        /**
         * 播放秒拍视频
         *
         * @param miaoPaiUrl
         */
        private void playVideo(String miaoPaiUrl) {
            String realVideoPath = VideoUrlUtil.getRealVideoPath(miaoPaiUrl);
            if (!TextUtils.isEmpty(realVideoPath)) {
                Intent intent = VideoPlayerActivity.newIntent(mActivity, miaoPaiUrl, realVideoPath);
                mActivity.startActivity(intent);
            } else {
                AppToast.showToast(R.string.cannot_fetch_video_real_url);
            }
        }

        /**
         * 使用浏览器打开链接
         *
         * @param webUrl
         */
        private void openBrowser(String webUrl) {
            if (BaseSettings.sSettings.browser == Browser.BUILT_IN) {
                // 使用内置浏览器
                Intent intent = WebViewActivity.newIntent(mActivity, webUrl);
                mActivity.startActivity(intent);
            } else {
                // 使用系统浏览器
                CommonUtils.openBrowser(mActivity, webUrl);
            }
        }
    }
}
