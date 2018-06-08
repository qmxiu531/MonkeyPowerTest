package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;

/**
 * Created by xhk on 2018/1/29.
 */

public class PowerInfoObj implements Serializable {
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 开始功耗
     */
    private double startPower;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 结束功耗
     */
    private double endPower;
    /**
     * 应用包名
     */
    private String pakName;

    public PowerInfoObj(long startTime, double startPower, long endTime, double endPower, String pakName) {
        this.startTime = startTime;
        this.startPower = startPower;
        this.endTime = endTime;
        this.endPower = endPower;
        this.pakName = pakName;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getStartPower() {
        return startPower;
    }

    public void setStartPower(double startPower) {
        this.startPower = startPower;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getEndPower() {
        return endPower;
    }

    public void setEndPower(double endPower) {
        this.endPower = endPower;
    }

    public String getPakName() {
        return pakName;
    }

    public void setPakName(String pakName) {
        this.pakName = pakName;
    }
}
