package autotest.gionee.automonkeypowertest.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.SelectApp;
import autotest.gionee.automonkeypowertest.bean.SelectAppParams;
import autotest.gionee.automonkeypowertest.bean.SettingsInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.dialog.SelectAppObtainer;
import autotest.gionee.automonkeypowertest.dialog.SettingsDialog;
import autotest.gionee.automonkeypowertest.dialog.SettingsObtainer;
import autotest.gionee.automonkeypowertest.main.SelectAppDialog.OnSelectAppListener;
import autotest.gionee.automonkeypowertest.report.ReportActivity;
import autotest.gionee.automonkeypowertest.sendmail.SendMailActivity;
import autotest.gionee.automonkeypowertest.service.IGoService;
import autotest.gionee.automonkeypowertest.service.MonkeyService;
import autotest.gionee.automonkeypowertest.util.Condition;
import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.Helper.LanguageHelper;
import autotest.gionee.automonkeypowertest.util.Helper.PowerHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;
import autotest.gionee.automonkeypowertest.util.sqlite.DatabaseHelper;

class MainPresenter {

    private IMainView mView;
    private Activity  mContext;

    MainPresenter(IMainView view) {
        this.mView = view;
        mContext = mView.getActivity();
    }

    void initCondition(Bundle savedInstanceState) {
        TestParams lastTestParams = DBManager.getLastTestParams();
        Util.i("lastTestParams=" + lastTestParams.toString());
        mView.setParams(lastTestParams);
        try {
            Condition.addToWhileList(mContext, mContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PowerHelper.getInstance().create(mContext, savedInstanceState);
    }

    void stopTest() {
        Util.i("停止测试");
        Util.isTest = false;
        Util.isCycle = false;
        mContext.stopService(new Intent(mContext, mView.isMonkeyTest() ? MonkeyService.class : IGoService.class));
    }

    void startTest(TestParams params) {
        Util.i("开始测试");
        Util.isTest = true;
        Util.isCycle = true;
        Configurator.setTestParams(params);
        Util.i("testParams=" + params.toString());
        Intent intent = new Intent(mContext, mView.isMonkeyTest() ? MonkeyService.class : IGoService.class);
        mContext.startService(intent);
    }

    void updatePlanViews() {
        if (!Util.isTest && System.currentTimeMillis() - UpdateViewsTask.lastUpdatePlanTime > 20) {
            new UpdateViewsTask(mView).execute();
        }
    }

    void showTestFinishDialog(Intent intent) {
        final boolean isException = intent.getBooleanExtra("isException", false);
        String        errorMsg    = intent.getStringExtra("errorMsg");
        String        title       = isException ? mContext.getString(R.string.dialogTitle_Exception) : mContext.getString(R.string.dialogTitle_TestFinish);
        String        msg         = isException ? (mContext.getString(R.string.content_Exception) + errorMsg) : mContext.getString(R.string.content_TestFinish);
        DialogHelper.create(mContext, title, msg, builder -> {
            builder.setNegativeButton(isException ? mContext.getString(R.string.btn_Text_Confirm) : mContext.getString(R.string.btn_Text_Cancel), null);
            if (!isException) {
                builder.setPositiveButton(R.string.btn_Text_Show, (dialogInterface, i) -> showReport(mContext, true));
            }
        }).show();
    }

    private void showClearReportsConfirm() {
        DialogHelper.create(mContext, mContext.getString(R.string.ClearReport), mContext.getString(R.string.ClearConfirm), builder -> {
            builder.setPositiveButton(mContext.getString(R.string.confirm), (dialogInterface, i) -> {
                try {
                    DBManager.del(DatabaseHelper.TABLE_NAME_MONKEY_POWER, null, null);
//                    Preference.putLong(Constants.KEY_MAX_BATCH, Constants.DEFAULT_MAX_BATCH);
                    Preference.putLong(Constants.KEY_LAST_SELECT_BATCH, Constants.DEFAULT_MAX_BATCH);
                    DBManager.del(DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH, null, null);
                    mView.showSnackBar(mContext.getString(R.string.clearSuccess));
                } catch (Exception e) {
                    mView.showSnackBar(mContext.getString(R.string.clearFail));
                }
            });
            builder.setNegativeButton(mContext.getString(R.string.cancel), null);
        }).show();
    }

    /**
     * 菜单-查看报告-没有报告
     */
    private void showEmptyTips() {
        DialogHelper.create(mContext, mContext.getResources().getString(R.string.app_name), mContext.getString(R.string.noReport), builder -> builder.setNegativeButton(R.string.back, null)).show();
    }

    void showSelectAppDialog() {
        new SelectAppObtainer(mContext) {
            @Override
            protected void onPostExecute(SelectAppParams p) {
                super.onPostExecute(p);
                SelectAppDialog dialog = new SelectAppDialog(mContext, p.appNames, p.selectItem, new OnSelectAppListener() {

                    @Override
                    public void onSave(ArrayList<String> selectApps) {
                        String selectAppsJson = new Gson().toJson(new SelectApp(selectApps));
                        Preference.putString(Constants.KEY_SELECT_APP, selectAppsJson);
                        LanguageHelper.setLastLanguage(LanguageHelper.getCurrentLanguage());
                        updatePlanViews();
                    }
                });
                dialog.show();
            }
        }.execute();
    }

    void showReport(Activity activity, boolean showTheLast) {
        Intent intent = new Intent(activity, ReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("showTheNews", showTheLast);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    void startSendMailActivity(Activity activity) {
        Intent sendMail = new Intent(activity, SendMailActivity.class);
        sendMail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(sendMail);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    void handleClearReportBtn() {
//        long batch = Preference.getLong(Constants.KEY_MAX_BATCH, Constants.DEFAULT_MAX_BATCH);
        if (DBManager.isEmpty(DatabaseHelper.TABLE_NAME_MONKEY_POWER_BATCH)) {
            showEmptyTips();
        } else {
            showClearReportsConfirm();
        }
    }

    void showNoWholePowerFileConfirmDialog(TestParams params) {
        DialogHelper.create(mView.getActivity(), "警告", "此系统版本未检测到有库仑计接口，请联系驱动增加库仑计接口。是否继续测试?", builder -> {
            builder.setPositiveButton("确定", (dialog, which) -> {
                startTest(params);
                mView.updateViews();
            });
            builder.setNegativeButton("取消", null);
        }).show();
    }

    void showExportDialog() {
        DialogHelper.create(mContext, "导出excel表格", "确定导出同版本对比数据?", builder ->
                builder.setPositiveButton("确定", (dialog, which) ->
                        new ExportTask(mView).execute()).setNegativeButton("取消", null)).show();
    }


    void insertTestData() {
        new Thread() {
            @Override
            public void run() {
                Util.insertTestData(mContext);
            }
        }.start();
        Toast.makeText(mContext, "开始填充数据，5秒后查看", Toast.LENGTH_SHORT).show();
    }

    void checkLanguage() {
        LanguageHelper.Language lastLanguage = LanguageHelper.getLastLanguage();
        if (lastLanguage.isEmpty()) {
            LanguageHelper.setLastLanguage(LanguageHelper.getCurrentLanguage());
        } else {
            if (!lastLanguage.equals(LanguageHelper.getCurrentLanguage())) {
                mView.showWarningDialog("当前系统语言有变更，请重新选择测试应用或者切换系统语言,以免出现测试异常");
            }
        }
    }

    void showSettingsDialog(Activity activity) {
        new SettingsObtainer() {
            @Override
            protected void onPostExecute(SettingsInfo settingsInfo) {
                super.onPostExecute(settingsInfo);
                new SettingsDialog(activity, settingsInfo).show();
            }
        }.execute();

    }
}
