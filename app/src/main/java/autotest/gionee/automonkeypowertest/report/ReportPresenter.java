package autotest.gionee.automonkeypowertest.report;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;

class ReportPresenter {
    private IReportView              mView;
    private HashMap<String, Integer> mUids;
    private int selectBatch = 0;
    private ArrayList<String> mSelectBatchs;
    private ArrayList<Long>   batchValues;

    ReportPresenter(IReportView iReportView) {
        this.mView = iReportView;
        mUids = new HashMap<>();
        mSelectBatchs = new ArrayList<>();
    }

    HashMap<String, Integer> getUids() {
        return mUids;
    }

    void showReport(int i) {
        selectBatch = i;
        new ReportTask().execute(batchValues.size() == 0 ? 0L : batchValues.get(i), mView.isFirstTime() ? 1L : 0L);
        Preference.putLong(Constants.KEY_LAST_SELECT_BATCH, selectBatch);
    }

    void showDetailsActivity(int i) {
        Intent intent = new Intent(mView.getContext(), DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("selectBatch", batchValues.get(selectBatch));
        intent.putExtra("selectPkgName", mView.getData().get(i).pkgName);
        mView.getContext().startActivity(intent);
    }

    ArrayList<String> getBatchs() {
        return mSelectBatchs;
    }

    private int getSelectBatch() {
        int  maxBatch   = batchValues.size();
        long lastSelect = Preference.getLong(Constants.KEY_LAST_SELECT_BATCH);
        lastSelect = lastSelect > maxBatch ? maxBatch : lastSelect;
        return (int) (mView.showTheNews() ? maxBatch - 1 : lastSelect);
    }

    void updateBatchs() {
        batchValues = DBManager.getBatchs();
        int size = batchValues.size();
        if (mSelectBatchs.size() < size || size == 0) {
            mSelectBatchs.clear();
            if (size == 0) {
                mSelectBatchs.add("0");
            } else {
                for (long i = 0; i < size; i++) {
                    mSelectBatchs.add((i + 1) + "");
                }
            }
            mView.getSpinnerAdapter().notifyDataSetChanged();
        }
        mView.setSelection_Spinner(getSelectBatch());
    }

    private class ReportTask extends AsyncTask<Long, Integer, ReportResultBean> {

        @Override
        protected ReportResultBean doInBackground(Long... longs) {
            Long batchId = longs[0];
            updateUids();
            ReportResultBean reportBean    = DBManager.getReportBean(batchId);
            int              duration      = 0;
            int              duration_back = 0;
            if (reportBean != null && reportBean.reportBeans.size() > 0) {
                TestParams params = reportBean.params;
                duration = (int) params.testTime;
                long appWaitTime  = params.appWaitTime;
                long lastWaitTime = params.lastWaitTime;
                duration_back = (int) (appWaitTime + lastWaitTime);
            }
            if (reportBean != null) {
                int testAppSize = reportBean.reportBeans.size();
                publishProgress(reportBean.params.appSize, testAppSize, duration, duration_back, reportBean.params.testType);
                if (longs.length > 1 && longs[1] == 1) {
                    SystemClock.sleep(testAppSize > 6 ? 300 : 100);
                }
            }
            return reportBean;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
            super.onProgressUpdate(params);
            mView.updateCurrentAppSize(params[0]);
            mView.updateTestAppSize(params[1] + "");
            mView.updateTimesStr("(" + params[2] + "+" + params[3] + ")");
            mView.updateTestTypeStr(ReportBean.toTestTypeName(params[4]));
        }

        @Override
        protected void onPostExecute(ReportResultBean list) {
            mView.updateReportsData(list);
            super.onPostExecute(list);
        }

//        private int getAppSize(long batch) {
//            try {
//                Cursor cursor = DBManager.getDb().rawQuery("select * from " + DatabaseHelper.TABLE_NAME_MONKEY_POWER_PARAMS + " where batch=?", new String[]{String.valueOf(batch)});
//                if (cursor != null && cursor.getCount() > 0) {
//                    cursor.moveToFirst();
//                    int    appSizeIndex = cursor.getColumnIndex(APP_SIZE);
//                    String size         = cursor.getString(appSizeIndex);
//                    return Integer.parseInt(size);
//                }
//            } catch (Exception e) {
//                Util.i("ReportTask:" + e.toString());
//            }
//            return 0;
//        }

        private void updateUids() {
            AppHelper                                               appHelper  = new AppHelper(mView.getContext());
            HashMap<String, HtmlReportInfo.MyBatterySipper.AppInfo> appInfoMap = appHelper.getAppInfoMap();
            Set<String>                                             pkgNames   = appInfoMap.keySet();
            for (String str : pkgNames) {
                HtmlReportInfo.MyBatterySipper.AppInfo appInfo     = appInfoMap.get(str);
                String                                 packageName = appInfo.packageName;
                mUids.put(appInfo.appName, AppHelper.pkg2Uid(mView.getContext(), packageName));
            }
        }
    }
}
