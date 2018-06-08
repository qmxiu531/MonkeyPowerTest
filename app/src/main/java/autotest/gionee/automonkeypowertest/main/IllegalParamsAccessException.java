package autotest.gionee.automonkeypowertest.main;

import android.view.View;

/**
 * gionee
 * 2018/3/28
 */

public class IllegalParamsAccessException extends IllegalAccessException {
    private final View view;

    public IllegalParamsAccessException(View view, String e) {
        super(e);
        this.view = view;
    }

    public View getView() {
        return view;
    }
}
