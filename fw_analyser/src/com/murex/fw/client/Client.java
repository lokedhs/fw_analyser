package com.murex.fw.client;

import com.murex.fw.MessageLog;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client
{
    private int portTestBlockSize = 3;

    private String host;
    private int port;

    private BufferedReader controlIn;
    private PrintWriter controlOut;

    private MessageLog messageLog = new MessageLog( "client" );

    public Client( String host, int port ) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        Socket s = new Socket( host, port );
        controlIn = new BufferedReader( new InputStreamReader( s.getInputStream(), "UTF-8" ) );
        controlOut = new PrintWriter( new OutputStreamWriter( s.getOutputStream(), "UTF-8" ) );

        testPing( 7700, 10 );

        reportServerLog();

        s.close();
    }

    private void reportServerLog() throws IOException {
        Pattern pattern = Pattern.compile( "^([a-zA-Z0-9]+):([0-9]+):(.*)$" );

        List<String> serverMessages = readServerMessages();
        for( String message : serverMessages ) {
            Matcher matcher = pattern.matcher( message );
            if( !matcher.matches() ) {
                throw new RuntimeException( "Cannot parse message: '" + message + "'" );
            }

            String type = matcher.group( 1 );
            long timestamp = Long.parseLong( matcher.group( 2 ) );
            String messageText = matcher.group( 3 );

            System.out.printf( "LOG %s server: %s%n", type, messageText );
        }
    }

    private List<String> readServerMessages() throws IOException {
        controlOut.println( "messages" );
        controlOut.flush();

        int numMessages = Integer.parseInt( controlIn.readLine() );
        List<String> result = new ArrayList<String>( numMessages );
        for( int i = 0 ; i < numMessages ; i++ ) {
            result.add( controlIn.readLine() );
        }

        return result;
    }

    private void testPing( int startPort, int numPorts ) throws IOException {
        for( int i = startPort ; i < startPort + numPorts ; i += portTestBlockSize ) {
            testBlock( i, Math.min( (startPort + numPorts) - i, portTestBlockSize ) );
        }
    }

    private void testBlock( int start, int count ) throws IOException {
        System.out.printf( "Testing block start=%d, count=%d%n", start, count );

        controlOut.println( "listen " + start + " " + count );
        controlOut.flush();

        BitSet failedPorts = new BitSet( 65536 );
        String reply = controlIn.readLine();
        String[] parts = reply.split( " +" );
        String failedResult = parts[0].trim();
        if( !failedResult.equals( "" ) ) {
            String[] portStrings = parts[0].split( "," );
            for( String s : portStrings ) {
                failedPorts.set( Integer.parseInt( s ) );
            }
        }

        System.out.printf( "Server reports %d failed listeners in the range %d-%d%n",
                           failedPorts.cardinality(), start, start + count - 1 );
        Set<Thread> threads = new HashSet<Thread>();
        for( int i = start ; i < start + count ; i++ ) {
            if( !failedPorts.get( i ) ) {
                Thread t = new PingClientThread( this, threads, host, i );
                synchronized( threads ) {
                    threads.add( t );
                }
                t.start();
            }
        }

        synchronized( threads ) {
            try {
                while( !threads.isEmpty() && !Thread.interrupted() ) {
                    threads.wait();
                }
            }
            catch( InterruptedException e ) {
                // This shouldn't happen, unless interruption support is added to the client in which
                // case this code needs to be modified.
                System.out.println( "Client was interrupted." );
                e.printStackTrace();
            }
        }

        controlOut.println( "stopPingListener " + start + " " + count );
        controlOut.flush();
        String stopListenerReply = controlIn.readLine();
        if( !stopListenerReply.equals( "OK" ) ) {
            throw new RuntimeException( "Expected OK reply from server, got: " + stopListenerReply );
        }
    }

    public MessageLog getMessageLog() {
        return messageLog;
    }
}
