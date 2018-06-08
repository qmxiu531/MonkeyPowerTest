package autotest.gionee.automonkeypowertest.sendmail;


import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

interface ISendMailView {
    void showSnackBar(String message);

    Activity getContext();

    EditText getAddress();

    Button getSendMailBtn();

    Button getRefreshBtn();

    View getClearBtn();

    TextView getConnectText();

    ArrayList<VersionSipperNamesBean> getListViewData();

    void updateListViewData(ArrayList<VersionSipperNamesBean> data);
}
