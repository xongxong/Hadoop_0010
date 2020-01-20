package com.xiong;

import java.lang.management.ManagementFactory;

public class JvmTest1 {
    public static void main(String[] args) {
        new Thread(new jvmPara1()).start();
    }
}

class jvmPara1 implements Runnable {

    @Override
    public void run() {
        while (true) {
            String name = "";
            name = ManagementFactory.getRuntimeMXBean().getName();
            System.out.println(name);
            String pid = name.split("@")[0];
            System.out.println("Pid is:" + pid);

        }
    }
}
