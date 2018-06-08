package autotest.gionee.automonkeypowertest.dialog;

import android.os.AsyncTask;

import autotest.gionee.automonkeypowertest.bean.SettingsInfo;
import autotest.gionee.automonkeypowertest.util.Configurator;

public class SettingsObtainer extends AsyncTask<Void, Void, SettingsInfo> {

    @Override
    protected SettingsInfo doInBackground(Void... voids) {
        return Configurator.getSettings();
    }
}
