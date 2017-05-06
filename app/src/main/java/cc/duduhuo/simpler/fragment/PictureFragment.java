package cc.duduhuo.simpler.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.jpeng.progress.CircleProgress;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.glide.ProgressModelLoader;
import cc.duduhuo.simpler.util.DensityUtil;
import cc.duduhuo.simpler.view.GifMovieView;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/2 18:53
 * 版本：1.0
 * 描述：微博大图显示Fragment
 * 备注：
 * =======================================================
 */
public class PictureFragment extends Fragment {
    private static final String BUNDLE_URL = "url";
    private String url;
    private int mScreenWidth;
    private ProgressModelLoader mProgressModelLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            url = bundle.getString(BUNDLE_URL);
        }
        mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getActivity());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        frameLayout.setLayoutParams(lp);
        frameLayout.setBackgroundColor(0xff000000);
        initView(frameLayout);
        return frameLayout;
    }

    private void initView(FrameLayout frameLayout) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        /* 图片加载进度圈 */
        CircleProgress progress = getProgressView(getActivity());
        mProgressModelLoader = new ProgressModelLoader(getHandler(progress));

        if (url.endsWith(".gif")) {
            GifMovieView gifView = new GifMovieView(frameLayout.getContext());
            gifView.setLayoutParams(layoutParams);
            gifView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击图片返回
                    getActivity().onBackPressed();
                }
            });
            frameLayout.addView(gifView);
            progress.inject(gifView);
            loadPic(url, gifView, true);
        } else {
            PhotoView photoView = new PhotoView(frameLayout.getContext());
            photoView.setLayoutParams(layoutParams);
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    // 点击图片返回
                    getActivity().onBackPressed();
                }
            });
            frameLayout.addView(photoView);
            progress.inject(photoView);
            loadPic(url, photoView, false);
        }
    }

    public static PictureFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_URL, url);
        PictureFragment testFm = new PictureFragment();
        testFm.setArguments(bundle);
        return testFm;
    }

    private void loadPic(String url, final ImageView imageView, boolean isGif) {
        if (isGif) {
            Glide.with(this).using(mProgressModelLoader).load(url).asGif().
                    placeholder(R.drawable.loading_pic_black).into(new SimpleTarget<GifDrawable>() {
                @Override
                public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                    GifMovieView gifView = (GifMovieView) imageView;
                    gifView.setMovieByteArray(resource.getData());
                    // 宽度全屏
                    int width = resource.getIntrinsicWidth();
                    int height = resource.getIntrinsicHeight();
                    if (width > 0 && height > 0) {
                        float wScale = (float) mScreenWidth / width;
                        float hScale = (float) getAppHeight(getActivity()) / height;
                        if (wScale <= hScale) {
                            gifView.setScaleX(wScale);
                            gifView.setScaleY(wScale);
                        } else {
                            gifView.setScaleX(hScale);
                            gifView.setScaleY(hScale);
                        }
                    }
                }
            });
        } else {
            Glide.with(this).using(mProgressModelLoader).load(url).asBitmap().
                    placeholder(R.drawable.loading_pic_black).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    PhotoView photoView = (PhotoView) imageView;
                    photoView.setImageBitmap(resource);

                    int width = resource.getWidth();
                    int height = resource.getHeight();
                    int appHeight = getAppHeight(getActivity());
                    if (width > 0) {
                        float wScale = (float) mScreenWidth * height / (appHeight * width);
                        float hScale = (float) appHeight * width / (height * mScreenWidth);
                        if (hScale > 0 && wScale > 1.0F) {
                            // 双击图片宽度全屏
                            photoView.setScaleLevels(1.0F, wScale, wScale * 1.5F);
                        }
                    } else {
                        photoView.setScaleLevels(1.0F, 1.75F, 3.0F);
                    }
                }
            });
        }
    }

    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

    private Handler getHandler(final CircleProgress progress) {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                progress.setLevel(msg.arg1);
                progress.setMaxValue(msg.arg2);
                return true;
            }
        });
    }

    /**
     * 得到图片加载进度圈
     *
     * @return a CircleProgress instance.
     */
    private CircleProgress getProgressView(Context context) {
        return new CircleProgress.Builder()
                .setTextColor(Color.WHITE)
                .setTextSize(DensityUtil.dip2px(context, 12))
                .setCircleRadius(DensityUtil.dip2px(context, 26))
                .setCircleWidth(DensityUtil.dip2px(context, 2))
                .setProgressColorRes(R.color.colorPrimary, context)
                .setFanPadding(DensityUtil.dip2px(context, 2))
                .setBottomWidth(DensityUtil.dip2px(context, 1))
                .build();
    }
}
