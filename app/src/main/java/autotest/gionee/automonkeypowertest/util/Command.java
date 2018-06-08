package autotest.gionee.automonkeypowertest.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 执行shell脚本工具类
 *
 * @author Mountain
 */
public class Command {
    public final static String COMMAND_SU = "su";
    public final static String COMMAND_SH = "sh";
    public final static String COMMAND_EXIT = "exit\n";
    public final static String COMMAND_LINE_END = "\n";
    public static Process process = null;

    /**
     * Command执行结果
     *
     * @author Mountain
     */
    public static class CommandResult {
        public static int result = -1;
        public static String errorMsg;
        public static String successMsg;
    }


    /**
     * 执行命令—单条
     *
     * @param command
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = {command};
        return execCommand(commands, isRoot);
    }

    /**
     * 执行命令-多条
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0)
            return commandResult;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg;
        StringBuilder errorMsg;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            //获取错误信息
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) successMsg.append(s);
            while ((s = errorResult.readLine()) != null) errorMsg.append(s);
            commandResult.successMsg = successMsg.toString();
            commandResult.errorMsg = errorMsg.toString();
            Util.i("successMsg:" + commandResult.successMsg + " | " + "errorMsg:" + commandResult.errorMsg);
        } catch (Exception e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(Constants.TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } finally {
            close(os, successResult, errorResult);
            if (process != null) process.destroy();
        }
        return commandResult;
    }

    private static void close(Closeable... close) {
        for (int i = 0; i < close.length; i++) {
            try {
                if (close[i] != null) {
                    close[i].close();
                    close[i] = null;
                }
            } catch (IOException e) {
                String errMsg = e.getMessage();
                if (errMsg != null) {
                    Log.e(Constants.TAG, errMsg);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void stopCommand() {
        if (process != null) {
            OutputStream os = process.getOutputStream();
            try {
                os.write(Byte.valueOf("CTRL+C"));
                os.write(Byte.valueOf("exit"));
                os.flush();
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
