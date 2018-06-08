package autotest.gionee.automonkeypowertest.sendmail;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import autotest.gionee.automonkeypowertest.util.Constants;
import autotest.gionee.automonkeypowertest.util.Preference;
import autotest.gionee.automonkeypowertest.util.Util;

class RefreshDefaultEmailTask extends AsyncTask<Void, Void, Boolean> {
    private ISendMailView mISendMailView;
    static boolean isRefreshing = false;

    RefreshDefaultEmailTask(ISendMailView sendMailView) {
        this.mISendMailView = sendMailView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isRefreshing = true;
        RotateAnimation rotateAnimation = new RotateAnimation(0, 270, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(100);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        mISendMailView.getRefreshBtn().setAnimation(rotateAnimation);
        mISendMailView.getRefreshBtn().startAnimation(rotateAnimation);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        long start = System.currentTimeMillis();
        try {
            DefaultEmail defaultAddress = getDefaultAddress();
            String defaultAddress_json = "";
            if (!defaultAddress.isEmpty()) {
                Gson gson = new Gson();
                defaultAddress_json = gson.toJson(defaultAddress);
            }
            Preference.putString(Constants.KEY_ADDRESS_JSON, defaultAddress_json);
            Util.i("defaultAddress_json" + defaultAddress_json);
            long end = System.currentTimeMillis();
            if ((end - start) < 2000) {
                SystemClock.sleep(2000);
            }
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        mISendMailView.getRefreshBtn().clearAnimation();
        isRefreshing = false;
        mISendMailView.showSnackBar("更新默认收件人" + (isSuccess ? "成功!" : "失败！"));
    }

    private String getRemoteEmailContent() {
        HttpURLConnection conn = null;
        InputStream input = null;
        BufferedReader in = null;
        try {
            URL url = new URL("http://db.autotest.gionee.com:8383/job/UpdateEmail_xml/ws/GioneeAutotestSendMail/src/main/resources/email.xml");
            conn = (HttpURLConnection) url.openConnection();
            input = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DefaultEmail getDefaultAddress() {
        String content = getRemoteEmailContent();
        Pattern compile = Pattern.compile("<APP_POWER_TEST>.*?</APP_POWER_TEST>", Pattern.DOTALL);
        Matcher matcher = compile.matcher(content);
        String address_default_appPower = "";
        while (matcher.find()) {
            address_default_appPower = matcher.group();
        }
        Pattern c_address = Pattern.compile("<address>.*?</address>");
        Matcher m_address = c_address.matcher(address_default_appPower);
        ArrayList<String> address_List = new ArrayList<>();
        while (m_address.find()) {
            String group = m_address.group();
            String address = group.replace("<address>", "").replace("</address>", "");
            if (!address_List.contains(address)) {
                address_List.add(address);
            }
        }
        Pattern c_CCAddress = Pattern.compile("<CCaddress>.*?</CCaddress>");
        Matcher m__CCAddress = c_CCAddress.matcher(address_default_appPower);
        ArrayList<String> ccAddress_List = new ArrayList<>();
        while (m__CCAddress.find()) {
            String group = m__CCAddress.group();
            String address = group.replace("<CCaddress>", "").replace("</CCaddress>", "");
            if (!ccAddress_List.contains(address)) {
                ccAddress_List.add(address);
            }
        }
        return new DefaultEmail(address_List, ccAddress_List);
    }

}