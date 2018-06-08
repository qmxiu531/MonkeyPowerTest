package autotest.gionee.automonkeypowertest.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import autotest.gionee.automonkeypowertest.R;


class SelectAppDialog extends AlertDialog.Builder {

    private ArrayList<String> appNames;
    private boolean[]         selectItem;
    private boolean[] isSelectAll = {false};

    SelectAppDialog(Context context, ArrayList<String> appNames, boolean[] selectItem, final OnSelectAppListener selectAppListener) {
        super(context);
        this.appNames = appNames;
        this.selectItem = selectItem;
        this.setTitle("选择测试应用");
        this.setIcon(R.mipmap.logo);
        this.setCancelable(false);
        this.setMultiChoiceItems(appNames.toArray(new CharSequence[]{}), selectItem, selectAppListener::onSelectItem);
        this.setPositiveButton("保存", (dialog, which) -> selectAppListener.onSave(getSelectApps()));
        this.setNeutralButton("全选", null);
        this.setNegativeButton("取消", (dialog, which) -> selectAppListener.onCancel());
        this.setOnDismissListener(dialog -> selectAppListener.onDismiss());
    }

    @NonNull
    private ArrayList<String> getSelectApps() {
        ArrayList<String> selectApps = new ArrayList<>();
        for (int i = 0; i < appNames.size(); i++) {
            if (selectItem[i]) {
                selectApps.add(appNames.get(i));
            }
        }
        return selectApps;
    }

    @Override
    public AlertDialog show() {
        final AlertDialog show = super.show();
        show.getWindow().setWindowAnimations(R.style.dialog_anim);
        final Button button = show.getButton(DialogInterface.BUTTON_NEUTRAL);
        isSelectAll[0] = isSelectAll();
        button.setText(isSelectAll[0] ? "全不选" : "全选");
        button.setOnClickListener(v -> {
            isSelectAll[0] = !isSelectAll[0];
            button.setText(isSelectAll[0] ? "全不选" : "全选");
            ListView listView = show.getListView();
            if (!isSelectAll[0]) {
                Arrays.fill(selectItem, false);
                listView.clearChoices();
                listView.setSelection(0);
            } else {
                Arrays.fill(selectItem, true);
                listView.setSelection(0);
            }
        });
        return show;
    }

    private boolean isSelectAll() {
        for (boolean b : selectItem) {
            if (!b)
                return false;
        }
        return true;
    }

    abstract static class OnSelectAppListener {
        void onCancel() {

        }

        public abstract void onSave(ArrayList<String> selectApps);

        void onDismiss() {

        }

        void onSelectItem(DialogInterface dialog, int which, boolean isChecked) {

        }
    }
}
