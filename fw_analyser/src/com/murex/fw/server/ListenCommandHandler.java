package com.murex.fw.server;

import com.murex.fw.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ListenCommandHandler implements CommandHandler
{
    @Override
    public List<String> processCommand( Server server, String[] parts ) throws CommandException {
        int startPort = Integer.parseInt( parts[1] );
        int numPorts = parts.length > 2 ? Integer.parseInt( parts[2] ) : 1;

        if( startPort <= 0 || numPorts <= 0 || startPort + numPorts > 65536 ) {
            throw new CommandException( "illegal port settings" );
        }

        List<Integer> failedPorts = new ArrayList<Integer>();
        for( int port = startPort ; port < startPort + numPorts ; port++ ) {
            try {
                server.startPingListener( port );
            }
            catch( IOException e ) {
                server.getMessageLog().pushMessage( MessageType.ERROR, "exception when starting listener on port " + port, e );
                failedPorts.add( port );
            }
        }

        StringBuilder buf = new StringBuilder();
        for( int i = 0 ; i < failedPorts.size() ; i++ ) {
            if( i != 0 ) {
                buf.append( "," );
            }
            buf.append( failedPorts.get( i ) );
        }
        return Collections.singletonList( buf.toString() );
    }
}
