package autotest.gionee.automonkeypowertest.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.databinding.ActivityMainBinding;
import autotest.gionee.automonkeypowertest.databinding.ContentMain2Binding;
import autotest.gionee.automonkeypowertest.databinding.ContentMainBinding;
import autotest.gionee.automonkeypowertest.dialog.UploadDialog;
import autotest.gionee.automonkeypowertest.service.HighPowerService;
import autotest.gionee.automonkeypowertest.util.About;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, IMainView {
    private EditText mTestTime_et, mWaitTime_et, mLastWaitTime_et, mCycle_count_et;
    private TextView mTestApp_count_tv, mFinishTime_tv, mTestTime_all_tv;
    private Button mStart_btn, mSelectAppBtn;
    private RadioGroup           testTypeGroup;
    private MainPresenter        mPresenter;
    private MainActivityReceiver mReceiver;
    private ContentMainBinding   binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding b = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding = b.contentLayout;
        Toolbar toolbar = b.toolbar;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        initView();
        mPresenter = new MainPresenter(this);
        mPresenter.initCondition(savedInstanceState);
        regMainReceiver();
        mPresenter.checkLanguage();
        try {
            ShellUtil.requireRoot(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(new Intent(MainActivity.this, HighPowerService.class));
        updateViews();
    }

    public void showAbout(View v) {
        About.showAboutDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (!Util.isTest) {
                    try {
                        TestParams params = getParams();
                        File       file   = new File(Constants.WHOLE_POWER_PATH);
                        if (!file.exists()) {
                            mPresenter.showNoWholePowerFileConfirmDialog(params);
                        } else {
                            mPresenter.startTest(params);
                        }
                    } catch (Exception e) {
                        showSnackBar(e.getMessage());
                        mStart_btn.startAnimation(AnimationHelper.shake());
                    }
                } else {
                    mPresenter.stopTest();
                }
                updateViews();
                break;

            case R.id.selectAppBtn:
                mPresenter.showSelectAppDialog();
                break;

            default:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.i("onStart");
        mPresenter.updatePlanViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.i("onRestart");
        updateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.i("onResume");
        updateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.i("onDestroy");
        unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                mPresenter.showSettingsDialog(getActivity());
                break;
            case R.id.action_abouts:
                About.showAboutDialog(this);
                break;
            case R.id.reports:
                mPresenter.showReport(this, false);
                break;
            case R.id.clearReport:
                mPresenter.handleClearReportBtn();
                break;
            case R.id.exportReports:
                mPresenter.showExportDialog();
                break;
            case R.id.uploadData:
                new UploadDialog(this).show();
                break;
            case R.id.sendMail:
                mPresenter.startSendMailActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    public void updateExpectText(String testAppCount, String testTime, String finishTime) {
        AnimationHelper.setScrollText(mTestApp_count_tv, testAppCount);
        AnimationHelper.setScrollText(mTestTime_all_tv, testTime);
        AnimationHelper.setScrollText(mFinishTime_tv, finishTime);
    }

//    @Deprecated
//    @Override
//    public boolean isMonkeyTest2() {
//        return testTypeGroup.getCheckedRadioButtonId() == R.id.monkeyTest2;
//    }


    @Override
    public void showWarningDialog(String s) {
        DialogHelper.create(this, "提示", s, builder -> builder.setPositiveButton("确定", null)).show();
    }

    @Override
    public TestParams getParams() throws IllegalAccessException {
        try {
            String time = mTestTime_et.getText().toString();
            if (time.equals("") || time.trim().equals("0")) {
                throw new IllegalParamsAccessException(mTestTime_et, "请输入正确的测试时间");
            }
            String cycleCount = mCycle_count_et.getText().toString();
            if (cycleCount.equals("") || cycleCount.trim().equals("0")) {
                showSnackBar("请输入正确的测试次数");
                throw new IllegalParamsAccessException(mCycle_count_et, "请输入正确的测试次数");
            }
            String appWaitTime = mWaitTime_et.getText().toString();
            if (appWaitTime.equals("")) {
                showSnackBar("请输入正确的应用静置时间");
                throw new IllegalParamsAccessException(mWaitTime_et, "请输入正确的应用静置时间");
            }
            String lastWaitTime = mLastWaitTime_et.getText().toString();
            if (lastWaitTime.equals("")) {
                showSnackBar("请输入正确的循环静置时间");
                throw new IllegalParamsAccessException(mLastWaitTime_et, "请输入正确的循环静置时间");
            }
            long testTime     = Long.parseLong(time);
            long cycle        = Long.parseLong(cycleCount);
            long appWait      = Long.parseLong(appWaitTime);
            long lastWait     = Long.parseLong(lastWaitTime);
            int  testAppCount = AppHelper.getTestAppCount();
            if (testAppCount == 0) {
                showSnackBar("请至少选择一个应用测试");
                throw new IllegalParamsAccessException(mTestApp_count_tv, "请至少选择一个应用测试");
            }
            if (Util.getBatterLevel(getApplicationContext()) < 10) {
                showSnackBar("当前电量过低，请充电后测试");
                throw new IllegalAccessException("当前电量过低，请充电后测试");
            }
            return new TestParams().setAppSize(testAppCount)
                    .setTestTime(testTime)
                    .setAppWaitTime(appWait)
                    .setLastWaitTime(lastWait)
                    .setCycleCount(cycle)
                    .setTestType(testTypeGroup.getCheckedRadioButtonId())
                    .setSoftVersion(Util.getSoftVersion());
        } catch (IllegalParamsAccessException e) {
            shakeView(e.getView());
            throw new IllegalAccessException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }

    void shakeView(View v) {
        runOnUiThread(() -> v.startAnimation(AnimationHelper.shake()));
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void updateViews() {
        boolean isTest = Util.isTest;
        if (mStart_btn != null) {
            mStart_btn.setText(!isTest ? getString(R.string.startTest) : getString(R.string.stopTest));
        }
        int childCount = testTypeGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            testTypeGroup.getChildAt(i).setEnabled(!isTest);
        }
        setViewsEnable(!isTest, mTestTime_et, mWaitTime_et, binding.cycleCountLayout, mSelectAppBtn, mCycle_count_et, mLastWaitTime_et);
    }

    private void setViewsEnable(boolean isEnable, View... views) {
        for (View view : views) {
            if (view != null)
                view.setEnabled(isEnable);
        }
    }

    @Override
    public boolean isMonkeyTest() {
        return testTypeGroup.getCheckedRadioButtonId() == R.id.monkeyTest;
    }

    TextWatcher waitTimeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String time = s.toString();
            if (!time.equals("")) {
                mPresenter.updatePlanViews();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher testTimeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String time = charSequence.toString();
            if (!time.equals("") && !time.equals("0") && Long.parseLong(time) != 0) {
                mPresenter.updatePlanViews();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher cycle_count_listener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence count_char, int i, int i1, int i2) {
            String cycle_count_str = count_char.toString();
            if (!cycle_count_str.equals("") && !cycle_count_str.equals("0") && Long.parseLong(cycle_count_str) != 0) {
                mPresenter.updatePlanViews();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private RadioGroup.OnCheckedChangeListener testType_listener = (group, checkedId) -> {
        Configurator.setTestType(checkedId);
        mPresenter.updatePlanViews();
    };

    private void regMainReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Constants.ACTION_TEST_FINISH);
        mReceiver = new MainActivityReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    private void initView() {
        mLastWaitTime_et = this.binding.lastWaitTime;
        mStart_btn = this.binding.start;
        mTestTime_et = this.binding.testTime;
        mWaitTime_et = this.binding.appWaitTime;
        mTestApp_count_tv = this.binding.needTestAppCount;
        mTestTime_all_tv = this.binding.planTestTimeOfAll;
        mFinishTime_tv = this.binding.finishTime;
        mCycle_count_et = this.binding.cycleCount;
        testTypeGroup = this.binding.testTypeGroup;
        mSelectAppBtn = this.binding.selectAppBtn;
        mStart_btn.setOnClickListener(this);
        mSelectAppBtn.setOnClickListener(this);
        mStart_btn.setOnLongClickListener(this);
        mTestTime_et.addTextChangedListener(testTimeTextWatcher);
        mWaitTime_et.addTextChangedListener(waitTimeTextWatcher);
        mLastWaitTime_et.addTextChangedListener(waitTimeTextWatcher);
        mCycle_count_et.addTextChangedListener(cycle_count_listener);
        testTypeGroup.setOnCheckedChangeListener(testType_listener);
        RelativeLayout activity_main = this.binding.contentMain;
        AnimationHelper.setUpAnimation(Util.getAllChild(activity_main));
    }

    @Override
    public void setParams(TestParams params) {
        mTestTime_et.setText(String.valueOf(params.testTime));
        mTestTime_et.setSelection(mTestTime_et.getText().length());
        mWaitTime_et.setText(String.valueOf(params.appWaitTime));
        mWaitTime_et.setSelection(mWaitTime_et.getText().length());
        mLastWaitTime_et.setText(String.valueOf(params.lastWaitTime));
        mLastWaitTime_et.setSelection(mLastWaitTime_et.getText().length());
        mTestApp_count_tv.setText(getString(R.string.needTestAppCount, 0));
        mTestTime_all_tv.setText(getString(R.string.planTestTimeOfAll, 0));
        mFinishTime_tv.setText(getString(R.string.finishTime, 0));
        mCycle_count_et.setText(String.valueOf(params.cycleCount));
        mCycle_count_et.setSelection(mCycle_count_et.getText().length());
        int testType = Configurator.getTestType();
        testTypeGroup.check(testType);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onLongClick(View view) {
        mPresenter.insertTestData();
        return true;
    }


    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_TIME_TICK:
                        if (System.currentTimeMillis() - UpdateViewsTask.lastUpdatePlanTime > 30) {
                            mPresenter.updatePlanViews();
                        }
                        break;
                    case Constants.ACTION_TEST_FINISH:
                        mPresenter.showTestFinishDialog(intent);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
