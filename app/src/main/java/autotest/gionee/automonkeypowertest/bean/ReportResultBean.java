package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * gionee
 * 2018/2/2
 */

public class ReportResultBean implements Serializable{

    public ArrayList<ReportBean> reportBeans = new ArrayList<>();
    public TestParams params;
    public ArrayList<PowerInfoResult> highPowerResults;

    public ReportResultBean() {

    }

    public ReportResultBean setReportBeans(ArrayList<ReportBean> reportBeans) {
        this.reportBeans = reportBeans;
        return this;
    }

    public ReportResultBean setParams(TestParams params) {
        this.params = params;
        return this;
    }

    public ReportResultBean setHighPowerResults(ArrayList<PowerInfoResult> highPowerResults) {
        this.highPowerResults = highPowerResults;
        return this;
    }
}
