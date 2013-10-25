package com.murex.fw;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageLog
{
    private String name;
    private BlockingDeque<DiagnosticMessage> pending = new LinkedBlockingDeque<DiagnosticMessage>();

    public MessageLog( String name ) {
        this.name = name;
    }

    public void pushMessage( MessageType type, String s ) {
        pushMessage( type, s, null );
    }

    public void pushMessage( MessageType type, String s, IOException e ) {
        DiagnosticMessage msg = new DiagnosticMessage( type, s, e );
        pending.addLast( msg );

        System.out.printf( "LOG %s: %s%n", name, msg.getText() );
    }
}
