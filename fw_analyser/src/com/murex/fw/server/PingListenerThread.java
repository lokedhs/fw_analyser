package com.murex.fw.server;

import com.murex.fw.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

class PingListenerThread extends Thread
{
    private Server server;
    private ServerSocket socket;
    private int port;
    private AtomicBoolean closed = new AtomicBoolean( false );

    public PingListenerThread( Server server, ServerSocket socket, int port ) {
        super( "PingListenerThread port=" + port );
        this.server = server;
        this.socket = socket;
        this.port = port;
    }

    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while( true ) {
                final Socket s = socket.accept();
                Thread t = new ServerPingThread( s );
                t.start();
            }
        }
        catch( IOException e ) {
            if( !closed.get() ) {
                server.getMessageLog().pushMessage( MessageType.ERROR, "failed to accept connection on port " + port );
            }
        }
    }

    public void stopListener() {
        closed.set( true );

        interrupt();
        try {
            socket.close();
        }
        catch( IOException e ) {
            server.getMessageLog().pushMessage( MessageType.ERROR, "exception when closing listener on port + " + port );
        }
    }

    private class ServerPingThread extends Thread
    {
        private final Socket s;

        public ServerPingThread( Socket s ) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream(), "UTF-8" ) );
                PrintWriter out = new PrintWriter( new OutputStreamWriter( s.getOutputStream(), "UTF-8" ) );
                String st = in.readLine();
                out.println( st );
                out.flush();
                s.close();
            }
            catch( IOException e ) {
                server.getMessageLog().pushMessage( MessageType.ERROR, "exception when handling ping", e );
            }
        }
    }
}
