package autotest.gionee.automonkeypowertest.report;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.bean.ReportBean;
import autotest.gionee.automonkeypowertest.bean.ReportResultBean;

interface IReportView {

    void updateCurrentAppSize(long size);

    void updateTestAppSize(String string);

    void updateTimesStr(String string);

    void updateTestTypeStr(String string);

    void updateReportsData(ReportResultBean list);

    Context getContext();

    ArrayList<ReportBean> getData();

    void setSelection_Spinner(int selection);

    ArrayAdapter getSpinnerAdapter();

    boolean isFirstTime();

    boolean showTheNews();
}
