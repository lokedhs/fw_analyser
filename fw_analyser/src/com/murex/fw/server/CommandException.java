package com.murex.fw.server;

class CommandException extends Exception
{
    public CommandException() {
        super();
    }

    public CommandException( String message ) {
        super( message );
    }

    public CommandException( String message, Throwable cause ) {
        super( message, cause );
    }

    public CommandException( Throwable cause ) {
        super( cause );
    }
}
