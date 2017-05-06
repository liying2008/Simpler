package cc.duduhuo.simpler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.duduhuo.applicationtoast.AppToast;
import cc.duduhuo.simpler.R;
import cc.duduhuo.simpler.base.BaseActivity;
import cc.duduhuo.simpler.listener.impl.GroupMgrOp;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/4/29 22:01
 * 版本：1.0
 * 描述：编辑分组信息/新建分组
 * 备注：
 * =======================================================
 */
public class GroupEditActivity extends BaseActivity {
    /** 编辑分组模式 */
    public static final int TYPE_EDIT = 0;
    /** 添加分组模式 */
    public static final int TYPE_ADD = 1;
    /** Activity模式（编辑分组模式、添加分组模式） */
    private static final String INTENT_TYPE = "type";
    /** 分组ID */
    public static final String INTENT_GID = "gid";
    /** Item在RecyclerView中的Position */
    public static final String INTENT_POSITION = "position";
    /** 分组名称 */
    public static final String INTENT_NAME = "name";
    /** 分组描述 */
    public static final String INTENT_DESC = "desc";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.etGroupName)
    EditText mEtGroupName;
    @BindView(R.id.etGroupDesc)
    EditText mEtGroupDesc;
    /** 当前模式 */
    private int mType;
    private long mGid;
    private int mPosition;
    private String mGroupName;
    private String mDesc;
    private GroupMgrOp mGroupMgrOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mType = intent.getIntExtra(INTENT_TYPE, TYPE_EDIT);
        if (mType == TYPE_EDIT) {
            mTvTitle.setText(R.string.title_edit_group);
            mGid = intent.getLongExtra(INTENT_GID, 0L);
            mPosition = intent.getIntExtra(INTENT_POSITION, 0);
            mGroupName = intent.getStringExtra(INTENT_NAME);
            mDesc = intent.getStringExtra(INTENT_DESC);
            mEtGroupName.setText(mGroupName);
            mEtGroupName.setSelection(mGroupName.length());
            mEtGroupDesc.setText(mDesc);
            mEtGroupDesc.setSelection(mDesc.length());
        } else if (mType == TYPE_ADD) {
            mTvTitle.setText(R.string.title_add_group);
        }
        mGroupMgrOp = new GroupMgrOp(this);
        mGroupMgrOp.setOnGroupCreateListener(new MyGroupCreateListener());
        mGroupMgrOp.setOnGroupUpdateListener(new MyGroupUpdateListener());
    }

    public static Intent newIntent(Context context, int type) {
        Intent intent = new Intent(context, GroupEditActivity.class);
        intent.putExtra(INTENT_TYPE, type);
        return intent;
    }

    public static Intent newIntent(Context context, int type, long gid, int position, String name, String desc) {
        Intent intent = new Intent(context, GroupEditActivity.class);
        intent.putExtra(INTENT_TYPE, type);
        intent.putExtra(INTENT_GID, gid);
        intent.putExtra(INTENT_POSITION, position);
        intent.putExtra(INTENT_NAME, name);
        intent.putExtra(INTENT_DESC, desc);
        return intent;
    }

    @OnClick(R.id.tvOk)
    void ok() {
        String name = mEtGroupName.getText().toString();
        String desc = mEtGroupDesc.getText().toString();
        if (mType == TYPE_ADD) {
            // 新增分组
            if (TextUtils.isEmpty(name)) {
                AppToast.showToast("分组名称不能为空");
                return;
            }
            AppToast.showToast(R.string.in_operation);
            mGroupMgrOp.onCreate(name, desc, null);
        } else if (mType == TYPE_EDIT) {
            // 更新分组
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(desc)) {
                AppToast.showToast("分组名称和分组描述不能全为空");
                return;
            }
            if (mGid == 0L) {
                AppToast.showToast("分组ID错误");
                return;
            }
            if ("".equals(name)) {
                name = mGroupName;
            }
            if ("".equals(desc)) {
                desc = mDesc;
            }
            AppToast.showToast(R.string.in_operation);
            mGroupMgrOp.onUpdate(mGid, mPosition, name, desc, null);
        }
    }

    @OnClick(R.id.tvBack)
    void back() {
        this.finish();
    }

    private class MyGroupCreateListener implements GroupMgrOp.OnGroupCreateListener {

        @Override
        public void onSuccess(long listId) {
            AppToast.showToast("分组创建成功");
            Intent data = new Intent();
            data.putExtra(INTENT_GID, listId);
            setResult(RESULT_OK, data);
            finish();
        }

        @Override
        public void onFailure() {
            AppToast.showToast("分组创建失败");
        }
    }

    private class MyGroupUpdateListener implements GroupMgrOp.OnGroupUpdateListener {

        @Override
        public void onSuccess(int position, String name, String description) {
            AppToast.showToast("分组修改成功");
            Intent data = new Intent();
            data.putExtra(INTENT_POSITION, position);
            data.putExtra(INTENT_NAME, name);
            data.putExtra(INTENT_DESC, description);
            setResult(RESULT_OK, data);
            finish();
        }

        @Override
        public void onFailure() {
            AppToast.showToast("分组修改失败");
        }
    }

}
