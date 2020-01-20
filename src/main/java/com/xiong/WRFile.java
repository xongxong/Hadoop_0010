package com.xiong;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/*
需求：两个线程写一个TXT文件，线程1：I love you 线程2：so do I 。保证线程1、2有序执行，一句I love you，对应一句so do I。
 */
public class WRFile {
    boolean flag;

    public synchronized void read1() {
        if (this.flag) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("love.txt", "rw");
            file.seek(file.length());
            file.writeBytes("I Love you");
            file.writeBytes("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.flag = true;
        this.notify();
    }

    public synchronized void read2() {
        if (!this.flag) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile("love.txt", "rw");
            file.seek(file.length());
            file.writeBytes("so do I");
            file.writeBytes("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.flag = false;
        this.notify();
    }

    public static void main() {
        WRFile wr = new WRFile();
        FirstThread ft = new FirstThread(wr);
        SecondThread st = new SecondThread(wr);

        Thread t1 = new Thread(ft);
        Thread t2 = new Thread(st);
        t1.start();
        t2.start();
    }

    public static Map<String, String> analysis1(String sql, Set<String> set) {
        String[] strs = sql.replaceAll("\n", " ").replaceAll(" as ", " ").replaceAll(",", " ").toLowerCase().split("\\s+");
        Set<String> dataPool = new HashSet<>(Arrays.asList(new String[]{"select", "where", "join", "inner", "left", "right", "full", "on"}));
        Map<String, String> map = new HashMap<>();
        int count = 0, len = strs.length;
        for (int i = 0; i < len - 1; i++)
            if (set.contains(strs[i]) && !set.contains(strs[i + 1]) && !dataPool.contains(strs[i + 1]))
                map.put(strs[i + 1], strs[i]);
            else if (set.contains(strs[i]) && (set.contains(strs[i + 1]) || dataPool.contains(strs[i + 1]))) {
                map.put("" + count, strs[i]);
                count++;
            }
        if (set.contains(strs[len - 1]))
            map.put("" + count, strs[len - 1]);
        return map;
    }

    public static void main(String[] args) {

    }

}

class FirstThread implements Runnable {
    private WRFile wrFile = new WRFile();

    public FirstThread(WRFile wrFile) {
        this.wrFile = wrFile;
    }

    @Override
    public void run() {
        while (true) {
            wrFile.read1();
        }
    }
}

class SecondThread implements Runnable {
    private WRFile wrFile = new WRFile();

    public SecondThread(WRFile wrFile) {
        this.wrFile = wrFile;
    }

    @Override
    public void run() {
        while (true) {
            wrFile.read2();
        }
    }
}