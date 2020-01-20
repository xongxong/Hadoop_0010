package com.xiong.Callback;

public class Local implements CallBack, Runnable {

    public Remote remote;
    public String message;
    public Local() {
    }
    public Local(Remote remote,String message){
        this.remote = remote;
        this.message = message;
    }
    public void sendMessage(){
        System.out.println(Thread.currentThread().getName()+" current thread");
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        remote.executeMessage(message, this);
    }
    @Override
    public void execute(Object... objects) {
        System.out.println(objects[0]);
        System.out.println(Thread.currentThread().getName() + " current thread");
        System.out.println("done!");
    }
    public static void main(String[] args) {
        Local local = new Local(new Remote(), "hello World");
        local.sendMessage();
        System.out.println("gogogo...");
    }
}
