package autotest.gionee.automonkeypowertest.bean;


import java.io.Serializable;

public class VoltageBean implements Serializable{
    public int voltageStart = 0;
    public int voltageEnd   = 0;
    public int voltageAvg   = 0;

    public VoltageBean() {
        this(0,0,0);
    }

    public VoltageBean(int voltageStart, int voltageEnd, int voltageAvg) {
        this.voltageStart = voltageStart;
        this.voltageEnd = voltageEnd;
        this.voltageAvg = voltageAvg;
    }
}
