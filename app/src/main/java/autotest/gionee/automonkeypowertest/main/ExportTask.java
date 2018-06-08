package autotest.gionee.automonkeypowertest.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;

import java.io.File;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.EmailHtml.FileUtil;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.JExcelUtil;
import autotest.gionee.automonkeypowertest.util.Util;

import static autotest.gionee.automonkeypowertest.util.Constants.EXCEL_NAME;


public class ExportTask  extends AsyncTask<Void, Void, Void> {

    private final Activity mContext;

    ExportTask(IMainView view) {
        mContext = view.getActivity();
    }

    private ProgressDialog dialog;
    private boolean isExported = true;

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setTitle(mContext.getString(R.string.exportReports));
        dialog.setMessage("正在导出，请稍候……");
        dialog.setIcon(R.mipmap.logo);
        dialog.setIndeterminate(true);
        dialog.setOnDismissListener(dialogInterface -> showExportResultDialog());
        dialog.getWindow().setWindowAnimations(R.style.dialog_anim);
        dialog.show();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        File file = new File(Constants.FILE_PATH);
        if (!file.exists()) {
            boolean mkdirs = isExported = file.mkdirs();
            Util.i("创建" + (mkdirs ? "成功" : "失败"));
            if (!mkdirs) {
                return null;
            }
        }
        isExported = JExcelUtil.exportExcel(new File(EXCEL_NAME));
        SystemClock.sleep(1000);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

    private void showExportResultDialog() {
        DialogHelper.create(mContext, "导出到", isExported ? EXCEL_NAME : "导出失败", new DialogHelper.OnBeforeCreate() {
            @Override
            public void setOther(AlertDialog.Builder builder) {
                if (Build.VERSION.SDK_INT < 24 && isExported) {
                    builder.setPositiveButton("打开", listener);
                }
                builder.setNegativeButton("确定", null);
            }

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        Intent excelFileIntent = FileUtil.getExcelFileIntent(EXCEL_NAME);
                        mContext.startActivity(excelFileIntent);
                    } catch (Exception e) {
                        Util.i(e.toString());
                    }
                }
            };
        }).show();
    }
}
