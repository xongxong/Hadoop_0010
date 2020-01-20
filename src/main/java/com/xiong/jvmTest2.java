package com.xiong;

import java.lang.management.ManagementFactory;

public class jvmTest2 {
    public static void main(String[] args) {
        new Thread(new jvmPara2()).start();
    }
}

class jvmPara2 implements Runnable {

    @Override
    public void run() {
        while (true) {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            System.out.println(name);
            String pid = name.split("@")[0];
            System.out.println("Pid is:" + pid);

        }
    }
}
