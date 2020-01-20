package com.xiong.Callback;

public class Remote {
    public void executeMessage(String msg, CallBack callBack) {
        for (int i = 0; i < 1000000000; i++)
            ;
        System.out.println("execute msg:" + msg);
        msg += "---msg change";
        callBack.execute(msg);
    }
}
