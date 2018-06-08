package autotest.gionee.automonkeypowertest.util;

import android.app.Application;
import android.content.Context;

import autotest.gionee.automonkeypowertest.util.sqlite.DBManager;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Preference.init(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            DBManager.init(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
