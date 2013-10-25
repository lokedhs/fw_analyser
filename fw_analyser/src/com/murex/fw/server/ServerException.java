package com.murex.fw.server;

public class ServerException extends Exception
{
    public ServerException() {
        super();
    }

    public ServerException( String message ) {
        super( message );
    }

    public ServerException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ServerException( Throwable cause ) {
        super( cause );
    }
}
