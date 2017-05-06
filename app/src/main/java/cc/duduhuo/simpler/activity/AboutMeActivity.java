package cc.duduhuo.simpler.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnDialogOpListener;
import cc.duduhuo.simpler.listener.impl.FriendshipOp;
import cc.duduhuo.simpler.listener.impl.ViewUserOp;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.DialogUtil;
import cc.duduhuo.simpler.util.UserVerify;

public class AboutMeActivity extends BaseActivity {
    private static final long AUTHOR_UID = 3832099344L;
    private String mAuthorScreenName;
    @BindView(R.id.civHead)
    ImageView mCivHead;
    @BindView(R.id.ivAvatarVip)
    ImageView mIvAvatarVip;
    @BindView(R.id.tvName)
    TextView mTvName;
    @BindView(R.id.tvRelation)
    TextView mTvRelation;
    @BindView(R.id.tvDescription)
    TextView mTvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        UsersAPI usersAPI = new UsersAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        usersAPI.show(AUTHOR_UID, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    User author = User.parse(s);
                    // 加载头像
                    Glide.with(AboutMeActivity.this).load(author.avatar_large).into(mCivHead);
                    UserVerify.verify(author, mIvAvatarVip, mTvDescription);
                    mTvName.setText(author.name);
                    mAuthorScreenName = author.screen_name;
                    if (author.following) {
                        // 已关注
                        mTvRelation.setText(R.string.pay_attention_ok);
                        mTvRelation.setClickable(false);
                    } else {
                        // 未关注
                        mTvRelation.setText(R.string.pay_attention_to_him);
                        mTvRelation.setClickable(true);
                    }
                    // 点击作者头像，进入作者主页
                    mCivHead.setOnClickListener(new ViewUserOp(AboutMeActivity.this, author.screen_name));
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
            }
        });
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AboutMeActivity.class);
        return intent;
    }

    @OnClick(R.id.rlEmail)
    void email() {
        DialogUtil.showSelectDialog(this, "复制邮箱地址", "给作者发邮件", new OnDialogOpListener() {
            @Override
            public void onOp1() {
                CommonUtils.copyText(AboutMeActivity.this, getString(R.string.my_email));
                AppToast.showToast(R.string.copied);
            }

            @Override
            public void onOp2() {
                // 获取设备分辨率大小
                Display display = getWindowManager().getDefaultDisplay();
                String resolution = "Resolution: " + display.getWidth() + "x" + display.getHeight() + "; ";
                String msgPreset = resolution + "\nAndroid: " + android.os.Build.VERSION.RELEASE
                        + "; \nPhone: " + android.os.Build.MODEL
                        + "; \nVersion: " + App.getInstance().getVersionName()
                        + "; \n（以上数据由应用自动收集，发送邮件时请保留）。";
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:" + getString(R.string.my_email)));
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - 用户反馈");
                mailIntent.putExtra(Intent.EXTRA_TEXT, msgPreset);
                ComponentName componentName = mailIntent.resolveActivity(getPackageManager());
                if (componentName != null) {
                    startActivity(mailIntent);
                } else {
                    // 复制邮箱地址
                    CommonUtils.copyText(AboutMeActivity.this, getString(R.string.my_email));
                    AppToast.showToast("您没有安装邮件类应用，已将作者邮箱地址复制到剪贴板。");
                }
            }
        });
    }

    @OnClick(R.id.rlGitHub)
    void gitHub() {
        DialogUtil.showSelectDialog(this, "复制GitHub地址", "在浏览器中打开", new OnDialogOpListener() {

            @Override
            public void onOp1() {
                CommonUtils.copyText(AboutMeActivity.this, getString(R.string.my_github));
                AppToast.showToast(R.string.copied);
            }

            @Override
            public void onOp2() {
                CommonUtils.openBrowser(AboutMeActivity.this, getString(R.string.my_github));
            }
        });
    }

    @OnClick(R.id.rlWebsite)
    void website() {
        DialogUtil.showSelectDialog(this, "复制网址", "在浏览器中打开", new OnDialogOpListener() {
            @Override
            public void onOp1() {
                CommonUtils.copyText(AboutMeActivity.this, getString(R.string.my_website));
                AppToast.showToast(R.string.copied);
            }

            @Override
            public void onOp2() {
                CommonUtils.openBrowser(AboutMeActivity.this, getString(R.string.my_website));
            }
        });
    }

    @OnClick(R.id.tvRelation)
    void followAuthor() {
        FriendshipsAPI fApi = new FriendshipsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        FriendshipOp friendshipOp = new FriendshipOp(this, fApi);
        friendshipOp.onCreate(0, AUTHOR_UID, mAuthorScreenName);
        friendshipOp.setOnFriendshipOpResultListener(new FriendshipOp.OnFriendshipOpResultListener() {
            @Override
            public void onCreateSuccess(int position, String screenName) {
                AppToast.showToast(getString(R.string.pay_attention_prefix, screenName));
                mTvRelation.setText(R.string.pay_attention_ok);
                mTvRelation.setClickable(false);
            }

            @Override
            public void onCreateFailure(int position, String screenName, String msg) {
                AppToast.showToast(getString(R.string.pay_attention_failure_prefix, msg));
            }

            @Override
            public void onDestroySuccess(int position, String screenName) {
                // no op
            }

            @Override
            public void onDestroyFailure(int position, String screenName, String msg) {
                // no op
            }

            @Override
            public void onAddToGroupFailure(String msg) {
                AppToast.showToast(getString(R.string.add_to_group_failure_prefix, msg));
            }
        });
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }
}
