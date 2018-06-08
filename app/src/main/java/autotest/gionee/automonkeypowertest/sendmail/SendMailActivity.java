package autotest.gionee.automonkeypowertest.sendmail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;


public class SendMailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, ISendMailView {
    private Button mSendMail, mRefreshBtn, mClearBtn;
    private TextView mConnectText;
    private EditText mAddressee;
    private VersionSipperNamesAdapter mAdapter;
    private SendMailPresenter mPresenter;
    private WLanReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmail);
        setLogo();
        mPresenter = new SendMailPresenter(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        mPresenter.checkRemoteVersions();
        regReceiver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendDiffMail:
                mPresenter.handlerSendBtn();
                break;
            case R.id.clear:
                mPresenter.clearReceiverEmails();
                break;
            case R.id.default_email_info:
                mPresenter.showDefaultEmailInfoDialog();
                break;
            case R.id.default_email_refresh:
                mPresenter.refreshDefaultEmail();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Preference.putBoolean(Constants.KEY_IS_SEND_DEFAULT_EMAIL, isChecked);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VersionSipperNamesBean bean = mAdapter.getData(position);
        bean.isChecked = !bean.isChecked;
        mAdapter.updateDate(position, bean);
        CheckBox isChecked = (CheckBox) view.findViewById(R.id.isChecked);
        isChecked.setChecked(bean.isChecked);
    }

    private void initViews() {
        mConnectText = (TextView) findViewById(R.id.connectService);
        mSendMail = (Button) findViewById(R.id.sendDiffMail);
        mClearBtn = (Button) findViewById(R.id.clear);
        mAddressee = (EditText) findViewById(R.id.addressee);
        mAddressee.setText(Preference.getString(Constants.KEY_RECEIVER_EMAILS, Constants.DEFAULT_RECEIVER_EMAILS));
        mSendMail.setOnClickListener(this);
        mClearBtn.setOnClickListener(this);
        RelativeLayout sendMail = (RelativeLayout) findViewById(R.id.sendMail);
        AnimationHelper.setUpAnimation(Util.getAllChild(sendMail));
        mAdapter = new VersionSipperNamesAdapter(this);
        ListView VersionSipperNames_list = (ListView) findViewById(R.id.VersionSipperNamesList);
        VersionSipperNames_list.setAdapter(mAdapter);
        VersionSipperNames_list.setOnItemClickListener(this);
        CheckBox default_email_checkbox = (CheckBox) findViewById(R.id.default_email_checkbox);
        default_email_checkbox.setOnCheckedChangeListener(this);
        default_email_checkbox.setChecked(Preference.getBoolean(Constants.KEY_IS_SEND_DEFAULT_EMAIL));
        mRefreshBtn = (Button) findViewById(R.id.default_email_refresh);
        mRefreshBtn.setOnClickListener(this);
        Button infoBtn = (Button) findViewById(R.id.default_email_info);
        infoBtn.setOnClickListener(this);
    }

    @Override
    public void showSnackBar(String message) {
        Snackbar.make(this.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    public EditText getAddress() {
        return mAddressee;
    }

    @Override
    public Button getSendMailBtn() {
        return mSendMail;
    }

    public Button getRefreshBtn() {
        return mRefreshBtn;
    }

    @Override
    public View getClearBtn() {
        return mClearBtn;
    }

    @Override
    public TextView getConnectText() {
        return mConnectText;
    }

    @Override
    public ArrayList<VersionSipperNamesBean> getListViewData() {
        return mAdapter.getData();
    }

    @Override
    public void updateListViewData(ArrayList<VersionSipperNamesBean> data) {
        mAdapter.updateData(data);
    }


    private void setLogo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_SendMail);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.logo);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    private void regReceiver() {
        mReceiver = new WLanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }

    class WLanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mPresenter.checkRemoteVersions();
        }
    }


}
