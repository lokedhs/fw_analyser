package com.murex.fw.server;

import com.murex.fw.MessageLog;
import com.murex.fw.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server
{
    private static Map<String, CommandHandler> COMMAND_HANDLERS;

    private int port;
    private Map<Integer, PingListenerThread> pingListeners = new HashMap<Integer, PingListenerThread>();
    private MessageLog messageLog = new MessageLog( "server" );
    private AtomicBoolean willShutDown = new AtomicBoolean( false );

    static {
        HashMap<String, CommandHandler> h = new HashMap<String, CommandHandler>();
        h.put( "listen", new ListenCommandHandler() );
        h.put( "stopPingListener", new StopListenerHandler() );
        h.put( "messages", new FetchMessagesCommandHandler() );
        COMMAND_HANDLERS = h;
    }

    public Server( int port ) {
        this.port = port;
    }

    public void start() throws ServerException {
        try {
            ServerSocket server = new ServerSocket( port );
            Thread t = new ControllerLoopThread( server );
            t.start();
            System.out.println( "Server started" );
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
                List<String> result = handler.processCommand( this, parts );
                if( !result.isEmpty() ) {
                    for( String row : result ) {
                        out.println( row );
                    }
                    out.flush();
                }
            }
            catch( CommandException e ) {
                throw new ServerException( e );
            }
        }
    }

    public void startPingListener( int port ) throws IOException {
        synchronized( pingListeners ) {
            ServerSocket s = new ServerSocket( port );
            PingListenerThread thread = new PingListenerThread( this, s, port );
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

    private class ControllerLoopThread extends Thread
    {
        private final ServerSocket server;

        public ControllerLoopThread( ServerSocket server ) {
            this.server = server;
        }

        @Override
        public void run() {
            try {
                Socket s = server.accept();
                controllerLoop( s );
                System.out.println( "Client disconnected, terminating server" );
                System.exit( 0 );
            }
            catch( IOException e ) {
                messageLog.pushMessage( MessageType.ERROR, "IOException in controller loop", e );
            }
            catch( ServerException e ) {
                messageLog.pushMessage( MessageType.ERROR, "ServerException in controller loop", e );
            }
        }
    }
}
