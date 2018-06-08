package autotest.gionee.automonkeypowertest.util.Helper;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import autotest.gionee.automonkeypowertest.util.Command;

import static android.content.ContentValues.TAG;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

public class WakeHelper {
    private PowerManager mPm;
    private KeyguardManager mKeyguardManager;
    private PowerManager.WakeLock mScreenOnWakeLock;
    private static KeyguardManager.KeyguardLock mKeyguardLock;

    public WakeHelper(Context context) {
        mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mPm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mScreenOnWakeLock = mPm.newWakeLock(PARTIAL_WAKE_LOCK, TAG);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("disableKeyguard");
    }

    public void disableKeyguard() {
        mKeyguardLock.disableKeyguard();
    }

    public PowerManager.WakeLock getLock() {
        return mScreenOnWakeLock;
    }

    public boolean isLock() {
        return mKeyguardManager.isKeyguardLocked();
    }

    public boolean isScreenOn() {
        return mPm.isInteractive();
    }

    public void reenableKeyguard() {
        mKeyguardLock.reenableKeyguard();
    }

    public void lock() {
        if (isScreenOn())
            Command.execCommand("input keyevent 26", false);
    }
    public void unLock(){
        if (!isScreenOn()){
            Command.execCommand("input keyevent 26", false);
        }
    }
}
