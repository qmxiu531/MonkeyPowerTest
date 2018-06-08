package autotest.gionee.automonkeypowertest.dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.VersionSipper;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;
import autotest.gionee.automonkeypowertest.util.sqlite.RemoteDBUtil;


public class UploadDialog extends AlertDialog.Builder {


    private ArrayList<Long> bs;

    public UploadDialog(Context context) {
        super(context);
        this.setIcon(R.mipmap.logo);
        this.setTitle("选择要上传的批次:");
        this.setItems(getBatchs(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Long id = bs.get(i);
                    new UploadTask().execute(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public AlertDialog show() {
        AlertDialog show = super.show();
        show.getWindow().setWindowAnimations(R.style.dialog_anim);
        return show;
    }

    private String[] getBatchs() {
        bs = DBManager.getBatchs();
//        long mb = Preference.getLong(Constants.KEY_MAX_BATCH, Constants.DEFAULT_MAX_BATCH);
        String[] batchs      = new String[bs.size()];
        String   softVersion = Util.getSoftVersion();
        for (int i = 0; i < bs.size(); i++) {
            batchs[i] = softVersion + "第" + (i + 1) + "次";
        }
        return batchs;
    }

    private class UploadTask extends AsyncTask<Long, String, String> {
        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(getContext());
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.setMessage("请稍候……");
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.getWindow().setWindowAnimations(R.style.dialog_anim);
            mProgress.show();
        }

        @Override
        protected String doInBackground(Long... integers) {
            Long    batch  = integers[0];
            boolean upload = false;
//            int uploadReply = 0;
            int test_data_referer_id = 0;
            try {
                VersionSipper versionSipper = DBManager.getVersionSipper(batch);
                publishProgress("正在获取数据，请稍候……");
                if (versionSipper.sipper_Map_Front.isEmpty())
                    return "上传失败，空数据";
//                Gson gson = new Gson();
//                String content = gson.toJson(versionSipper);
//                String fileName = Util.getSoftVersion() + ".txt";
//                publishProgress("正在登录中，请稍候……");
//                FtpUtil ftpUtil = new FtpUtil(new Client());
                publishProgress("正在上传中，请稍候……");
//                upload = ftpUtil.upload("AppPower" + File.separator + Util.getModel(), fileName, content);
//                uploadReply = FtpUtil.uploadReply;
//                Util.i("操作完毕;" + uploadReply);
                RemoteDBUtil remoteDBUtil = new RemoteDBUtil();
                test_data_referer_id = remoteDBUtil.createProjectData();
                upload = remoteDBUtil.uploadVersionSipper(versionSipper, test_data_referer_id);
                Util.i("操作完毕;" + upload);
                SystemClock.sleep(1000);
            } catch (Exception e) {
                Util.i(e.toString());
            }
//            return upload ? "上传成功!\n地址：18.8.10.110/AppPower/" + Util.getModel() : ("上传失败,错误码:" + uploadReply);
            return upload ? ("上传成功!已上传到云平台订制报告，序号:" + test_data_referer_id) : "上传失败";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mProgress.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String str) {
            mProgress.cancel();
            showResultDialog(str);
        }

        private void showResultDialog(String str) {
            DialogHelper.create(getContext(), getContext().getString(R.string.app_name), str, new DialogHelper.OnBeforeCreate() {
                @Override
                public void setOther(AlertDialog.Builder builder) {
                    builder.setPositiveButton("确定", null);
                }
            }).show();
        }
    }
}
