package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;

/**
 * gionee
 * 2017/12/22
 */

public class SettingsInfo implements Serializable{
    public boolean bugReport;
    public boolean trackball;
    public boolean nav;
    public boolean appSwitch;
    public boolean isStopMusic;
    public boolean disCharge;
    public boolean killAppAfterMonkey;
    public int     digitKeep;
    public int     monkeyThrottle;
    public boolean screenOff;

    public SettingsInfo setBugReport(boolean bugReport) {
        this.bugReport = bugReport;
        return this;
    }

    public SettingsInfo setNav(boolean nav) {
        this.nav = nav;
        return this;
    }

    public SettingsInfo setTrackball(boolean trackball) {
        this.trackball = trackball;
        return this;
    }

    public SettingsInfo setAppSwitch(boolean appSwitch) {
        this.appSwitch = appSwitch;
        return this;
    }

    public SettingsInfo setIsStopMusic(boolean isStopMusic) {
        this.isStopMusic = isStopMusic;
        return this;
    }

    public SettingsInfo setDisCharge(boolean disCharge) {
        this.disCharge = disCharge;
        return this;
    }

    public SettingsInfo setKillAppAfterMonkey(boolean killAppAfterMonkey) {
        this.killAppAfterMonkey = killAppAfterMonkey;
        return this;
    }

    public SettingsInfo setDigitKeep(int digitKeep) {
        this.digitKeep = digitKeep;
        return this;
    }

    public SettingsInfo setMonkeyThrottle(int monkeyThrottle) {
        this.monkeyThrottle = monkeyThrottle;
        return this;
    }

    public SettingsInfo setScreenOff(boolean screenOff) {
        this.screenOff = screenOff;
        return this;
    }
}
