package cc.duduhuo.simpler.util;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.User;

import cc.duduhuo.simpler.R;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 11:02
 * 版本：1.0
 * 描述：用户认证信息处理工具类
 * 备注：
 * =======================================================
 */
public class UserVerify {
    /** 个人认证 */
    public static final int V_PERSON = 0x0000;
    /** 企业认证 */
    public static final int V_ENTERPRISE = 0x0001;
    /** 无认证 */
    public static final int NONE = 0x0010;

    /**
     * 获取认证状态
     *
     * @param user
     * @return
     */
    public static int getVerifyType(User user) {
        if (user.verified) {
            if (user.verified_type == 0) {
                return V_PERSON;
            } else {
                return V_ENTERPRISE;
            }
        } else {
            return NONE;
        }
    }

    /**
     * 获取认证状态
     *
     * @param user
     * @return
     */
    public static int getVerifyType(cc.duduhuo.simpler.bean.weibo.User user) {
        if (user.verified) {
            if (user.verified_type == 0) {
                return V_PERSON;
            } else {
                return V_ENTERPRISE;
            }
        } else {
            return NONE;
        }
    }

    /**
     * 处理认证信息的工具
     *
     * @param user
     * @param ivAvatarVip 用户身份认证标识
     * @param tvReason    认证原因/描述
     */
    public static void verify(User user, @Nullable ImageView ivAvatarVip, @Nullable TextView tvReason) {
        int type = getVerifyType(user);
        if (type == NONE) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.GONE);
            }
            if (tvReason != null) {
                tvReason.setText(user.description);
            }
        } else if (type == V_PERSON) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.VISIBLE);
                ivAvatarVip.setImageResource(R.drawable.avatar_vip);
            }
            if (tvReason != null) {
                tvReason.setText(user.verified_reason);
            }
        } else if (type == V_ENTERPRISE) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.VISIBLE);
                ivAvatarVip.setImageResource(R.drawable.avatar_enterprise_vip);
            }
            if (tvReason != null) {
                tvReason.setText(user.verified_reason);
            }
        }
    }

    /**
     * 处理认证信息的工具
     *
     * @param user
     * @param ivAvatarVip 用户身份认证标识
     * @param tvReason    认证原因/描述
     */
    public static void verify(cc.duduhuo.simpler.bean.weibo.User user, @Nullable ImageView ivAvatarVip, @Nullable TextView tvReason) {
        int type = getVerifyType(user);
        if (type == NONE) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.GONE);
            }
            if (tvReason != null) {
                tvReason.setText(user.description);
            }
        } else if (type == V_PERSON) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.VISIBLE);
                ivAvatarVip.setImageResource(R.drawable.avatar_vip);
            }
            if (tvReason != null) {
                tvReason.setText(user.verified_reason);
            }
        } else if (type == V_ENTERPRISE) {
            if (ivAvatarVip != null) {
                ivAvatarVip.setVisibility(View.VISIBLE);
                ivAvatarVip.setImageResource(R.drawable.avatar_enterprise_vip);
            }
            if (tvReason != null) {
                tvReason.setText(user.verified_reason);
            }
        }
    }
}
