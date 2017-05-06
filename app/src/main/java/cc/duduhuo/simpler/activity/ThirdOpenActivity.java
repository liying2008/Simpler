package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.ThirdOpensAdapter;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.OpenSourceLib;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/6 13:43
 * 版本：1.0
 * 描述：应用使用到的开源库
 * 备注：
 * =======================================================
 */
public class ThirdOpenActivity extends BaseActivity {
    @BindView(R.id.rvOpen)
    RecyclerView mRvOpen;

    private List<OpenSourceLib> mLibs;
    private ThirdOpensAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_open);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        mLibs = new ArrayList<>();
        mLibs.add(new OpenSourceLib("weibo_android_sdk", "sinaweibosdk", "新浪微博 Android SDK", "https://github.com/sinaweibosdk/weibo_android_sdk"));
        mLibs.add(new OpenSourceLib("ApplicationToast", "liying2008", "Application Toast", "https://github.com/liying2008/ApplicationToast"));
        mLibs.add(new OpenSourceLib("PhotoView", "chrisbanes", "Implementation of ImageView for Android that supports zooming, by various touch gestures.", "https://github.com/chrisbanes/PhotoView"));
        mLibs.add(new OpenSourceLib("glide", "bumptech", "An image loading and caching library for Android focused on smooth scrolling", "https://github.com/bumptech/glide"));
        mLibs.add(new OpenSourceLib("okhttp", "square", "An HTTP+HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp"));
        mLibs.add(new OpenSourceLib("okio", "square", "A modern I/O API for Java.", "https://github.com/square/okio"));
        mLibs.add(new OpenSourceLib("butterknife", "JakeWharton", "Bind Android views and callbacks to fields and methods.", "https://github.com/JakeWharton/butterknife"));
        mLibs.add(new OpenSourceLib("CircleImageView", "hdodenhof", "A circular ImageView for Android", "https://github.com/hdodenhof/CircleImageView"));
        mLibs.add(new OpenSourceLib("jsoup", "jhy", "jsoup: Java HTML Parser, with best of DOM, CSS, and jquery", "https://github.com/jhy/jsoup"));
        mLibs.add(new OpenSourceLib("LoadingProgress", "peng8350", "This is the library when you loading an image from net.you may be use it to show the progress.it support many image framework..such as:fresco,glide,picasso,uil and so on.", "https://github.com/peng8350/LoadingProgress"));
        mLibs.add(new OpenSourceLib("guava", "google", "Google Core Libraries for Java", "https://github.com/google/guava"));
        mLibs.add(new OpenSourceLib("FileDownloader", "lingochamp", "Multitask、Breakpoint-resume、High-concurrency、Simple to use、Single/NotSingle-process", "https://github.com/lingochamp/FileDownloader"));
        mAdapter = new ThirdOpensAdapter(this, mLibs);
        mRvOpen.setAdapter(mAdapter);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ThirdOpenActivity.class);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
