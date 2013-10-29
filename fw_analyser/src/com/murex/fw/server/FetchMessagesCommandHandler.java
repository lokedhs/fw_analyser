package com.murex.fw.server;

import com.murex.fw.DiagnosticMessage;

import java.util.ArrayList;
import java.util.List;

class FetchMessagesCommandHandler implements CommandHandler
{
    @Override
    public List<String> processCommand( Server server, String[] parts ) throws CommandException {
        List<DiagnosticMessage> messages = server.getMessageLog().fetchAndRemoveMessages();

        List<String> res = new ArrayList<String>();
        res.add( String.valueOf( messages.size() ) );

        for( DiagnosticMessage m : messages ) {
            res.add( String.format( "%s:%d:%s", m.getType(), m.getTimestamp(), m.getText() ) );
        }

        return res;
    }
}
