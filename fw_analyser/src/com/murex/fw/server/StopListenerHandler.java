package com.murex.fw.server;

import java.util.Collections;
import java.util.List;

public class StopListenerHandler implements CommandHandler
{
    @Override
    public List<String> processCommand( Server server, String[] parts ) throws CommandException {
        int startPort = Integer.parseInt( parts[1] );
        int numPorts = parts.length > 2 ? Integer.parseInt( parts[2] ) : 1;

        if( startPort <= 0 || numPorts <= 0 || startPort + numPorts > 65536 ) {
            throw new CommandException( "illegal port settings" );
        }

        for( int port = startPort ; port < startPort + numPorts ; port++ ) {
            server.stopPingListener( port );
        }

        return Collections.singletonList( "OK" );
    }
}
