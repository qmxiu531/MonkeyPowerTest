package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;

/**
 * gionee
 * 2018/2/28
 */

public class PowerInfoResult implements Serializable{
    private String power;
    private String time;
    private String name;
    private String pakName;

    public PowerInfoResult(String power, String time, String name, String pakName) {
        this.power = power;
        this.time = time;
        this.name = name;
        this.pakName = pakName;
    }

    public String getPower() {
        return power;
    }

    public PowerInfoResult setPower(String power) {
        this.power = power;
        return this;
    }

    public String getTime() {
        return time;
    }

    public PowerInfoResult setTime(String time) {
        this.time = time;
        return this;
    }

    public String getName() {
        return name;
    }

    public PowerInfoResult setName(String name) {
        this.name = name;
        return this;
    }

    public String getPakName() {
        return pakName;
    }

    public PowerInfoResult setPakName(String pakName) {
        this.pakName = pakName;
        return this;
    }
}
