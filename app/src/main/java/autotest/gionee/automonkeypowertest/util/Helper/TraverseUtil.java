package autotest.gionee.automonkeypowertest.util.Helper;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mysql.jdbc.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

public class TraverseUtil {

    private TestReceiver    testReceiver;
    private Context         mContext;
    private TestAppListener listener;
    private boolean isInit = false;

    public TraverseUtil(Context context) {
        mContext = context;
    }

    public void setTestAppListener(TestAppListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

    public void testApp(String appName) {
        Util.i("testApp");
        testReceiver = new TestReceiver();
        mContext.registerReceiver(testReceiver, new IntentFilter("com.gionee.external.test.done"));
        new Thread() {
            @Override
            public void run() {
                if (!isInit) {
                    installTestApp();
                }
                ShellUtil.execCommand("am instrument -w -r -e apps " + appName + "  -e debug false -e class com.gionee.traversetest.testcase.Case_1000 com.gionee.traversetest.test/android.support.test.runner.AndroidJUnitRunner", false);
            }
        }.start();
    }

    public void stop() {
        if (testReceiver != null) {
            mContext.unregisterReceiver(testReceiver);
        }
        ShellUtil.killUiAutomator();
    }

    public interface TestAppListener {
        void onTestEnd();
    }

    private void installTestApp() {
        String[] files = copyFile2Cache();
        for (String filePath : files) {
            if (!Util.isTest) {
                return;
            }
            if (StringUtils.isNullOrEmpty(filePath)) {
                continue;
            }
            Util.i("pm install -r " + filePath);
            ShellUtil.execCommand("pm uninstall com.gionee.traversetest.testcase", false);
            ShellUtil.CommandResult commandResult = ShellUtil.execCommand("pm install -i autotest.gionee.automonkeypowertest --user 0  -r " + filePath, false);
            Util.i(commandResult.successMsg + "  | " + commandResult.errorMsg);
        }
    }

    private String[] copyFile2Cache() {
        String filePath  = mContext.getExternalCacheDir() + File.separator + "traversetest-debug.apk";
        String filePath2 = mContext.getExternalCacheDir() + File.separator + "traversetest-debug-androidTest.apk";
        copyFilesAssets("traversetest-debug.apk", filePath);
        copyFilesAssets("traversetest-debug-androidTest.apk", filePath2);
        return new String[]{filePath, filePath2};
    }


    public void copyFilesAssets(String fileName, String newPath) {
        try {
            InputStream      is     = mContext.getAssets().open(fileName);
            FileOutputStream fos    = new FileOutputStream(new File(newPath));
            byte[]           buffer = new byte[1024];
            int              byteCount;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Util.i("copyFilesAssets=" + e.toString());
        }
    }

    public class TestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(testReceiver);
            testReceiver = null;
            if (listener != null) {
                listener.onTestEnd();
            }
        }
    }
}
