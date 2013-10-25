package com.murex.fw;

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

    public void pushMessage( MessageType type, String s, Throwable e ) {
        DiagnosticMessage msg = new DiagnosticMessage( type, s, e );
        pending.addLast( msg );

        System.out.printf( "LOG %s %s: %s%n", msg.getType().toString(), name, msg.getText() );
        if( e != null ) {
            e.printStackTrace();
        }
    }
}
