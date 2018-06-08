package autotest.gionee.automonkeypowertest.report;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.databinding.ActivityReport3Binding;
import autotest.gionee.automonkeypowertest.util.DirectionScrollUtil;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Util;

public class ReportActivity extends AppCompatActivity implements IReportView, DirectionScrollUtil.OnDirectionScrollListener, View.OnClickListener {
    private ArrayAdapter<String>   mSpinnerAdapter;
    private ReportPresenter        mPresenter;
    private ActionBar              actionBar;
    private ReportAdapter          mAdapter;
    private ActivityReport3Binding binding;
    private boolean showTheNews = false;
    private boolean isFirstTime = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report3);
        setLogo();
        mPresenter = new ReportPresenter(this);
        initViews();
        showTheNews = getIntent().getBooleanExtra("showTheNews", false);
        isFirstTime = true;
        int mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        DirectionScrollUtil.setDirectionScrollListener(binding.mReportList, mTouchSlop, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.updateBatchs();
    }

    private void initViews() {
        binding.currentAppSize.setText("0");
        binding.testAppSize.setText("0");
        binding.duration.setText("(20+20)");
        mAdapter = new ReportAdapter(this, mPresenter.getUids());
        binding.highPowerBtn.setOnClickListener(this);
        binding.mReportList.setAdapter(mAdapter);
        binding.mReportList.setOnItemClickListener(reportItemListener);
        binding.batchSpinner.setOnItemSelectedListener(spinnerListener);
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mPresenter.getBatchs());
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.batchSpinner.setAdapter(mSpinnerAdapter);
        AnimationHelper.setUpAnimation(Util.getAllChild(binding.root));
    }

    AdapterView.OnItemSelectedListener spinnerListener    = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mPresenter.showReport(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            adapterView.setSelection(0);
        }
    };
    AdapterView.OnItemClickListener    reportItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mPresenter.showDetailsActivity(i);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };


    @Override
    public void updateCurrentAppSize(long size) {
        AnimationHelper.setScrollText(binding.currentAppSize, String.format(getString(R.string.currentAppSize), size + ""));
    }

    @Override
    public void updateTestAppSize(String string) {
        AnimationHelper.setScrollText(binding.testAppSize, String.format(getString(R.string.testAppSize), string));
    }

    @Override
    public void updateTimesStr(String string) {
        AnimationHelper.setScrollText(binding.duration, String.format(getString(R.string.duration), string));
    }

    @Override
    public void updateTestTypeStr(String string) {
        AnimationHelper.setScrollText(binding.testTypeTv, string);
    }

    @Override
    public void updateReportsData(ReportResultBean list) {
        mAdapter.updateData(list);
        binding.mReportList.setSelection(0);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public ArrayList<ReportBean> getData() {
        return mAdapter.getData();
    }

    @Override
    public void setSelection_Spinner(int selection) {
        if (selection >= 0) {
            binding.batchSpinner.setSelection(selection);
        }
    }

    @Override
    public ArrayAdapter getSpinnerAdapter() {
        return mSpinnerAdapter;
    }

    @Override
    public boolean isFirstTime() {
        return isFirstTime;
    }

    @Override
    public boolean showTheNews() {
        return showTheNews;
    }

    private void setLogo() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.logo);
            actionBar.setTitle(R.string.title_TestReport);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public void onScrollUp() {
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onScrollDown() {
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void onClick(View v) {
        ReportResultBean originData = mAdapter.getOriginData();
        if (originData == null || originData.highPowerResults == null || originData.highPowerResults.size() == 0) {
            Toast.makeText(getApplicationContext(), "无数据", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, HighPowerActivity.class);
            intent.putExtra("highPowerList", originData);
            startActivity(intent);
        }
    }
}
