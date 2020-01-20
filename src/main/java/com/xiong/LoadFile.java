package com.xiong;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class LoadFile {


    public static void downloadFile(String remoteFilePath, String localFilePath) {
        URL urlfile = null;
        HttpsURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        try {
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpsURLConnection) urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        downloadFile("https://www.jianshu.com/p/048888737857", "C:\\Users\\HJ18031701\\Desktop\\file\\xiongxiong.md");
    }
}
