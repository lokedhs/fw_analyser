package com.murex.fw.server;

import com.murex.fw.MessageLog;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server
{
    private static Map<String,CommandHandler> COMMAND_HANDLERS;

    private int port;
    private Map<Integer,PingListenerThread> pingListeners = new HashMap<Integer,PingListenerThread>();
    private MessageLog messageLog = new MessageLog( "server" );

    static {
        HashMap<String,CommandHandler> h = new HashMap<String,CommandHandler>();
        h.put( "listen", new ListenCommandHandler() );
        h.put( "stopPingListener", new StopListenerHandler() );
        COMMAND_HANDLERS = h;
    }

    public Server( int port ) {
        this.port = port;
    }

    public void start() throws ServerException {
        try {
            ServerSocket server = new ServerSocket( port );
            Socket s = server.accept();
            controllerLoop( s );
        }
        catch( IOException e ) {
            throw new ServerException( e );
        }
    }

    private void controllerLoop( Socket socket ) throws IOException, ServerException {
        BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream(), "UTF-8" ) );
        PrintWriter out = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), "UTF-8" ) );

        String s;
        while( (s = in.readLine()) != null ) {
            System.out.println( "got command: '" + s + "'" );
            String[] parts = s.split( " +" );
            if( parts.length == 0 ) {
                throw new ServerException( "blank command" );
            }

            CommandHandler handler = COMMAND_HANDLERS.get( parts[0] );
            if( handler == null ) {
                throw new ServerException( "unknown command: '" + parts[0] + "'" );
            }

            try {
                String result = handler.processCommand( this, parts );
                out.println( result );
                out.flush();
            }
            catch( CommandException e ) {
                throw new ServerException( e );
            }
        }
    }

    public void startPingListener( int port ) throws IOException {
        synchronized( pingListeners ) {
            ServerSocket s = new ServerSocket( port );
            PingListenerThread thread = new PingListenerThread( this, s );
            pingListeners.put( port, thread );
            thread.start();
        }
    }

    public void stopPingListener( int port ) {
        synchronized( pingListeners ) {
            PingListenerThread listener = pingListeners.remove( port );
            listener.stopListener();
        }
    }

    public MessageLog getMessageLog() {
        return messageLog;
    }
}
