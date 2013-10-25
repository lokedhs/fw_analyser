package com.murex.fw;

public class Test
{
    public static void main( String[] args ) {
        try {
            System.out.println( "Starting server" );
            new Thread() {
                @Override
                public void run() {
                    Main.main( new String[] { "s", "1030" } );
                }
            }.start();

            System.out.println( "Waiting to allow server to start up" );
            Thread.sleep( 1000 );

            System.out.println( "Starting client" );
            Main.main( new String[] { "c", "localhost", "1030" } );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
