package autotest.gionee.automonkeypowertest.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utility class for shell command execution ,this class need android.uid.shell
 * <p>
 * Author Viking Den <dengwj@gionee.com>
 * Version 1.0
 * Time 2016/8/8 0008 15:46
 */
public class ShellUtil {
    private final static Uri    CONTENT_URI      = Uri.parse("content://com.amigo.settings.RosterProvider/rosters");
    public static final  String COMMAND_SU       = "amigosu";
    //            public static final String COMMAND_SU       = "su";
    public static final  String COMMAND_SH       = "sh";
    public static final  String COMMAND_EXIT     = "exit\n";
    public static final  String COMMAND_LINE_END = "\n";

    private ShellUtil() {
        throw new AssertionError();
    }

    /**
     * kill special process id by shell
     *
     * @param pid process id to kill
     */
    public static void killProcess(int pid) {
        CommandResult result = ShellUtil.execCommand("kill -9 " + pid, false);
        Log.i("killProcess result : " + result.result);
        Log.i("killProcess successMsg : " + result.successMsg);
        Log.i("killProcess errorMsg : " + result.errorMsg);
    }

    /**
     * kill uiautomator process if exist
     */
    public static void killUiAutomator() {
        Log.i("enter killUiAutomator");
        int pUiautomatorId = dumpUiautomatorId();
        if (pUiautomatorId == -1) {
            Log.e("pUiautomatorId = -1 , uiautomator process is not exist");
            return;
        }
        killProcess(pUiautomatorId);
    }

    public static void killProcess(String filterStr) {
        int id = dumpPid(filterStr);
        Util.i("process id = " + id);
        if (id == -1) {
            Log.e("id = -1 , filterStr process is not exist");
            return;
        }
        killProcess(id);
    }

    /**
     * dump uiautomator id
     *
     * @return uiautomator id value
     */
    private static int dumpUiautomatorId() {
        int pId = -1;
        try {
            //root      3815  485   1884972 51760 futex_wait 0000000000 S uiautomator
            CommandResult result = ShellUtil.execCommand("ps |grep uiautomator", false);
            if (result.result == 0 && result.successMsg != null && !result.successMsg.equals("")) {
                String   line  = result.successMsg;
                String[] args  = line.split(" ");
                int      arg_2 = 1;
                for (String arg : args) {
                    if (!arg.equals("") && arg_2 == 2) {
                        return Integer.parseInt(arg);
                    }
                    if (!arg.equals("")) {
                        arg_2++;
                    }
                }
            }
            Log.i("dumpUiautomatorId result : " + result.result);
            Log.i("dumpUiautomatorId successMsg : " + result.successMsg);
            Log.i("dumpUiautomatorId errorMsg : " + result.errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pId;
    }

    public static int dumpPid(String filterStr) {
        int pId = -1;
        try {
            //root      3815  485   1884972 51760 futex_wait 0000000000 S uiautomator
            CommandResult result = ShellUtil.execCommand("ps |grep " + filterStr, false);
            if (result.result == 0 && result.successMsg != null && !result.successMsg.equals("")) {
                String   line  = result.successMsg;
                String[] args  = line.split(" ");
                int      arg_2 = 1;
                for (String arg : args) {
                    if (!arg.equals("") && arg_2 == 2) {
                        return Integer.parseInt(arg);
                    }
                    if (!arg.equals("")) {
                        arg_2++;
                    }
                }
            }
            Log.i("dumpId result : " + result.result);
            Log.i("dumpId successMsg : " + result.successMsg);
            Log.i("dumpId errorMsg : " + result.errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pId;
    }

    /**
     * obtain root when device is eng version
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public static void requireRoot(Context context) {
        ContentValues values = new ContentValues();
        values.put("usertype", "root");
        values.put("packagename", context.getPackageName());
        values.put("status", 1);
        context.getContentResolver().insert(CONTENT_URI, values);
    }

    public static void indexRoot(Context context) {
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String usertype    = cursor.getString(cursor.getColumnIndex("usertype"));
                String packagename = cursor.getString(cursor.getColumnIndex("packagename"));
                String status      = cursor.getString(cursor.getColumnIndex("status"));
                Log.i("*********************************************************");
                Log.i("usertype : " + usertype);
                Log.i("packagename : " + packagename);
                Log.i("status : " + status);
            }
        }
    }

    /**
     * check current device has been rooted or not
     *
     * @return if rooted , return true ; otherwise , return false
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * execute a single line command
     *
     * @param command command to execute
     * @param isRoot  command need root or not
     * @return return execute result
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     * execute a single line command
     *
     * @param commands a list of command to execute
     * @param isRoot   command need root or not
     * @return return execute result
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, true);
    }

    /**
     * execute a single line command
     *
     * @param commands a list of command to execute
     * @param isRoot   command need root or not
     * @return return execute result
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute a single line command
     *
     * @param command         command to execute
     * @param isRoot          command need root or not
     * @param isNeedResultMsg need execution result message
     * @return return execute result
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute a single line command
     *
     * @param commands        a list of command to execute
     * @param isRoot          command need root or not
     * @param isNeedResultMsg need execution result message
     * @return return execute result
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot, isNeedResultMsg);
    }

    /**
     * execute a single line command
     *
     * @param commands        a list of command to execute
     * @param isRoot          command need root or not
     * @param isNeedResultMsg need execution result message
     * @return return execute result
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process        process       = null;
        BufferedReader successResult = null;
        BufferedReader errorResult   = null;
        StringBuilder  successMsg    = null;
        StringBuilder  errorMsg      = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? "" : successMsg.toString(), errorMsg == null ? ""
                : errorMsg.toString());
    }

    public static class CommandResult {

        /**
         * result of command
         **/
        public int    result;
        /**
         * success message of command result
         **/
        public String successMsg;
        /**
         * error message of command result
         **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "reuslt=" + result + " successMsg=" + successMsg + " errorMsg=" + errorMsg;
        }
    }

    public static boolean returnResult(int value) {
        if (value == 0) {//success
            return true;
        } else { // fail or unknown
            return false;
        }
    }
}
