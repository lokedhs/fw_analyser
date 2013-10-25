package com.murex.fw.server;

import java.net.ServerSocket;

class ControllerThread extends Thread
{
    private ServerSocket server;

    public ControllerThread( ServerSocket server ) {
        this.server = server;
    }

    public void run() {
        while( true ) {

        }
    }
}
