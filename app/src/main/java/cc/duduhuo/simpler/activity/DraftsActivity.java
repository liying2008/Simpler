package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.adapter.DraftsAdapter;
import cc.duduhuo.simpler.app.App;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.bean.Draft;
import cc.duduhuo.simpler.config.BaseConfig;
import cc.duduhuo.simpler.db.DraftServices;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/23 14:51
 * 版本：1.0
 * 描述：微博草稿箱
 * 备注：
 * =======================================================
 */
public class DraftsActivity extends BaseActivity implements DraftsAdapter.OnItemClickListener, DraftsAdapter.OnItemLongClickListener {
    private static final int REQUEST_CODE_REFRESH = 0;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.rvDrafts)
    RecyclerView mRvDrafts;
    @BindView(R.id.tvNone)
    TextView mTvNone;

    private DraftServices mDraftServices;
    private DraftsAdapter mAdapter;
    private List<Draft> mDrafts;
    private int mPosition;

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            LoadDraftsTask task = new LoadDraftsTask();
            task.execute();
            mSwipeRefresh.setRefreshing(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        ButterKnife.bind(this);
        mRvDrafts.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DraftsAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRvDrafts.setAdapter(mAdapter);
        mSwipeRefresh.setColorSchemeResources(BaseConfig.sSwipeRefreshColor);
        mSwipeRefresh.setOnRefreshListener(mRefreshListener);

        mDraftServices = new DraftServices(this);
        // 加载草稿列表
        LoadDraftsTask task = new LoadDraftsTask();
        task.execute();
        mSwipeRefresh.setRefreshing(true);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DraftsActivity.class);
        return intent;
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    @Override
    public void onItemClick(int position) {
        this.mPosition = position;
        startActivityForResult(WBPostActivity.newIntent(this, mDrafts.get(position)), REQUEST_CODE_REFRESH);
    }

    @Override
    public void onItemLongClick(final int position) {
        final AlertDialog dialog = App.getAlertDialogBuilder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_draft_op, null);
        TextView tvDel = ButterKnife.findById(view, R.id.tvDel);
        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                int id = mDrafts.get(position).id;
                mDraftServices.deleteDraftById(id);
                mDrafts.remove(position);
                mAdapter.notifyItemRemoved(position);
                AppToast.showToast(R.string.deleted);
                if (mDrafts.isEmpty()) {
                    mTvNone.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REFRESH && resultCode == RESULT_OK) {
            mDrafts.remove(mPosition);
            mDrafts.add(mPosition, (Draft) data.getParcelableExtra(WBPostActivity.INTENT_REFRESH));
            mAdapter.notifyItemChanged(mPosition);
        }
    }

    /**
     * 加载草稿任务类
     */
    private class LoadDraftsTask extends AsyncTask<Void, Void, List<Draft>> {

        @Override
        protected List<Draft> doInBackground(Void... params) {
            List<Draft> drafts = mDraftServices.getDraftsByUid(BaseConfig.sUid);
            return drafts;
        }

        @Override
        protected void onPostExecute(List<Draft> drafts) {
            super.onPostExecute(drafts);
            mSwipeRefresh.setRefreshing(false);
            if (drafts == null || drafts.isEmpty()) {
                mTvNone.setVisibility(View.VISIBLE);
            } else {
                mTvNone.setVisibility(View.GONE);
                mDrafts = drafts;
                mAdapter.setDrafts(drafts);
            }
        }
    }
}
