package cc.duduhuo.simpler.listener.impl;

import android.content.Context;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.Group;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnGroupMemberMgrListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/30 16:53
 * 版本：1.0
 * 描述：分组成员管理实现类
 * 备注：
 * =======================================================
 */
public class GroupMemberMgrOp implements OnGroupMemberMgrListener {
    private Context mContext;
    private GroupAPI mGApi;
    private OnGroupMemberDestroyListener mDestroyListener;
    private OnGroupMemberAddListener mAddListener;

    public GroupMemberMgrOp(Context context) {
        this.mContext = context;
    }

    @Override
    public void onDestroy(long uid, long listId, final int position) {
        if (mGApi == null) {
            mGApi = new GroupAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.deleteMembers(listId, uid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    Group group = Group.parse(obj);
                    if (group != null && group.id != 0L) {
                        if (mDestroyListener != null) {
                            mDestroyListener.onSuccess(position, group.name);
                        }
                    } else {
                        if (mDestroyListener != null) {
                            mDestroyListener.onFailure();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mDestroyListener != null) {
                        mDestroyListener.onFailure();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mDestroyListener != null) {
                    mDestroyListener.onFailure();
                }
            }
        });
    }

    @Override
    public void onAdd(long uid, long listId) {
        if (mGApi == null) {
            mGApi = new GroupAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.addMember(listId, uid, new RequestListener() {
            @Override
            public void onComplete(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    Group group = Group.parse(obj);
                    if (group != null && group.id != 0L) {
                        if (mAddListener != null) {
                            mAddListener.onSuccess(group.name);
                        }
                    } else {
                        if (mAddListener != null) {
                            mAddListener.onFailure();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mAddListener != null) {
                        mAddListener.onFailure();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mAddListener != null) {
                    mAddListener.onFailure();
                }
            }
        });
    }

    public void setOnGroupMemberDestroyListener(OnGroupMemberDestroyListener listener) {
        this.mDestroyListener = listener;
    }

    public void setOnGroupMemberAddListener(OnGroupMemberAddListener listener) {
        this.mAddListener = listener;
    }

    /**
     * 删除分组成员结果监听
     */
    public interface OnGroupMemberDestroyListener {
        void onSuccess(int position, String groupName);
        void onFailure();
    }

    /**
     * 添加分组成员结果监听
     */
    public interface OnGroupMemberAddListener {
        void onSuccess(String groupName);
        void onFailure();
    }

}
