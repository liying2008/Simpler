package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.AccountListAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.bean.Account;
import cc.duduhuo.simpler.config.BaseSettings;
import cc.duduhuo.simpler.listener.impl.RecyclerItemClickListener;
import cc.duduhuo.simpler.service.UnreadService;
import cc.duduhuo.simpler.service.notifier.Notifier;
import cc.duduhuo.simpler.util.AccessTokenKeeper;
import cc.duduhuo.simpler.config.Constants;
import cc.duduhuo.simpler.util.AccountUtil;
import cc.duduhuo.simpler.util.PrefsUtils;
import cc.duduhuo.simpler.util.SettingsUtil;

public class SwitchAccountActivity extends BaseActivity {
    /** 已经绑定的账户数量 */
    @BindView(R.id.tvAccountNum)
    TextView mTvAccountNum;
    /** 已绑定的账号列表 */
    @BindView(R.id.rvAccounts)
    RecyclerView mRvAccounts;
    private AccountListAdapter mAdapter;
    private List<Account> mAccounts;
    private AsyncTask<Void, Void, Void> mReadAccountsTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {
            // 读取数据库，可能耗时较长
            mAccounts = App.userServices.getAllUsers();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTvAccountNum.setText(getString(R.string.bind_accounts_num, mAccounts.size()));
            mAdapter = new AccountListAdapter(SwitchAccountActivity.this, mAccounts);
            mRvAccounts.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_account);
        ButterKnife.bind(this);

        // 获取所有已授权的用户信息
        mReadAccountsTask.execute();
        registerAsyncTask(SwitchAccountActivity.class, mReadAccountsTask);

        mRvAccounts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setRecyclerListener();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SwitchAccountActivity.class);
        return intent;
    }

    /**
     * 返回
     */
    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    /**
     * 添加新账号
     */
    @OnClick(R.id.tvAddAccount)
    void addNewAccount() {
        // 设置为添加帐号模式
        BaseConfig.sAddAccountMode = true;
        // 跳转到微博登录界面
        startActivity(WBLoginActivity.newIntent(this));
    }

    /**
     * 设置recyclerView的点击监听
     */
    private void setRecyclerListener() {
        RecyclerItemClickListener.OnItemClickListener localListener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Account account = mAccounts.get(position);
                if (account.uid.equals(BaseConfig.sUid)) {
                    // 无需切换
                    return;
                }
                // 切换帐号
                // 设置当前的用户ID
                BaseConfig.sUid = account.uid;
                // 保存当前用户ID
                PrefsUtils.putString(Constants.PREFS_CUR_UID, account.uid);
                // 设置当前使用的AccessToken
                BaseConfig.sAccessToken = AccessTokenKeeper.readAccessToken(account.uid, true);
                // 设置当前用户帐户
                BaseConfig.sAccount = AccountUtil.readAccount(account.uid, true);
                // 设置当前用户设置
                BaseSettings.sSettings = SettingsUtil.readSettings(account.uid, true);
                App.getInstance().finishAllActivities();
                startActivity(MainActivity.newIntent(SwitchAccountActivity.this));
                Notifier.cancelAll();   // 清空所有通知
                // 重启消息通知
                UnreadService.stopService();
                UnreadService.startService();
                // 默认Token没有失效
                BaseConfig.sTokenExpired = false;
                AppToast.showToast(getString(R.string.account_switch_to, account.name));
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // no op
            }
        };
        mRvAccounts.addOnItemTouchListener(new RecyclerItemClickListener(this, mRvAccounts, localListener));
    }

    @Override
    protected void onDestroy() {
        unregisterAsyncTask(SwitchAccountActivity.class);
        super.onDestroy();
    }
}
