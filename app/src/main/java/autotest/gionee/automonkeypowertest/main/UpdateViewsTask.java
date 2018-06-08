package autotest.gionee.automonkeypowertest.main;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.TestParams;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AppHelper;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;

/**
 * gionee
 * 2018/2/5
 */

public class UpdateViewsTask extends AsyncTask<Long, Void, Long> {
    private int size = 0;
    private long      allTime;
    private long      finishTime;
    private IMainView mView;
    private Context   mContext;
    static  long    lastUpdatePlanTime = 0;
    private boolean refresh            = true;

    UpdateViewsTask(IMainView view) {
        mView = view;
        mContext = mView.getContext();
        lastUpdatePlanTime = System.currentTimeMillis();
    }

    @Override
    protected Long doInBackground(Long... longs) {
        File file = new File(Constants.FILE_PATH);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            Util.i("创建文件=" + mkdirs);
        }
        AppHelper          appHelper = new AppHelper(mContext);
        ArrayList<AppInfo> appInfo   = appHelper.getSelectedAppInfo();
        size = appInfo.size();
        try {
            TestParams p            = mView.getParams();
            long       testTime     = p.testTime;
            long       wait         = p.appWaitTime;
            long       cycle_count  = p.cycleCount;
            long       lastWaitTime = p.lastWaitTime;
            allTime = (((testTime + wait) * size) + lastWaitTime) * cycle_count;
            finishTime = allTime * 60 * 1000 + System.currentTimeMillis();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            refresh = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Long along) {
        super.onPostExecute(along);
        try {
            if (refresh) {
                String testAppCount = mContext.getString(R.string.needTestAppCount, String.valueOf(size));
                String testTime     = mContext.getString(R.string.planTestTimeOfAll, (mView.isMonkeyTest()) ? allTime : "约" + allTime);
                String finishTime   = mContext.getString(R.string.finishTime, Util.long2String(this.finishTime));
                mView.updateExpectText(testAppCount, testTime, finishTime);
            }
        } catch (Exception e) {
            Util.i(e.toString());
        }
    }
}
