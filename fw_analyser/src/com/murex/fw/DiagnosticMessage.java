package com.murex.fw;

public class DiagnosticMessage
{
    private long timestamp;
    private MessageType type;
    private String text;
    private Throwable throwable;

    public DiagnosticMessage( MessageType type, String text, Throwable throwable ) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.text = text;
        this.throwable = throwable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
