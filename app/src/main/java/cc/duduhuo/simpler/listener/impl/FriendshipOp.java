package cc.duduhuo.simpler.listener.impl;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.fragment.GroupMemberAddFragment;
import cc.duduhuo.simpler.listener.OnFriendshipListener;
import cc.duduhuo.simpler.util.DialogUtil;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/16 14:33
 * 版本：1.0
 * 描述：用户关系操作类
 * 备注：
 * =======================================================
 */
public class FriendshipOp implements OnFriendshipListener {
    private static final String TAG = "FriendshipOp";
    private BaseActivity mActivity;
    private FriendshipsAPI mAPI;
    private OnFriendshipOpResultListener mListener;
    private List<Long> mAddGroupIds;

    public FriendshipOp(BaseActivity activity, FriendshipsAPI api) {
        this.mActivity = activity;
        this.mAPI = api;
    }

    @Override
    public void onCreate(final int position, final long uid, final String screenName) {
        GroupMemberAddFragment fragment = new GroupMemberAddFragment();
        fragment.show(mActivity.getSupportFragmentManager(), "group_member_add_fragment");
        fragment.setOnAddGroupsListener(new GroupMemberAddFragment.OnAddGroupsListener() {
            @Override
            public void onAddGroups(List<Long> groupIds) {
                mAddGroupIds = groupIds;
                Log.d(TAG, mAddGroupIds.toString());
                create(position, uid, screenName);
            }
        });
    }

    private void create(final int position, final long uid, final String screenName) {
        mAPI.create(uid, screenName, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                Log.d(TAG, "关注：" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            // 关注成功
                            if (mListener != null) {
                                mListener.onCreateSuccess(position, screenName);
                            }
                            addToGroups(uid);
                        } else {
                            // 关注失败
                            if (mListener != null) {
                                mListener.onCreateFailure(position, screenName, "error_code:" + obj.optString("error_code"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onCreateFailure(position, screenName, e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onCreateFailure(position, screenName, e.getMessage());
                }
            }
        });
    }

    /**
     * 将关注的用户添加到选中的分组
     */
    private void addToGroups(long uid) {
        int num = mAddGroupIds.size();
        if (num > 0) {
            GroupAPI groupAPI = new GroupAPI(mActivity, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
            for (int i = 0; i < num; i++) {
                addToGroup(groupAPI, uid, mAddGroupIds.get(i));
            }
        }
    }

    /**
     * 将关注的用户添加到指定分组
     *
     * @param groupAPI
     * @param uid
     * @param gid
     */
    private void addToGroup(GroupAPI groupAPI, long uid, long gid) {
        groupAPI.addMember(gid, uid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            // 添加成功
                            // no op
                        } else {
                            if (mListener != null) {
                                mListener.onAddToGroupFailure("error_code:" + obj.optString("error_code"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onAddToGroupFailure(e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onAddToGroupFailure(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy(final int position, final long uid, final String screenName) {
        final AlertDialog dialog = App.getAlertDialogBuilder(mActivity).create();
        View rootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_warning, null);
        ((TextView) rootView.findViewById(R.id.tvTitle)).setText("确定取消关注？");
        TextView tvConfirm = (TextView) rootView.findViewById(R.id.tvConfirm);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tvCancel);
        tvConfirm.setText("取消关注");
        dialog.show();
        DialogUtil.setBottom(dialog, rootView);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy(position, uid, screenName);
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void destroy(final int position, long uid, final String screenName) {
        mAPI.destroy(uid, screenName, new RequestListener() {
            @Override
            public void onComplete(String s) {
//                Log.d(TAG, "取消关注：" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (obj.isNull("error")) {
                            // 取消关注成功
                            if (mListener != null) {
                                mListener.onDestroySuccess(position, screenName);
                            }
                        } else {
                            // 取消关注失败
                            if (mListener != null) {
                                mListener.onDestroyFailure(position, screenName, "error_code:" + obj.optString("error_code"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mListener != null) {
                            mListener.onDestroyFailure(position, screenName, e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onDestroyFailure(position, screenName, e.getMessage());
                }
            }
        });
    }

    public void setOnFriendshipOpResultListener(OnFriendshipOpResultListener listener) {
        this.mListener = listener;
    }

    public interface OnFriendshipOpResultListener {
        /** 关注成功 */
        void onCreateSuccess(int position, String screenName);
        /** 关注失败 */
        void onCreateFailure(int position, String screenName, String msg);
        /** 取消关注成功 */
        void onDestroySuccess(int position, String screenName);
        /** 取消关注失败 */
        void onDestroyFailure(int position, String screenName, String msg);
        /** 添加到分组失败 */
        void onAddToGroupFailure(String msg);
    }
}
