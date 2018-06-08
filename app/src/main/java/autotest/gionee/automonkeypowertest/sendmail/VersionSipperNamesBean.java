package autotest.gionee.automonkeypowertest.sendmail;


import autotest.gionee.automonkeypowertest.util.sqlite.RemoteDBUtil;

class VersionSipperNamesBean {
   RemoteDBUtil.RemoteVersionName remoteVersionName;
    boolean isChecked = true;

    VersionSipperNamesBean(RemoteDBUtil.RemoteVersionName remoteVersionName, boolean isChecked) {
        this.remoteVersionName = remoteVersionName;
        this.isChecked = isChecked;
    }
}