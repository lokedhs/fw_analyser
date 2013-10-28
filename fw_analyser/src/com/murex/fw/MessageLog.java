package com.murex.fw;

import java.util.ArrayList;
import java.util.List;

public class MessageLog
{
    private String name;
    private List<DiagnosticMessage> pending = new ArrayList<DiagnosticMessage>();

    public MessageLog( String name ) {
        this.name = name;
    }

    public void pushMessage( MessageType type, String s ) {
        pushMessage( type, s, null );
    }

    public void pushMessage( MessageType type, String s, Throwable e ) {
        DiagnosticMessage msg = new DiagnosticMessage( type, s, e );
        synchronized( pending ) {
            pending.add( msg );
        }

        System.out.printf( "LOG %s %s: %s%n", msg.getType().toString(), name, msg.getText() );
        if( e != null ) {
            e.printStackTrace();
        }
    }

    public List<DiagnosticMessage> fetchAndRemoveMessages() {
        List<DiagnosticMessage> ret = new ArrayList<DiagnosticMessage>();
        synchronized( pending ) {
            ret.addAll( pending );
            pending.clear();
        }
        return ret;
    }
}
