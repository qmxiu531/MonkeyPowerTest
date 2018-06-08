package autotest.gionee.automonkeypowertest.sendmail;

import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.RemoteDBUtil;

class CheckRemoteVersionsTask extends AsyncTask<Void, Integer, ArrayList<RemoteDBUtil.RemoteVersionName>> {
    private boolean isConnect = false;
    public static boolean isChecked = true;
    private ISendMailView mISendMailView;

    CheckRemoteVersionsTask(ISendMailView sendMailView) {
        this.mISendMailView = sendMailView;
    }

    @Override
    protected void onPreExecute() {
        isChecked = false;
        if (mISendMailView.getConnectText() != null) {
            AnimationHelper.setScrollText(mISendMailView.getConnectText(), mISendMailView.getContext().getString(R.string.message_Connecting));
        }
        if (mISendMailView.getSendMailBtn() != null) {
            mISendMailView.getSendMailBtn().setEnabled(false);
        }
        super.onPreExecute();
    }

    @Override
    protected ArrayList<RemoteDBUtil.RemoteVersionName> doInBackground(Void... voids) {
        RemoteDBUtil remoteDBUtil = new RemoteDBUtil();
        isConnect = remoteDBUtil.isConnect();
        ArrayList<RemoteDBUtil.RemoteVersionName> remoteVersionNameList = new ArrayList<>();
        if (isConnect) {
            remoteVersionNameList = remoteDBUtil.getRemoteVersionNameList(Util.getModel());
        }
        SystemClock.sleep(1000);
        remoteDBUtil.close();
        return remoteVersionNameList;
    }

    @Override
    protected void onPostExecute(ArrayList<RemoteDBUtil.RemoteVersionName> list) {
        super.onPostExecute(list);
        try {
            if (mISendMailView.getSendMailBtn() != null) {
                mISendMailView.getSendMailBtn().setEnabled(isConnect);
            }
            if (mISendMailView.getConnectText() != null) {
                AnimationHelper.setScrollText(mISendMailView.getConnectText(), mISendMailView.getContext().getString(R.string.connectService, isConnect ? "成功" : "失败,请连接内网"));
            }
            ArrayList<VersionSipperNamesBean> data = new ArrayList<>();
            for (RemoteDBUtil.RemoteVersionName remoteVersionName : list) {
                data.add(new VersionSipperNamesBean(remoteVersionName, true));
            }
            mISendMailView.updateListViewData(data);
        } catch (Exception e) {
            Util.i(e.toString());
        }
        isChecked = true;
    }
}