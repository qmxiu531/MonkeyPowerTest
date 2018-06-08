package autotest.gionee.automonkeypowertest.main;


import android.app.Activity;
import android.content.Context;

import autotest.gionee.automonkeypowertest.bean.TestParams;

interface IMainView {
    Activity getActivity();

    TestParams getParams() throws IllegalAccessException;

    void showSnackBar(String text);

    void updateViews();

    boolean isMonkeyTest();

    void updateExpectText(String testAppCount, String testTime, String finishTime);

    void showWarningDialog(String s);

    void setParams(TestParams params);

    Context getContext();
}
