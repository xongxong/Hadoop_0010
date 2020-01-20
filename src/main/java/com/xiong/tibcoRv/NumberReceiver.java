package com.xiong.tibcoRv;

import com.tibco.tibrv.Tibrv;
import com.tibco.tibrv.TibrvException;
import com.tibco.tibrv.TibrvFtMember;
import com.tibco.tibrv.TibrvFtMemberCallback;
import com.tibco.tibrv.TibrvListener;
import com.tibco.tibrv.TibrvMsg;
import com.tibco.tibrv.TibrvMsgCallback;
import com.tibco.tibrv.TibrvRvdTransport;

public class NumberReceiver implements TibrvMsgCallback, TibrvFtMemberCallback, Runnable {
    private String service = "7500";
    private String network = ";225.1.1.1";
    private String daemon = "tcp:7500";
    private String subject = "DEMO.FT.NUM";


    private String ftservice = "7504";
    private String ftnetwork = ";225.1.10.1";
    private String ftdaemon = "tcp:7504";
    private String ftgroupName = "DEMO.FT.GROUP";
    private int ftweight = 50;
    private int activeGoalNum = 1;
    private double hbInterval = 1.5;
    private double prepareInterval = 3;
    private double activateInterval = 4.8;
    private TibrvRvdTransport transport = null;
    private TibrvListener listener = null;

    private boolean active = false;


    @Override
    public void onFtAction(TibrvFtMember tibrvFtMember, String s, int action) {
        if (action == TibrvFtMember.PREPARE_TO_ACTIVATE) {
            System.out.println("TibrvFtMember.PREPARE_TO_ACTIVATE invoked...");
            System.out.println("*** PREPARE TO ACTIVATE: " + ftgroupName);
        } else if (action == TibrvFtMember.ACTIVATE) {
            System.out.println("TibrvFtMember.ACTIVATE invoked...");
            System.out.println("*** ACTIVATE: " + ftgroupName);
            enableListener();
            active = true;
        } else if (action == TibrvFtMember.DEACTIVATE) {
            System.out.println("TibrvFtMember.DEACTIVATE invoked...");
            System.out.println("*** DEACTIVATE: " + ftgroupName);
            disableListener();
            active = false;
        }
    }


    @Override
    public void run() {
        try {
            Tibrv.open(Tibrv.IMPL_NATIVE);
            transport = new TibrvRvdTransport(service, network, daemon);
            TibrvRvdTransport fttransport = new TibrvRvdTransport(ftservice, ftnetwork, ftdaemon);
            fttransport.setDescription("fault tolerance");

            new TibrvFtMember(Tibrv.defaultQueue(), // TibrvQueue
                    this,                 // TibrvFtMemberCallback
                    fttransport,          // TibrvTransport
                    ftgroupName,          // groupName
                    ftweight,             // weight
                    activeGoalNum,        // activeGoal
                    hbInterval,           // heartbeatInterval
                    prepareInterval,      // preparationInterval,
                    // Zero is a special value,
                    // indicating that the member does
                    // not need advance warning to activate
                    activateInterval,     // activationInterval
                    null);                // closure


            while (true) {
                try {
                    Tibrv.defaultQueue().dispatch();
                } catch (TibrvException e) {
                    System.err.println("Exception dispatching default queue:");
                    System.exit(0);
                } catch (InterruptedException ie) {
                    System.exit(0);
                }
            }
        } catch (TibrvException e) {
            e.printStackTrace();
        }
    }

    void enableListener() {
        try {
            // Subscribe to subject
            listener = new TibrvListener(Tibrv.defaultQueue(),
                    this,
                    transport,
                    subject,
                    null);
            System.out.println("Start Listening on: " + subject);
        } catch (TibrvException e) {
            System.err.println("Failed to create subject listener:");
            System.exit(0);
        }
    }

    void disableListener() {
        listener.destroy();
        System.out.println("Destroy Listener on Subject: " + subject);
    }

    @Override
    public void onMsg(TibrvListener tibrvListener, TibrvMsg tibrvMsg) {
        if (subject.equals(listener.getSubject())) {
            try {
                int num = tibrvMsg.getAsInt("number", 0);
                System.out.println("number: " + num);

            } catch (TibrvException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NumberReceiver rcv = new NumberReceiver();
        Thread tRcv = new Thread(rcv);
        tRcv.start();
        tRcv.join();
        System.out.println("stop");
    }
}
