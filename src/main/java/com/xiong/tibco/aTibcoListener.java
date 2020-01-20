package com.xiong.tibco;

import org.apache.log4j.Logger;


import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvDispatcher;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvQueue;
import com.tibco.tibrv.TibrvRvdTransport;
import com.tibco.tibrv.TibrvTransport;

public class aTibcoListener implements TibrvMsgCallback {
    private TibrvRvdTransport transport = null;

    //初始化TibrvListener
    public aTibcoListener() {
        try {
            Tibrv.open(Tibrv.IMPL_NATIVE);
            transport = new TibrvRvdTransport(
                    ConfigUtil.getValue("TIBCO_SERVICE"),
                    ConfigUtil.getValue("TIBCO_NETWORK"),
                    ConfigUtil.getValue("TIBCO_DAEMON"));
            new TibrvListener(Tibrv.defaultQueue(), this, transport, ConfigUtil.getValue("TIBCO_SEND_SUBJECT"), null);
        } catch (TibrvException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMsg(TibrvListener tibrvListener, TibrvMsg tibrvMsg) {
        if (tibrvMsg != null) {
            String receivedMsg;
            try {
                receivedMsg = (String) tibrvMsg.get("msg");
                System.out.println(receivedMsg);
            } catch (TibrvException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new aTibcoListener();
    }
}
