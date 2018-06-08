package autotest.gionee.automonkeypowertest.sendmail;


import com.google.gson.Gson;

import java.util.ArrayList;

import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Helper.AnimationHelper;
import autotest.gionee.automonkeypowertest.util.Helper.DialogHelper;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;

class SendMailPresenter {

    private ISendMailView mISendMailView;

    SendMailPresenter(ISendMailView iSendMailView) {
        this.mISendMailView = iSendMailView;
    }

    void clearReceiverEmails() {
        if ("".equals(mISendMailView.getAddress().getText().toString())) {
            mISendMailView.getAddress().startAnimation(AnimationHelper.shake());
            mISendMailView.getClearBtn().startAnimation(AnimationHelper.shake());
        }
        Preference.remove(Constants.KEY_RECEIVER_EMAILS);
        mISendMailView.getAddress().setText("");
    }

    void handlerSendBtn() {
        try {
            Util.hideMM(mISendMailView.getContext(), mISendMailView.getAddress());
            saveEmailAddressee();
        } catch (Exception e) {
            mISendMailView.getAddress().startAnimation(AnimationHelper.shake());
            mISendMailView.getSendMailBtn().startAnimation(AnimationHelper.shake());
            mISendMailView.showSnackBar(e.getMessage());
            return;
        }
        ArrayList<VersionSipperNamesBean> data = mISendMailView.getListViewData();
        if (data.size() == 0) {
            mISendMailView.showSnackBar("无数据");
            return;
        }
        ArrayList<Integer> ids = new ArrayList<>();
        for (VersionSipperNamesBean bean : data) {
            if (bean.isChecked) {
                ids.add(bean.remoteVersionName.id);
            }
        }
        if (ids.size() == 0) {
            mISendMailView.showSnackBar("请选择一个版本数据");
            return;
        }
        new SendMailDialog(mISendMailView.getContext(), ids).show();
    }

    private void saveEmailAddressee() {
        String newEmail = mISendMailView.getAddress().getText().toString();
        if (errorEmailAddress(newEmail)) {
            throw new IllegalStateException("请输入正确的邮件号码");
        } else if (!Preference.getBoolean(Constants.KEY_IS_SEND_DEFAULT_EMAIL)) {
            String[] split = newEmail.contains(";") ? newEmail.split(";") : new String[]{newEmail};
            for (String s : split) {
                if (errorEmailAddressFormat(s)) {
                    throw new IllegalStateException("邮箱格式不正确，请输入正确的邮件格式");
                }
            }
        }
        Preference.putString(Constants.KEY_RECEIVER_EMAILS, newEmail);
    }

    private boolean errorEmailAddress(String newEmail) {
        if (newEmail.startsWith(";") || newEmail.startsWith("@") || newEmail.endsWith("@") || newEmail.startsWith(".")) {
            return true;
        } else if ("".equals(newEmail)) {
            return !Preference.getBoolean(Constants.KEY_IS_SEND_DEFAULT_EMAIL) || "".equals(Preference.getString(Constants.KEY_ADDRESS_JSON, Constants.DEFAULT_ADDRESS_JSON));
        }
        return false;
    }

    private boolean errorEmailAddressFormat(String s) {
        return !s.matches("^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$");
    }


    void showDefaultEmailInfoDialog() {
        final String defaultAddress_json = Preference.getString(Constants.KEY_ADDRESS_JSON, Constants.DEFAULT_ADDRESS_JSON);
        if (!"".equals(defaultAddress_json)) {
            DialogHelper.create(mISendMailView.getContext(), "默认收件人", null, builder -> {
                builder.setCancelable(true);
                Gson gson = new Gson();
                DefaultEmail defaultEmail = gson.fromJson(defaultAddress_json, DefaultEmail.class);
                ArrayList<String> strings = new ArrayList<>();
                strings.addAll(defaultEmail.address_List);
                for (String s : defaultEmail.ccAddress_List) {
                    strings.add(s + "(抄送)");
                }
                builder.setItems(strings.toArray(new String[]{}), null);
            }).show();
        } else {
            DialogHelper.create(mISendMailView.getContext(), "默认收件人", "没有默认收件人", builder -> {
                builder.setCancelable(true);
                builder.setPositiveButton("确定", null);
            }).show();
        }
    }

    void refreshDefaultEmail() {
        if (!RefreshDefaultEmailTask.isRefreshing) {
            new RefreshDefaultEmailTask(mISendMailView).execute();
        }
    }


    void checkRemoteVersions() {
        if (CheckRemoteVersionsTask.isChecked) {
            new CheckRemoteVersionsTask(mISendMailView).execute();
        }
    }

}
