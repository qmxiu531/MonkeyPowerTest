package autotest.gionee.automonkeypowertest.util.Helper;

import android.content.Context;
import android.os.Environment;

import com.mysql.jdbc.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import autotest.gionee.automonkeypowertest.util.EmailHtml.FileUtil;
import autotest.gionee.automonkeypowertest.util.ShellUtil;
import autotest.gionee.automonkeypowertest.util.Util;

/**
 * gionee
 * 2017/12/14
 */

public class IGoHelper {
    public static final String TRAVERSAL_PATH    = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "traversal";
    public static final String TEST_COVERAGE_D_D = "\"testConverage\": \"\\d+\\.\\d+%\"";
    private Context mContext;

    public IGoHelper(Context context) {

        this.mContext = context;
    }

    public double getLastCoverage() {
        File file = new File(TRAVERSAL_PATH);
        if (!file.exists()) {
            Util.i(TRAVERSAL_PATH + "文件目录不存在");
            return 0.0;
        } else {
            File[] files = file.listFiles();
            if (files.length > 0) {
                Arrays.sort(files, (file1, file2) -> (int) (file2.lastModified() - file1.lastModified()));
                File f = files[0];
                if (f.isDirectory()) {
                    File result = new File(f.getAbsolutePath() + File.separator + "report" + File.separator + "report.json");
                    if (!result.exists()) {
                        return 0.0;
                    }
                    String content = FileUtil.readText(result);
                    String s       = Util.regexMatch(content, TEST_COVERAGE_D_D);
                    if (s.contains("\"")) {
                        try {
                            String s1        = s.replaceAll("\"", "");
                            String substring = s1.substring(s1.indexOf(":") + 1, s1.indexOf("%"));
                            Util.i("覆盖率=" + substring);
                            return Double.parseDouble(substring);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
        return 0.0;
    }

    public void testApp(long testTime, String pkgName) {
        Util.i("testApp");
        new Thread() {
            @Override
            public void run() {
                installTestApp();
                if (!Util.isTest) {
                    return;
                }
                ShellUtil.execCommand("am instrument --user 0 -w -r -e debug false -e class com.gionee.autotest.traversal.testcase.AutoTraversalMain -e command-mode false -e target " + pkgName + " -e max-runtime " + testTime * 60 + " -e max-steps 1000 -e max-restart-times 200 -e seed 5 -e throttle 800 com.gionee.autotest.traversal.testcase.test/android.support.test.runner.AndroidJUnitRunner", false);
            }
        }.start();
    }

    public void stop() {
//        ShellUtil.execCommand("pm clear com.gionee.autotest.traversal.testcase",false);
        ShellUtil.killProcess("com.gionee.autotest.traversal.testcase");
        ShellUtil.killUiAutomator();
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
            ShellUtil.execCommand("pm uninstall com.gionee.autotest.traversal.testcase", false);
            ShellUtil.CommandResult commandResult = ShellUtil.execCommand("pm install -i autotest.gionee.automonkeypowertest --user 0  -r " + filePath, false);
            Util.i(commandResult.successMsg + "  | " + commandResult.errorMsg);
        }
    }

    private String[] copyFile2Cache() {
        String filePath  = mContext.getExternalCacheDir() + File.separator + "case.apk";
        String filePath2 = mContext.getExternalCacheDir() + File.separator + "case_AndroidTest.apk";
        copyFilesAssets("case.apk", filePath);
        copyFilesAssets("case_AndroidTest.apk", filePath2);
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
}
