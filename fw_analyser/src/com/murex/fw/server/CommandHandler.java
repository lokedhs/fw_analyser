package com.murex.fw.server;

import java.util.List;

interface CommandHandler
{
    List<String> processCommand( Server server, String[] parts ) throws CommandException;
}
