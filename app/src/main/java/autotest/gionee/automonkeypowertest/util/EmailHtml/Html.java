package autotest.gionee.automonkeypowertest.util.EmailHtml;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.MyBatterySipper.AppInfo;
import autotest.gionee.automonkeypowertest.bean.HtmlReportInfo.VersionSipper;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.PowerHelper;
import autotest.gionee.automonkeypowertest.util.Util;

public class Html {
    private String TAG = Constants.TAG;
    private static final String USERNAME = "zn_software_autotest@gionee.com";
    private static final String PASSWORD = "zn_autotest";

    private static String Tag = "应用功耗测试";

    public String create(HtmlReportInfo info) {
        final ArrayList<VersionSipper> versionSippers = info.versionSippers;
        final HashMap<String, AppInfo> allPkgNames = getAllPkg(versionSippers);
        return create(new HtmlBuilder() {

            @Override
            public String onCreateTitle() {
                return Util.getSoftVersion() + "  应用功耗测试报告";
            }

            @Override
            public String onCreateAuthor() {
                return "测试平台部自动化组";
            }

            @Override
            public void onCreateOther(StringBuilder sb) {
//                for (VersionSipper versionSipper : versionSippers) {
//                    sb.append(createTextLine(versionSipper.softVersion+"整机待机功耗: "+PowerHelper.setDoubleScale(versionSipper.totalPower)));
//                }
            }

            @Override
            public void onCreateHeadline(StringBuilder sb) {
                sb.append("<th width='20%' rowspan='2' style='text-align:center'>应用名</th>");
                for (int i = 0; i < versionSippers.size(); i++) {
                    VersionSipper versionSipper = versionSippers.get(i);
                    Util.i(versionSipper.softVersion);
                    String simpleVersion = Util.simplifySoftVersion(versionSipper.softVersion);
                    sb.append("<th width='20%' colspan='7' style='text-align:center'>").append(simpleVersion).append("</th>");
                }
                sb.append("</tr>");
                for (int i = 0; i < versionSippers.size(); i++) {
                    sb.append("<th width='10%' style='text-align:center'>应用版本号</th>");
                    sb.append("<th width='10%'style='text-align:center'>应用运行时耗电(mAh)</th>");
                    sb.append("<th width='10%'style='text-align:center'>应用运行时平均电流(mA)</th>");
                    sb.append("<th width='10%'style='text-align:center'>应用运行时整机平均电流(mA)</th>");
                    sb.append("<th width='10%'style='text-align:center'>灭屏应用耗电(mAh)</th>");
                    sb.append("<th width='10%'style='text-align:center'>灭屏应用平均电流(mA)</th>");
                    sb.append("<th width='10%'style='text-align:center'>灭屏整机平均电流(mA)</th>");
                }
                sb.append("</tr>");
                Util.i("versionSippers.size:" + versionSippers.size() + " versionSippers.get(0).batterySipperList.size():" + versionSippers.get(0).sipper_Map_Front.size());
            }

            @Override
            public void onCreateRow(StringBuilder sb) {
                for (String pkgName : allPkgNames.keySet()) {
                    String appName = allPkgNames.get(pkgName).appName;
                    sb.append("<tr>");
                    sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(appName).append("</td>");
                    for (int count_version = 0; count_version < versionSippers.size(); count_version++) {
                        HashMap<String, MyBatterySipper> sipper_map = versionSippers.get(count_version).sipper_Map_Front;
                        MyBatterySipper myBatterySipper = sipper_map.get(pkgName);
                        myBatterySipper = myBatterySipper == null ? PowerHelper.getNABatterSipper() : myBatterySipper;
                        String versionName = myBatterySipper.appInfo.appVersion;
                        double sumPower = PowerHelper.setDoubleScale(myBatterySipper.sumPower);
                        double avtPower = PowerHelper.setDoubleScale(myBatterySipper.sumPower / ((float)myBatterySipper.duration/60));
                        double whole_power = PowerHelper.setDoubleScale(myBatterySipper.wholePower)/((float)myBatterySipper.duration/60);
//                        Util.i("duration=" + myBatterySipper.duration + "sumPower=" + sumPower + "   avtPower=" + avtPower);
                        HashMap<String, MyBatterySipper> sipper_map_back = versionSippers.get(count_version).sipper_Map_Back;
                        MyBatterySipper myBatterySipper_back = sipper_map_back.get(pkgName);
                        myBatterySipper_back = myBatterySipper_back == null ? PowerHelper.getNABatterSipper() : myBatterySipper_back;
                        double sumPower_back = PowerHelper.setDoubleScale(myBatterySipper_back.sumPower);
                        double avgPower_back = PowerHelper.setDoubleScale(myBatterySipper_back.sumPower / ((float)myBatterySipper_back.duration/60));
                        double whole_power_back = PowerHelper.setDoubleScale(myBatterySipper_back.wholePower)/((float)myBatterySipper_back.duration/60);
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append("".equals(versionName) ? "NA" : versionName).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(myBatterySipper.sumPower == Constants.NA ? "NA" : sumPower).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(myBatterySipper.sumPower == Constants.NA ? "NA" : avtPower).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(whole_power).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(myBatterySipper_back.sumPower == Constants.NA ? "NA" : sumPower_back).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(myBatterySipper_back.sumPower == Constants.NA ? "NA" : avgPower_back).append("</td>");
                        sb.append("<td style='background:#BFBFBF;font-weight:bold;color:green'>").append(whole_power_back).append("</td>");
                    }
                    sb.append("</tr>");
                }
            }
        });
    }

    private HashMap<String, AppInfo> getAllPkg(ArrayList<VersionSipper> versionSippers) {
        HashMap<String, AppInfo> allPkg = new HashMap<>();
        for (VersionSipper versionSipper : versionSippers) {
            HashMap<String, MyBatterySipper> sipper_map_front = versionSipper.sipper_Map_Front;
            Set<String> set = sipper_map_front.keySet();
            for (String s : set) {
                if (!allPkg.containsKey(s)) {
                    AppInfo appInfo = sipper_map_front.get(s).appInfo;
                    allPkg.put(s, appInfo);
                }
            }
        }
        return allPkg;
    }

    private String create(HtmlBuilder builder) {
        String s = builder.onCreateTheme();
        if (s != null) {
            Tag = s;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>");
        sb.append("<html><head><title>AutoTestReport</title>");
        sb.append("<style type='text/css'>");
        sb.append("body {");
        sb.append("font:normal 68% verdana,arial,helvetica;");
        sb.append("}");
        sb.append("table tr td, table tr th {");
        sb.append("font-size: 80%;");
        sb.append("}");
        sb.append("table.activity_details tr th{");
        // sb.append("font-weight: bold;");
        sb.append("text-align:left;");
        sb.append("background:#a6caf0;");
        sb.append("}");
        sb.append("table.activity_details tr td{");
        sb.append("background:#BFBFBF;");
        sb.append("}");
        sb.append("p {");
        sb.append("line-height:1.5em;");
        sb.append("margin-top:0.5em; margin-bottom:1.0em;");
        sb.append("}");
        sb.append("h1 {");
        sb.append("margin: 0px 0px 5px; font: bold 165% verdana,arial,helvetica");
        sb.append("}");
        sb.append("h2 {");
        sb.append("margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica");
        sb.append("}");
        sb.append("h3 {");
        sb.append("margin-bottom: 0.5em; font: bold 115% verdana,arial,helvetica");
        sb.append("}");
        sb.append("h4 {");
        sb.append("margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica");
        sb.append("}");
        sb.append("h5 {");
        sb.append("margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica");
        sb.append("}");
        sb.append("h6 {");
        sb.append("margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica");
        sb.append("}");
        sb.append(".Error {");
        sb.append("font-weight:bold; color:purple;");
        sb.append("}");
        sb.append(".Failure {");
        sb.append("font-weight:bold; color:red;");
        sb.append("}");
        sb.append(".TableRowColor {");
        sb.append("font-weight:bold; color:green;");
        sb.append("}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1 align='center'>").append(builder.onCreateTitle()).append("</h1>");
        sb.append("<br>");
        sb.append("<table width='95%'><tr>");
        sb.append("<td align='right'>").append(builder.onCreateAuthor()).append("</td></tr></table>");
        sb.append("<hr align='left' size='1' width='95%'>");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("<h2>测试详情:</h2>");
        builder.onCreateOther(sb);
        sb.append("<table class='activity_details' border='0' cellpadding='5' cellspacing='2' width='95%' align='center'>");
        sb.append("<tr valign='top'>");
        builder.onCreateHeadline(sb);
        builder.onCreateRow(sb);
        sb.append("</table>");
        sb.append("<br/>");
        sb.append("</body></html>");
        return sb.toString();
    }

    public boolean sendMail(String mailAddress,String ccAddress, String htmlContent, String productId) {
        final boolean[] result = {true};
        Log.i(TAG, "开始发送邮件");
        String subjectMsg = "【" + Tag + "】" + " " + productId + "测试报告";
        Mail.Builder mailBuilder = new Mail.Builder()
                .withUsername(USERNAME)
                .withPasscode(PASSWORD)
                .withHost("smtp.gionee.com")
                .withPort("465")
                .isNeedAuth(true)
                .withFrom("zn_software_autotest@gionee.com")
                .withTos(mailAddress)
                .withCcs(ccAddress)
                .withSubject(subjectMsg)
                .withContent(htmlContent)
                .addListener(new MailListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "发送邮件成功");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.i(TAG, "发送邮件失败:" + e.toString());
                        result[0] = false;
                    }
                });
        Mail mail = mailBuilder.build();
        mail.send();
        return result[0];
    }

    public String read(String htmlName) {
        StringBuilder buffer = new StringBuilder();
        File htmlFile = new File(Constants.FILE_PATH + htmlName);
        if (htmlFile.exists()) {
            Log.i(TAG, "当前没有找到html文件");
            return buffer.toString();
        }
        BufferedReader bReader = null;
        InputStreamReader isReader;
        try {
            isReader = new InputStreamReader(new FileInputStream(htmlFile), "UTF-8");
            bReader = new BufferedReader(isReader);
            String tempStr;
            while ((tempStr = bReader.readLine()) != null) {
                buffer.append(tempStr);
            }
            bReader.close();
        } catch (Exception e) {
            Log.i(Tag, e.toString());
        } finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (IOException e1) {
                Log.i(Tag, e1.toString());
            }
        }
        return buffer.toString();
    }

    abstract class HtmlBuilder {

        public String onCreateTheme() {
            return null;
        }

        public abstract String onCreateTitle();

        public abstract String onCreateAuthor();

        public abstract void onCreateOther(StringBuilder sb);

        public abstract void onCreateHeadline(StringBuilder sb);

        public abstract void onCreateRow(StringBuilder sb);

        public String createTextLine(String text) {
            return "<h2>" + text + "</h2>";
        }
    }
}
