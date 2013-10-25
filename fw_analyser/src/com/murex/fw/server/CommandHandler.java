package com.murex.fw.server;

interface CommandHandler
{
    String processCommand( Server server, String[] parts ) throws CommandException;
}
