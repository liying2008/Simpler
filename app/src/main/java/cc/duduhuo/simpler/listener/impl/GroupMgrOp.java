package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.GroupAPI;
import com.sina.weibo.sdk.openapi.models.Group;

import org.json.JSONException;
import org.json.JSONObject;

import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnGroupMgrListener;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 22:56
 * 版本：1.0
 * 描述：分组管理实现类
 * 备注：
 * =======================================================
 */
public class GroupMgrOp implements OnGroupMgrListener {
    private Context mContext;
    private OnGroupUpdateListener mUpdateListener;
    private OnGroupCreateListener mCreateListener;
    private OnGroupDestroyListener mDestroyListener;
    private GroupAPI mGApi;

    public GroupMgrOp(Context context) {
        this.mContext = context;
    }

    @Override
    public void onUpdate(long listId, final int position, final String name, final String description, String tags) {
        if (mGApi == null) {
            mGApi = new GroupAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.update(listId, name, description, tags, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        Group group = Group.parse(obj);
                        if (group.id != 0L) {
                            if (mUpdateListener != null) {
                                mUpdateListener.onSuccess(position, name, description);
                            }
                        } else {
                            if (mUpdateListener != null) {
                                mUpdateListener.onFailure();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mUpdateListener != null) {
                            mUpdateListener.onFailure();
                        }
                    }
                } else {
                    if (mUpdateListener != null) {
                        mUpdateListener.onFailure();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mUpdateListener != null) {
                    mUpdateListener.onFailure();
                }
            }
        });
    }

    @Override
    public void onCreate(String name, String description, String tags) {
        if (mGApi == null) {
            mGApi = new GroupAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.create(name, description, tags, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        Group group = Group.parse(obj);
                        if (group.id != 0L) {
                            if (mCreateListener != null) {
                                mCreateListener.onSuccess(group.id);
                            }
                        } else {
                            if (mCreateListener != null) {
                                mCreateListener.onFailure();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mCreateListener != null) {
                            mCreateListener.onFailure();
                        }
                    }
                } else {
                    if (mCreateListener != null) {
                        mCreateListener.onFailure();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mCreateListener != null) {
                    mCreateListener.onFailure();
                }
            }
        });
    }

    @Override
    public void onDestroy(long listId, final int position) {
        if (mGApi == null) {
            mGApi = new GroupAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mGApi.deleteGroup(listId, new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject obj = new JSONObject(s);
                        long id = obj.optLong("id", 0L);
                        if (id != 0L) {
                            if (mDestroyListener != null) {
                                mDestroyListener.onSuccess(position);
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
                } else {
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

    public void setOnGroupUpdateListener(OnGroupUpdateListener listener) {
        this.mUpdateListener = listener;
    }

    public void setOnGroupCreateListener(OnGroupCreateListener listener) {
        this.mCreateListener = listener;
    }

    public void setOnGroupDestroyListener(OnGroupDestroyListener listener) {
        this.mDestroyListener = listener;
    }

    /**
     * 好友分组更新结果监听
     */
    public interface OnGroupUpdateListener {
        void onSuccess(int position, String name, String description);

        void onFailure();
    }

    /**
     * 好友分组创建结果监听
     */
    public interface OnGroupCreateListener {
        void onSuccess(long listId);

        void onFailure();
    }

    /**
     * 好友分组删除结果监听
     */
    public interface OnGroupDestroyListener {
        void onSuccess(int position);

        void onFailure();
    }
}
