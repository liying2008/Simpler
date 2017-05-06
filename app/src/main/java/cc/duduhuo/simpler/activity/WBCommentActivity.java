package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.util.CommonUtils;
import cc.duduhuo.simpler.util.InputMethodUtils;
import cc.duduhuo.weibores.entities.Emoticon;
import cc.duduhuo.weibores.fragment.EmoticonPanelFragment;
import cc.duduhuo.weibores.utils.EmoticonUtils;

public class WBCommentActivity extends BaseActivity implements EmoticonPanelFragment.OnEmoticonClickListener {
    private static final String TAG = "WBCommentActivity";
    private static final String INTENT_SID = "sid";
    private static final String INTENT_CID = "cid";
    private static final String INTENT_STATUS = "status";
    private static final String INTENT_SCREEN_NAME = "screen_name";
    private static final String INTENT_COMMENT= "comment";
    private static final String INTENT_REPOST= "repost";
    private static final String INTENT_RETWEETED = "retweeted";
    private static StringBuilder sContentBuilder;
    /** 微博字数（中文字符个数） */
    private static int sLength = 0;
    /** 请求码 */
    private static final int REQUEST_AT_CODE = 2;   // AT
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.tvCount)
    TextView mTvCount;
    @BindView(R.id.ivEmoticon)
    ImageView mIvEmoticon;
    @BindView(R.id.flEmoticon)
    FrameLayout mFlEmoticon;
    @BindView(R.id.cbRepost)
    CheckBox mCbRepost;

    /** 微博表情面板Fragment */
    private EmoticonPanelFragment mEmoticonFragment;
    private long mSid;  // 微博Id
    private long mCid;  // 评论Id
    private String mScreenName;  // 被回复（或转发）的用户昵称
    private String mComment;  // 被回复的评论内容
    private boolean mRepost;    // 是否是只转发（不评论）
    private String mStatus;     // 微博文本
    private boolean mRetweeted; // 被转发的是不是 转发的微博

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_comment);
        ButterKnife.bind(this);

        mTvTitle.setText("评论微博");
        Intent intent = getIntent();
        mSid = intent.getLongExtra(INTENT_SID, 0L);
        mCid = intent.getLongExtra(INTENT_CID, 0L);
        mRepost = intent.getBooleanExtra(INTENT_REPOST, false);
        if (mCid != 0) {
            mScreenName = intent.getStringExtra(INTENT_SCREEN_NAME);
            mComment = intent.getStringExtra(INTENT_COMMENT);
        }
        sContentBuilder = new StringBuilder();
        mEtContent.requestFocus();
        // 添加文本变化监听器
        mEtContent.addTextChangedListener(new StatusTextWatcher());

        if (mRepost) {
            // 只转发
            mStatus = intent.getStringExtra(INTENT_STATUS);
            mScreenName = intent.getStringExtra(INTENT_SCREEN_NAME);
            mRetweeted = intent.getBooleanExtra(INTENT_RETWEETED, false);
            mTvTitle.setText("转发微博");
            mCbRepost.setText("只转发原微博");
            mEtContent.setHint("转发微博");
        } else {
            mCbRepost.setText("同时转发到微博");
            if (mCid == 0L) {
                mEtContent.setHint("评论微博");
            } else {
                mTvTitle.setText("回复评论");
                mEtContent.setHint("回复评论");
            }
        }
        // 打开输入法软键盘
        InputMethodUtils.openSoftKeyboard(this, mEtContent);
    }

    public static Intent newIntent(Context context, long sid) {
        Intent intent = new Intent(context, WBCommentActivity.class);
        intent.putExtra(INTENT_SID, sid);
        intent.putExtra(INTENT_REPOST, false);
        return intent;
    }

    public static Intent newIntent(Context context, long sid, String screenName, String status,
                                   boolean retweeted, boolean repost) {
        Intent intent = new Intent(context, WBCommentActivity.class);
        intent.putExtra(INTENT_SID, sid);
        intent.putExtra(INTENT_SCREEN_NAME, screenName);
        intent.putExtra(INTENT_STATUS, status);
        intent.putExtra(INTENT_RETWEETED, retweeted);
        intent.putExtra(INTENT_REPOST, repost);
        return intent;
    }

    public static Intent newIntent(Context context, long sid, long cid, String screenName, String comment) {
        Intent intent = new Intent(context, WBCommentActivity.class);
        intent.putExtra(INTENT_SID, sid);
        intent.putExtra(INTENT_REPOST, false);
        intent.putExtra(INTENT_CID, cid);
        intent.putExtra(INTENT_SCREEN_NAME, screenName);
        intent.putExtra(INTENT_COMMENT, comment);
        return intent;
    }


    /**
     * 发布评论
     */
    @OnClick(R.id.tvPost)
    void post() {
        if (mRepost) {
            // 只转发
            StatusesAPI sApi = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
            if (!mRetweeted) {
                repost(sApi, sContentBuilder.toString(), 0);
            } else {
                if (mCbRepost.isChecked()) {
                    repost(sApi, sContentBuilder.toString(), 0);
                } else {
                    String content = sContentBuilder.toString() + "//@" + mScreenName + ":" + mStatus;
                    repost(sApi, content, 0);
                }
            }
        } else {
            boolean isRepost = mCbRepost.isChecked();
            if (isRepost) {
                StatusesAPI sApi = new StatusesAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
                if (mCid == 0L) {
                    // 评论并转发
                    repost(sApi, sContentBuilder.toString(), 1);
                } else {
                    // 回复评论并转发
                    CommentsAPI cApi = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
                    replyComment(cApi);
                    String content = sContentBuilder.toString() + "//@" + mScreenName + ":" + mComment;
                    repost(sApi, content, 0);
                }
            } else {
                // 只评论
                CommentsAPI cApi = new CommentsAPI(this, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
                if (mCid == 0L) {
                    // 评论微博
                    cApi.create(sContentBuilder.toString(), mSid, false, new RequestListener() {
                        @Override
                        public void onComplete(String s) {
                            if (!TextUtils.isEmpty(s)) {
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    if (obj.isNull("error")) {
                                        // 完成
                                        AppToast.showToast(R.string.finish);
                                        WBCommentActivity.this.finish();
                                    } else {
                                        // 失败
                                        AppToast.showToast(R.string.comment_failure);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    AppToast.showToast(R.string.comment_failure);
                                }
                            }
                        }

                        @Override
                        public void onWeiboException(WeiboException e) {
                            e.printStackTrace();
                            try {
                                JSONObject obj = new JSONObject(e.getMessage());
                                int errorCode = obj.optInt("error_code", 0);
                                if (errorCode == 20101) {
                                    AppToast.showToast("微博不存在");
                                    return;
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            AppToast.showToast(R.string.comment_failure);
                        }
                    });
                } else {
                    // 回复评论
                    replyComment(cApi);
                }
            }
        }
    }

    /**
     * 转发一条微博
     *
     * @param sApi
     * @param status      添加的转发文本
     * @param commentType 是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论
     */
    private void repost(StatusesAPI sApi, String status, int commentType) {
        sApi.repost(mSid, status, commentType, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            // 完成
                            AppToast.showToast(R.string.finish);
                            WBCommentActivity.this.finish();
                        } else {
                            // 失败
                            AppToast.showToast(R.string.repost_failure);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppToast.showToast(R.string.repost_failure);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code", 0);
                    if (errorCode == 20101) {
                        AppToast.showToast("微博不存在");
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                AppToast.showToast(R.string.repost_failure);
            }
        });
    }

    /**
     * 回复一条评论
     *
     * @param cApi
     */
    private void replyComment(CommentsAPI cApi) {
        cApi.reply(mCid, mSid, sContentBuilder.toString(), false, false, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            AppToast.showToast(R.string.replied);
                            finish();
                        } else {
                            AppToast.showToast(R.string.reply_failure);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppToast.showToast(R.string.reply_failure);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                try {
                    JSONObject obj = new JSONObject(e.getMessage());
                    int errorCode = obj.optInt("error_code", 0);
                    if (errorCode == 20101) {
                        AppToast.showToast("微博不存在");
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                AppToast.showToast(R.string.reply_failure);
            }
        });
    }

    /**
     * 切换表情/输入法
     */
    @OnClick(R.id.ivEmoticon)
    void emoticon() {
        if (mEmoticonFragment == null) {
            mEmoticonFragment = EmoticonPanelFragment.Instance();
            // 隐藏软键盘
            InputMethodUtils.closeSoftKeyboard(this);
            getSupportFragmentManager().beginTransaction().add(R.id.flEmoticon, mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.ic_post_keyboard_dark);
        } else {
            if (mEmoticonFragment.isHidden()) {
                // 隐藏软键盘
                InputMethodUtils.closeSoftKeyboard(this);
                getSupportFragmentManager().beginTransaction().show(mEmoticonFragment).commit();
                mIvEmoticon.setBackgroundResource(R.drawable.ic_post_keyboard_dark);
            } else {
                getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
                // 开启软键盘
                InputMethodUtils.openSoftKeyboard(this, mEtContent);
                mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
            }
        }
    }

    @OnClick(R.id.ivAt)
    void at() {
        startActivityForResult(AtFriendActivity.newIntent(this), REQUEST_AT_CODE);
        if (mEmoticonFragment != null && !mEmoticonFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
        }
    }

    @OnClick(R.id.ivTopic)
    void topic() {
        mEtContent.setText(sContentBuilder.toString() + "##");
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
        mEtContent.setSelection(mEtContent.length() - 1);
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AT_CODE && resultCode == RESULT_OK) {
            String at = data.getStringExtra(AtFriendActivity.INTENT_AT);
            mEtContent.setText(sContentBuilder.toString() + "@" + at + " ");
            EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
            mEtContent.setSelection(mEtContent.length());
        }
    }

    /**
     * 获取点击事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isHideEmoticon(view, ev)) {
                if (mEmoticonFragment != null) {
                    getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
                    mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 判定是否需要隐藏表情面板 <br />
     * 如果点击的区域是EditText，则会唤起输入法面板，此时需要隐藏表情面板 <br />
     *
     * @param v
     * @param ev
     * @return
     */
    private boolean isHideEmoticon(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mEmoticonFragment != null && !mEmoticonFragment.isHidden()) {
            // 隐藏表情面板
            getSupportFragmentManager().beginTransaction().hide(mEmoticonFragment).commit();
            mIvEmoticon.setBackgroundResource(R.drawable.selector_ic_post_emoticon);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEmoticonDelete() {
        String text = mEtContent.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                // 模拟按键
                mEtContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
                return;
            }
            mEtContent.getText().delete(index, text.length());
            EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        mEtContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
    }

    @Override
    public void onEmoticonClick(Emoticon emoticon) {
        String s = sContentBuilder.toString();
        String selection = s.substring(0, mEtContent.getSelectionEnd()) + emoticon.getContent();
        mEtContent.setText(selection + s.substring(mEtContent.getSelectionEnd()));
        EmoticonUtils.handleEmoticonText(mEtContent, sContentBuilder.toString(), this);
        mEtContent.setSelection(selection.length());
    }

    /**
     * 微博文本变化监听器
     */
    private class StatusTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            sContentBuilder.delete(0, sContentBuilder.length());
            sContentBuilder.append(s);
//            Log.d(TAG, "内容：" + sContentBuilder.toString());
            sLength = CommonUtils.calcLength(s.toString());
            mTvCount.setText(sLength + "/140");
            if (sLength > 140) {
                mTvCount.setTextColor(ContextCompat.getColor(WBCommentActivity.this, R.color.wb_post_text_count_color_exceed));
            } else {
                mTvCount.setTextColor(ContextCompat.getColor(WBCommentActivity.this, R.color.wb_post_text_count_color_normal));
            }
        }
    }
}
