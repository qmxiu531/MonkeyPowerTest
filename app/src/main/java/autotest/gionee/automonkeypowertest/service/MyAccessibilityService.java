package autotest.gionee.automonkeypowertest.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import autotest.gionee.automonkeypowertest.util.Configurator;
import autotest.gionee.automonkeypowertest.util.Util;
import gionee.autotest.UI;
import gionee.autotest.UINotFoundException;
import gionee.autotest.UIO;


public class MyAccessibilityService extends AccessibilityService {
    String[] content = {"确定", "继续", "确认", "同意", "允许", "开始体验", "跳过", "关闭", "去听歌", "知道了"};

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getSource() == null || !Configurator.isMonitorPermit) {
            return;
        }
        UIO text = UI.findByText(event, -1, content);
        if (text.exist()) {
            try {
                text.click();
            } catch (UINotFoundException e) {
                Util.i(e.toString());
            }
        }
    }

    @Override
    public void onInterrupt() {
    }


}
