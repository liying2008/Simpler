package cc.duduhuo.simpler.listener.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.AccountAPI;
import com.sina.weibo.sdk.openapi.models.User;

import java.io.FileNotFoundException;

import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.config.SinaConsts;
import cc.duduhuo.simpler.listener.OnAvatarListener;
import cc.duduhuo.simpler.util.BitmapUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/5/3 21:37
 * 版本：1.0
 * 描述：用户头像相关功能实现类
 * 备注：
 * =======================================================
 */
public class AvatarOp implements OnAvatarListener {
    private Context mContext;
    private OnAvatarUploadOpListener mListener;
    private AccountAPI mAApi;

    public AvatarOp(Context context) {
        this.mContext = context;
    }

    /**
     * 更新当前登录用户的头像
     *
     * @param path 图片路径
     */
    @Override
    public void upload(String path) {
        Bitmap bitmap = BitmapUtils.getUploadImage(path);
        if (bitmap == null) {
            if (mListener != null) {
                mListener.onFailure("Bitmap为空");
            }
            return;
        }
        upload(bitmap);
    }

    public void upload(Bitmap bitmap) {
        if (mAApi == null) {
            mAApi = new AccountAPI(mContext, SinaConsts.APP_KEY, BaseConfig.sAccessToken);
        }
        mAApi.avatarUpload(bitmap, new RequestListener() {
            @Override
            public void onComplete(String s) {
                User user = User.parse(s);
                if (user != null && user.id > 0L) {
                    // 头像上传成功
                    if (mListener != null) {
                        mListener.onSuccess();
                    }
                } else {
                    if (mListener != null) {
                        mListener.onFailure(mContext.getString(R.string.avatar_upload_failure));
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                if (mListener != null) {
                    mListener.onFailure(mContext.getString(R.string.avatar_upload_failure));
                }
            }
        });
    }

    public void setOnAvatarUploadOpListener(OnAvatarUploadOpListener listener) {
        this.mListener = listener;
    }

    /**
     * 头像上传结果回调接口
     */
    public interface OnAvatarUploadOpListener {
        /** 上传成功 */
        void onSuccess();

        /** 上传失败 */
        void onFailure(String msg);
    }
}
