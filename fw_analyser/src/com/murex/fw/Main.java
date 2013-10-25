package com.murex.fw;

import com.murex.fw.client.Client;
import com.murex.fw.server.Server;
import com.murex.fw.server.ServerException;

import java.io.IOException;

public class Main
{
    public static void main( String[] args ) {
        String type = args[0];

        if( type.equals( "s" ) ) {
            try {
                int port = parsePort( args[1] );
                new Server( port ).start();
            }
            catch( Exception e ) {
                e.printStackTrace();
                System.exit( 1 );
            }
        }
        else if( type.equals( "c" ) ) {
            String host = args[1];
            int port = parsePort( args[2] );
            try {
                new Client( host, port ).start();
            }
            catch( IOException e ) {
                e.printStackTrace();
                System.exit( 1 );
            }
        }
        else {
            usage();
        }
    }

    private static int parsePort( String arg ) {
        int port = 0;
        try {
            port = Integer.parseInt( arg );
        }
        catch( NumberFormatException e ) {
            System.out.printf( "can't parse port number: '%s'%n", arg );
            usage();
        }
        return port;
    }

    private static void usage() {
        System.out.println( "Usage: {s,c} {port}" );
        System.exit( 1 );
    }
}
