package autotest.gionee.automonkeypowertest.bean;

import java.io.Serializable;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Util;

/**
 * gionee
 * 2018/2/2
 */

public class TestParams implements Serializable {
    public long   testTime     = Constants.DEFAULT_TEST_TIME;
    public long   appWaitTime  = Constants.DEFAULT_WAIT_TIME;
    public long   lastWaitTime = Constants.DEFAULT_LAST_WAIT_TIME;
    public long   cycleCount   = Constants.DEFAULT_CYCLE_COUNT;
    public int    appSize      = 0;
    public int    testType     = Constants.DEFAULT_TEST_TYPE_ID;
    public String softVersion  = Util.getSoftVersion();

    @Override
    public String toString() {
        return "testTime=" + testTime + " appWaitTime=" + appWaitTime + " lastWaitTime=" + lastWaitTime + " cycleCount=" + cycleCount +
                " appSize=" + appSize + " testType=" + testType + " softVersion=" + softVersion;
    }

    public TestParams setTestTime(long testTime) {
        this.testTime = testTime;
        return this;
    }

    public TestParams setAppWaitTime(long appWaitTime) {
        this.appWaitTime = appWaitTime;
        return this;
    }

    public TestParams setLastWaitTime(long lastWaitTime) {
        this.lastWaitTime = lastWaitTime;
        return this;
    }

    public TestParams setCycleCount(long cycleCount) {
        this.cycleCount = cycleCount;
        return this;
    }

    public TestParams setAppSize(int appSize) {
        this.appSize = appSize;
        return this;
    }

    public TestParams setTestType(int testType) {
        this.testType = testType;
        return this;
    }

    public boolean isMonkeyTest() {
        return testType == R.id.monkeyTest;
    }

    public TestParams setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
        return this;
    }
}
