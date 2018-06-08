package autotest.gionee.automonkeypowertest.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import autotest.gionee.automonkeypowertest.util.Helper.WholePowerHelper;


public class WholePowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("gionee.autotest.automonkeypowertest.getCoulomb_count")) {
            int append = intent.getIntExtra("append", 0);
            Util.i("写入coulomb_count");
            new Thread(new WriteThread(append == 1)).start();
        }
    }

    private class WriteThread implements Runnable {

        private boolean append;

        WriteThread(boolean append) {
            this.append = append;
        }

        @Override
        public void run() {
            FileWriter fileWriter = null;
            try {
                double coulomb_count = WholePowerHelper.getCoulomb_count();
                Util.i(coulomb_count + "coulomb_count");
                File file = new File(Constants.FILE_PATH + "coulomb_count");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fileWriter = new FileWriter(file, append);
                fileWriter.write((append?"\n":"")+coulomb_count );
                fileWriter.close();
            } catch (Exception e) {
                Util.i(e.toString());
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        Util.i(e.toString());
                    }
                }
            }
        }
    }
}
