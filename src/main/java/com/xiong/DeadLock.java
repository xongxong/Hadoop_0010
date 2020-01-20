package com.xiong;
/*
模拟死锁
 */
public class DeadLock implements Runnable {
    public int flag;
    //静态对象是类的所有对象共享的
    private static Object o1 = new Object(), o2 = new Object();

    public DeadLock(int flag) {
        this.flag = flag;
    }

    public static void main(String[] args) {
        DeadLock dl1 = new DeadLock(0);
        DeadLock dl2 = new DeadLock(1);
        new Thread(dl1).start();
        new Thread(dl2).start();
    }

    @Override
    public void run() {
        System.out.println("flag:" + flag);
        if (flag == 1) {
            synchronized (o1) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o2) {
                    System.out.println("1");
                }
            }
        }
        if (flag == 0) {
            synchronized (o2) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    System.out.println("0");
                }
            }
        }
    }
}
