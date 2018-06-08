package autotest.gionee.automonkeypowertest.util.Helper;


import android.app.AlertDialog;
import android.content.Context;

import autotest.gionee.automonkeypowertest.R;

public class DialogHelper {

    public static AlertDialog create(Context context, String title, String msg, OnBeforeCreate before) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setIcon(R.mipmap.logo).setCancelable(false);
        if (msg != null) {
            builder.setMessage(msg);
        }
        if (before != null) {
            before.setOther(builder);
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setWindowAnimations(R.style.dialog_anim);
        return alertDialog;
    }

    public interface OnBeforeCreate {
        void setOther(AlertDialog.Builder builder);
    }
}
