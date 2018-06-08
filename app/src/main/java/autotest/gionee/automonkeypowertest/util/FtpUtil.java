package autotest.gionee.automonkeypowertest.util;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FtpUtil {
    private static FTPClient ftp = null;
    private String url = "18.8.1.77";
    private int port = 21;
    private String username = "Anonymous";
    private String password = "";
    public static int uploadReply = 0;


    public FtpUtil(Client client) {
        this.url = client.url;
        this.port = client.port;
        this.username = client.username;
        this.password = client.password;
    }

    public FTPClient getFtp() {
        if (ftp == null) {
            ftp = new FTPClient();
        }
        return ftp;
    }

    public boolean getFtpConnect() throws IOException {
        getFtp();
        int reply;
        ftp.connect(url, port);
        ftp.login(username, password);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        Log.i(Constants.TAG, "登录成功");
        return true;
    }

    public boolean disConnectFtp() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                Util.i(ioe.toString());
            }
        }
        return !ftp.isConnected();
    }

    public boolean isRemoteFileExist(String remotePath, String fileName) {
        if (!isRemotePathExist(remotePath)) {
            return false;
        }
        try {
            FTPFile[] ftpFiles = ftp.listFiles(fileName);
            return ftpFiles.length > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isRemotePathExist(String remotePath) {
        try {
            getFtpConnect();
            return ftp.changeWorkingDirectory(remotePath);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean upload(String path, String fileName, String content) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Util.i(e.toString());
            return false;
        }
        return upload(path, fileName, is);
    }

    public boolean upload(String path, String filename, InputStream input) {
        boolean result = false;
        try {
            result = getFtpConnect();
            boolean isMk = mkdirs(path);
            Util.i("路径创建：" + (isMk ? "成功" : "失败"));
            ftp.changeWorkingDirectory(path);
            result = ftp.storeFile(filename, input);
            uploadReply = ftp.getReplyCode();
            Util.i(uploadReply + ":" + ftp.getReplyString());
            Log.i(Constants.TAG, result ? "上传成功" : "上传失败");
            input.close();
            ftp.logout();
        } catch (IOException e) {
            Util.i(e.toString());
        }
        return result;
    }

    private boolean mkdirs(String path) {
        boolean result = true;
        if (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }
        if (!path.contains("/")) {
            try {
                ftp.makeDirectory(path);
                ftp.changeToParentDirectory();
            } catch (IOException e) {
                result = false;
                Util.i(e.toString());
            }
        } else {
            String[] p = path.split("/");
            for (String s : p) {
                try {
                    ftp.makeDirectory(s);
                    ftp.changeWorkingDirectory(s);
                } catch (IOException e) {
                    Util.i(e.toString());
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public String[] listFileNames(String remotePath) throws IOException {
        getFtpConnect();
        boolean b = ftp.changeWorkingDirectory(remotePath);
        if (!b) {
            return new String[]{};
        }
        FTPFile[] fs = ftp.listFiles();
        if (fs.length == 0) {
            Util.i("无数据");
            return new String[]{};
        }
        String[] strings = new String[fs.length];
        for (int i = 0; i < fs.length; i++) {
            strings[i] = fs[i].getName();
        }
        return strings;
    }


    public ArrayList<String> getRemoteFileContentList(String remotePath, ArrayList<String> mVersionNames) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        getFtpConnect();
        boolean b = ftp.changeWorkingDirectory(remotePath);
        if (!b) {
            return list;
        }
        FTPFile[] fs = ftp.listFiles();
        if (fs.length == 0) {
            Util.i("无数据");
            return list;
        }
        for (FTPFile ftpFile : fs) {
            if (mVersionNames.contains(ftpFile.getName())) {
                OutputStream os = new ByteArrayOutputStream();
                ftp.retrieveFile(ftpFile.getName(), os);
                list.add(os.toString());
                os.close();
            }
        }
        ftp.logout();
        return list;
    }

    public boolean download(String filename, String localPath) {
        try {
            File localFilePath = new File(localPath + "/");
            if (!localFilePath.exists()) {
                localFilePath.mkdirs();
            }
            File localFile = new File(localPath + "/" + filename);
            if (localFile.exists()) {
                return false;
            }
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(filename)) {
                    OutputStream is = new FileOutputStream(localFilePath + "/"
                            + ff.getName());
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                    ftp.logout();
                }
            }
        } catch (Exception e) {
            Util.i(e.toString());
        } finally {
            disConnectFtp();
        }
        return true;
    }

    public static class Client {
        private String url = "18.8.10.110";
        private int port = 21;
        private String username = "autotest";
        private String password = "gionee";

        public Client setUrl(String url) {
            this.url = url;
            return this;
        }

        public Client setPort(int port) {
            this.port = port;
            return this;
        }

        public Client setUsername(String username) {
            this.username = username;
            return this;
        }

        public Client setPassword(String password) {
            this.password = password;
            return this;
        }

        public static Client getChopin() {
            return new Client().setUrl("015.3vftp.com").setPort(21).setPassword("123456").setUsername("chopinsong");
        }

        public static Client get158() {
            return new Client().setUrl("18.8.6.158").setPort(21).setPassword("autotest_ftp").setUsername("888888");
        }
    }
}
