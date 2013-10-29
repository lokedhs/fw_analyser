package com.murex.fw.client;

import com.murex.fw.MessageType;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Set;

public class PingClientThread extends Thread
{
    private Client client;
    private Set<Thread> threads;
    private String host;
    private int port;

    public PingClientThread( Client client, Set<Thread> threads, String host, int port ) {
        super( "PingClientThread " + host + ":" + port );
        this.client = client;
        this.threads = threads;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Socket s = null;
        try {
            try {
                s = new Socket();
                s.setSoTimeout( 10 * 1000 );
                SocketAddress endpoint = new InetSocketAddress( host, port );
                s.connect( endpoint, 10 * 1000 );
            }
            catch( IOException e ) {
                client.getMessageLog().pushMessage( MessageType.TEST_RESULT, "error when connecting, port: " + port + " message: " + e.getMessage() );
                return;
            }

            try {
                PrintWriter out = new PrintWriter( new OutputStreamWriter( s.getOutputStream(), "UTF-8" ) );
                BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream(), "UTF-8" ) );

                long connectTime = System.currentTimeMillis();
                String message = "test." + startTime + "." + port;
                out.println( message );
                out.flush();

                String reply = in.readLine();
                long endTime = System.currentTimeMillis();
                if( !reply.equals( message ) ) {
                    client.getMessageLog().pushMessage( MessageType.ERROR, "reply message not identical. Expected: '" + message + "', got: '" + reply + "'" );
                }

                client.getMessageLog().pushMessage( MessageType.TEST_RESULT, "ping reply on port " + port +
                        " startTime: " + startTime + " connectTime: " + connectTime + " endTime: " + endTime +
                        " connectLatency: " + (connectTime - startTime) + " fullLatency: " + (endTime - startTime) );
            }
            catch( IOException e ) {
                client.getMessageLog().pushMessage( MessageType.ERROR, "Unable to connect to port " + port, e );
            }
        }
        finally {
            if( s != null ) {
                try {
                    s.close();
                }
                catch( IOException e ) {
                    client.getMessageLog().pushMessage( MessageType.ERROR, "exception when closing socket", e );
                }
            }


            synchronized( threads ) {
                if( !threads.remove( this ) ) {
                    System.out.printf( "thread object missing in thread set when removing thread for port=%d%n", port );
                }
                threads.notify();
            }
        }
    }
}
