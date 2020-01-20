package com.xiong.tibco;
/*
 * Copyright (c) 1998-2003 TIBCO Software Inc.
 * All rights reserved.
 * TIB/Rendezvous is protected under US Patent No. 5,187,787.
 * For more information, please contact:
 * TIBCO Software Inc., Palo Alto, California, USA
 *
 * @(#)tibrvserver.java 1.7
 */


/*
 * tibrvserver - TIB/Rendezvous server program
 *
 * This program will answer trivial request from tibrvclient
 *
 * This server example uses a transport enabled for direct communication
 * by default.  If the client also uses an enabled transport, and the
 * network path does not cross through RVRDs, the resulting requests and
 * replies will use direct communication instead of passing through
 * Rendezvous daemons.
 *
 * Optionally the user may specify communication parameters for
 * tibrvTransport_Create, and a status display frequency value.  If none
 * are specified, default values are used.  For information on default
 * values for these parameters, please see the TIBCO/Rendezvous Concepts
 * manual.
 *
 * The following non-standard defaults are used in this sample program:
 *   service         "7522:7523"    service for search & client requests
 *   status          0              optional frequency of status display
 *                                  counts -- if non-zero, a message is
 *                                  printed every <n> response messages
 *                                  sent.
 *
 * Examples:
 *
 *   Accept server essages on service 7500, report status every 5000 messages:
 *     tibrvserver -service 7500 -status 5000
 *
 *   Use an ephemeral port for direct communication, specify a daemon host and
 *   port to prevent autostarting a daemon, with status every 1000 requests.
 *   If both client and server use this daemon value (with no other Rendezvous
 *   application which would restart the daemon) with direct-enabled transports,
 *   you can stop the daemon and observe that messages continue between the
 *   client and server with no daemon running.
 *     tibrvserver -service 7522: -daemon localhost:7500 -status 1000
 *
 */

import java.util.*;
import com.tibco.tibrv.*;

public class tibrvserver implements TibrvMsgCallback /*, TibrvTimerCallback */
{
    String service = "7522:7523";   /* Two-part service parameter for direct
                                       communication.  To use ephemeral port
                                       specify in the form "7522:"  */
    String network = null;
    String daemon  = null;
    long status_frq = 0;            /* Default frequency 0 for no status
                                       display while sending and receiving. */
    long requests = 0;
    double server_timeout = 120;

    static String request_subject;
    static String query_subject = "TIBRV.LOCATE";

    TibrvTransport transport;
    TibrvTimer timer;
    TibrvMsg reply_msg;
    TibrvMsg response_msg;

    int x;
    int y;
    int sum;

    boolean msg_received = true;
    boolean event_dispatched;

    public tibrvserver(String args[])
    {
        // parse arguments for possible optional
        // parameters.
        int i = get_InitParams(args);

        // open Tibrv in native implementation
        try
        {
            Tibrv.open(Tibrv.IMPL_NATIVE);
            System.out.println((new Date()).toString()+
                            ": tibrvserver (TIBCO Rendezvous V"+
                            Tibrv.getVersion()+" Java API)");
        }
        catch (TibrvException e)
        {
            System.err.println("Failed to open Tibrv in native implementation:");
            e.printStackTrace();
            System.exit(0);
        }

        // Create a transport.
        try
        {
            System.out.println("Create a transport on"+
                            " service "+((service!=null)?service:"(default)")+
                            " network "+((network!=null)?network:"(default)")+
                            " daemon "+((daemon!=null)?daemon:"(default)"));
            transport = new TibrvRvdTransport(service,network,daemon);
            transport.setDescription("tibrvserver");
        }
        catch (TibrvException e)
        {
            System.err.println("Failed to create TibrvRvdTransport:");
            e.printStackTrace();
            System.exit(0);
        }

       // Create request subject (inbox) and listener
        try
        {
            request_subject = transport.createInbox();
            new TibrvListener(Tibrv.defaultQueue(),
                              this,transport,request_subject,null);
        }
        catch (TibrvException e)
        {
            System.err.println("Failed to initialilze request listener:");
            e.printStackTrace();
            System.exit(0);
        }

       // Create query listener
        try
        {
            new TibrvListener(Tibrv.defaultQueue(),
                              this,transport,query_subject,null);
        }
        catch (TibrvException e)
        {
            System.err.println("Failed to initialilze query listener:");
            e.printStackTrace();
            System.exit(0);
        }

        // create query reply and request response messages
        reply_msg = new TibrvMsg();
        response_msg = new TibrvMsg();

        // Display a server-ready message.
        System.out.println("Listening for client searches on subject "+
                        query_subject+"\n"+
                        "Listening for client requests on subject "+
                        request_subject+"\n"+
                        "Wait time is "+server_timeout+" secs\n"+
                        (new Date()).toString()+": tibrvserver ready...");

        // dispatch Tibrv events with <server_timeout> second timeout.  If
        // message not received within this interval, quit.
        while(msg_received)
        {
            msg_received = false;
            try
            {
                event_dispatched =
                        Tibrv.defaultQueue().timedDispatch(server_timeout);
            }
            catch (TibrvException e)
            {
                System.err.println("Exception dispatching default queue:");
                e.printStackTrace();
                System.exit(0);
            }
            catch(InterruptedException ie)
            {
                System.exit(0);
            }
        }
        if (!event_dispatched)
            System.err.println("tibrvserver: timedDiapatch received timeout");
        System.out.println((new Date()).toString()+
                        ": "+requests+" client requests processed");

    }

    // Message callback.  Flag message received.  If query, reply with server's
    // request subject.  If request, validate message and reply.
    public void onMsg(TibrvListener listener, TibrvMsg msg)
    {
        msg_received = true;
        if (listener.getSubject() .equals (query_subject))
        {
            try
            {
                reply_msg.setReplySubject(request_subject);
                transport.sendReply(reply_msg,msg);
                System.out.println((new Date()).toString()+
                        ": Client search message received");
            }
            catch (TibrvException e)
            {
                System.err.println("Exception dispatching default queue:");
                e.printStackTrace();
                System.exit(0);
            }
        }
        else
        {
            try
            {
                x = msg.getAsInt("x",0);
            }
            catch (TibrvException e)
            {
                System.err.println("tibrvserver: Received bad request (x param).");
                return;
            }
            try
            {
                y = msg.getAsInt("y",0);
            }
            catch (TibrvException e)
            {
                System.err.println("tibrvserver: Received bad request (y param).");
                return;
            }
            sum = x + y;
            try
            {
                response_msg.update("sum", sum, TibrvMsg.U32);
                transport.sendReply(response_msg, msg);
                requests++;
                if (status_frq > 0) {
                    if ((requests % status_frq) ==0) {
                        System.out.println((new Date()).toString()+
                                    ": "+requests+" client requests processed");

                    }
                }
            }
            catch (TibrvException e)
            {
                System.err.println("Error sending a response to request message:");
                e.printStackTrace();
                return;
            }
        }
    }

    // print usage information and quit
    void usage()
    {
        System.err.println("Usage: java tibrvserver [-service <service>] [-network <network>]");
        System.err.println("                        [-daemon  <daemon>]  [-status  <#msgs>]");
        System.exit(-1);
    }

    // parse command line parameters.
    int get_InitParams(String[] args)
    {
        int i=0;
        if (args.length > 0)
        {
            if (args[i].equals("-?") ||
                args[i].equals("-h") ||
                args[i].equals("-help"))
            {
                usage();
            }
        }
        while(i < args.length-1 && args[i].startsWith("-"))
        {
            if (args[i].equals("-service"))
            {
                service = args[i+1];
                i += 2;
            }
            else
            if (args[i].equals("-network"))
            {
                network = args[i+1];
                i += 2;
            }
            else
            if (args[i].equals("-daemon"))
            {
                daemon = args[i+1];
                i += 2;
            }
            else
            if (args[i].equals("-status"))
            {
                status_frq = Integer.parseInt(args[i+1]);
                i += 2;
            }
            else
                usage();
        }
        return i;
    }

    public static void main(String args[])
    {
        new tibrvserver(args);
    }

}
