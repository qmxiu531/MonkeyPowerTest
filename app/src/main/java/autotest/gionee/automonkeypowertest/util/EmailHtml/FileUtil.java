package autotest.gionee.automonkeypowertest.util.EmailHtml;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import autotest.gionee.automonkeypowertest.util.Util;

public class FileUtil {

    /**
     * @testTxtFilePath test.txt文件路径
     * 检查test.txt文件是否存在
     */
    public static boolean isTestTxtFile(String testTxtFilePath) {
        return new File(testTxtFilePath).exists();
    }

    public static boolean delFile(String testTxtFilePath) {
        return new File(testTxtFilePath).delete();
    }

    public void write(String filePath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(filePath, false);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String read(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }
        StringBuilder     sb       = new StringBuilder();
        BufferedReader    reader   = null;
        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            reader = new BufferedReader(isReader);
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception e) {
            Util.i(e.toString());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isReader != null) {
                    isReader.close();
                }
            } catch (Exception e) {
                Util.i(e.toString());
            }
        }
        return sb.toString();
    }

    /**
     * 读取报告的html文件to String
     */
    public static String readReportHtml(String reportHtmlFilePath) {
        StringBuilder buffer = new StringBuilder();
        File mmsFile = new File(Environment.getExternalStorageDirectory()
                .getPath() + File.separator + reportHtmlFilePath);
        BufferedReader    reader = null;
        InputStreamReader inputStreamReader;
        boolean           isHaveTestResult;
        try {
            inputStreamReader = new InputStreamReader(
                    new FileInputStream(mmsFile), "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("<H1")) {//找到邮件的标题
                    String temp = tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</H1>"));
                }
                if (tempString.contains("测试结论:")) {
                    isHaveTestResult = true;
                    if (tempString.contains("通过") && isHaveTestResult) {//找到测试结果
                        String temp       = tempString.substring(tempString.indexOf("通过") - 1, tempString.indexOf("通过") + 2);
                        String testResult = temp.replaceAll(">", "");
                        if (testResult.contains("试")) {
                            testResult = temp.replaceAll("试", "");
                        } else if (testResult.contains(";")) {
                            testResult = temp.replaceAll(";", "");
                        }
                        isHaveTestResult = false;
                    }
                }
                buffer.append(tempString);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    Util.i(e1.toString());
                }
            }
        }
        return buffer.toString();
    }

    public static Intent getExcelFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    public static Intent getExcelFileIntent(String filePath) {
        return getExcelFileIntent(new File(filePath));
    }

    public static String readText(File file) {
        StringBuilder  buffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    Util.i(e1.toString());
                }
            }
        }
        return buffer.toString();
    }
}