SumoArena Java Example Client


This client provides a simple but complete implementation, so it can be used as
an example of socket and protocol handling, or as copiable base for your own
Java client.

To use this code to make your own client, you just have to:

1.  Update the following variable in the code:

    private static String name = null;
    private static String avatar = null;

    They may stay null, but in this case you must provide them using command line arguments.

2.  Modify the code below in the Player class: 
    

            ///////////////////////////// insert your code here ///////////////////////
            
            accelerationVector = new AccelerationVector(5, 5);
            
            //////////////////////////////////////////////////////////////////////////

This client accepts command line arguments:

    java SumoClient -name=<your client name> [-hostname=<server>] [-port=<server port number>] [-avatar=<url of the image>] [-verbose]

where

    <your client name> is the name displayed in the game engine for your client program
    <server> is the game server hostname (default: localhost)
    <server port number> is the game server port (default: 9090)
    <url of the image> is the image to be displayed for your sphere in the arena
    -verbose displays verbose logs on the standard output 

For example:

    java SumoClient -name="java client1" -avatar="http://localhost/myavatar.png" -verbose