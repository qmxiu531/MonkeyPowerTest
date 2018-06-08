package autotest.gionee.automonkeypowertest.dialog;


import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.Window;

import autotest.gionee.automonkeypowertest.R;
import autotest.gionee.automonkeypowertest.bean.SettingsInfo;
import autotest.gionee.automonkeypowertest.databinding.DialogSettingsBinding;
import autotest.gionee.automonkeypowertest.util.Configurator;

public class SettingsDialog extends AlertDialog.Builder {
    private SettingsInfo          info;
    private DialogSettingsBinding binding;

    public SettingsDialog(Context context, SettingsInfo info) {
        super(context);
        this.setIcon(R.mipmap.logo);
        this.setTitle(context.getString(R.string.settings));
        this.setCancelable(false);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_settings, null, false);
        this.setView(binding.getRoot());
        this.setPositiveButton("保存", (dialog, which) -> save());
        this.setNegativeButton("取消", null);
        this.info = info;
    }

    @Override
    public AlertDialog show() {
        AlertDialog show = super.show();
        try {
            Window window = show.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.dialog_anim);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        return show;
    }

    private void initView() {
        if (info == null) {
            info = Configurator.getSettings();
        }
        binding.numberPicker.setMaxValue(16);
        binding.numberPicker.setValue(info.digitKeep);
        binding.throttleMonkey.setText(String.valueOf(info.monkeyThrottle));
        binding.bugReport.setChecked(info.bugReport);
        binding.nav.setChecked(info.nav);
        binding.trackball.setChecked(info.trackball);
        binding.appSwitch.setChecked(info.appSwitch);
        binding.isStopMusic.setChecked(info.isStopMusic);
        binding.disCharge.setChecked(info.disCharge);
        binding.killAppAfterMonkey.setChecked(info.killAppAfterMonkey);
        binding.screenOff.setChecked(info.screenOff);
    }

    private void save() {
        int value = binding.numberPicker.getValue();
        SettingsInfo saveInfo = new SettingsInfo().setDigitKeep(value < 0 ? 0 : (value > 16 ? 16 : value))
                .setBugReport(binding.bugReport.isChecked())
                .setNav(binding.nav.isChecked())
                .setTrackball(binding.trackball.isChecked())
                .setAppSwitch(binding.appSwitch.isChecked())
                .setIsStopMusic(binding.isStopMusic.isChecked())
                .setDisCharge(binding.disCharge.isChecked())
                .setKillAppAfterMonkey(binding.killAppAfterMonkey.isChecked())
                .setScreenOff(binding.screenOff.isChecked());
        String throttle = this.binding.throttleMonkey.getText().toString();
        if (!"".equals(throttle)) {
            saveInfo.setMonkeyThrottle(Integer.parseInt(throttle));
        }
        Configurator.setSettingsInfo(saveInfo);
    }

}
