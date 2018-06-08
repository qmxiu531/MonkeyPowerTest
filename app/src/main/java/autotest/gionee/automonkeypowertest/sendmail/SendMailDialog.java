package autotest.gionee.automonkeypowertest.sendmail;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.EmailHtml.Html;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;
import autotest.gionee.automonkeypowertest.util.sqlite.RemoteDBUtil;

class SendMailDialog extends AlertDialog.Builder {
    private ArrayList<Integer> ids;

    SendMailDialog(Context context, ArrayList<Integer> ids) {
        super(context);
        this.ids = ids;
        this.setIcon(R.mipmap.logo);
        this.setTitle(context.getString(R.string.app_name));
        this.setMessage("确定要发送对比邮件?");
        this.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SendMailTask().execute();
            }
        });
        this.setNegativeButton("取消", null);
    }

    @Override
    public AlertDialog show() {
        AlertDialog show = super.show();
        show.getWindow().setWindowAnimations(R.style.dialog_anim);
        return show;
    }

    private class SendMailTask extends AsyncTask<Void, String, String> {


        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress = new ProgressDialog(getContext());
            mProgress.setIndeterminate(true);
            mProgress.setCancelable(false);
            mProgress.setMessage("请稍候……");
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.getWindow().setWindowAnimations(R.style.dialog_anim);
            mProgress.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                publishProgress("正在登录，请稍候……");
                RemoteDBUtil remoteDBUtil = new RemoteDBUtil();
                publishProgress("正在获取数据，请稍候……");
                HtmlReportInfo info = remoteDBUtil.getHtmlVersionSipper(ids);
                remoteDBUtil.close();
                if (info.versionSippers.isEmpty()) {
                    return "发送失败:服务器无数据,请先上传至服务器";
                }
                publishProgress("正在处理数据，请稍候……");
                Html   html           = new Html();
                String content        = html.create(info);
                String receiverEmails = Preference.getString(Constants.KEY_RECEIVER_EMAILS, Constants.DEFAULT_RECEIVER_EMAILS);
                publishProgress("正在发送邮件，请稍候……");
                boolean isSendDefaultEmail = Preference.getBoolean(Constants.KEY_IS_SEND_DEFAULT_EMAIL);
                String  address            = "";
                String  ccAddress          = null;
                if (isSendDefaultEmail) {
                    String defaultAddress_json = Preference.getString(Constants.KEY_ADDRESS_JSON, Constants.DEFAULT_ADDRESS_JSON);
                    if (!"".equals(defaultAddress_json)) {
                        Gson         gson         = new Gson();
                        DefaultEmail defaultEmail = gson.fromJson(defaultAddress_json, DefaultEmail.class);
                        for (String s : defaultEmail.address_List) {
                            address += ";" + s;
                        }
                        for (int i = 0; i < defaultEmail.ccAddress_List.size(); i++) {
                            ccAddress = (i == 0 ? "" : ";") + defaultEmail.ccAddress_List.get(i);
                        }
                    }
                }
                String allAddress = receiverEmails + address;
                Util.i(allAddress);
                if (allAddress.startsWith(";")) {
                    allAddress = allAddress.replaceFirst(";", "");
                }
                Util.i(allAddress);
                html.sendMail(allAddress, ccAddress, content, Util.getProductId());
            } catch (Exception e) {
                e.printStackTrace();
                return "发送失败，出现异常:" + e.toString();
            }
            return "发送成功";
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
            super.onPostExecute(str);
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
