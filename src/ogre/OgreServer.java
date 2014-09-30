package ogre;

/*
ChatShack Server
This is the server portion of the ChatShack client/server pair.
USAGE: when run with no arguments, the server will establish itself on the
local IP and listen for connection requests on port 12321; otherwise, the port
can be secified by the user.

This portion only listens at the specified port and creates new instances of 
CLientThread (which handles users/connections on a per-client basis).


*/

       

import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *
 * @author Skot
 */
public class OgreServer
{
    static ServerThreadHandler serverThreadHandler;
    
    public static void main(String [] args) throws IOException
    {
        int port = 12321;
        
        serverThreadHandler = new ServerThreadHandler();
                
        ServerSocket serverSocket;
        Socket socket;
        InputStream in = null;
        OutputStream out = null;
       
        
        //If an port argument was supplied, set the port:
        if (args != null)
        {
            if (args.length > 0)
            {
                port = Integer.parseInt(args[0]);
            }
        }
            
        System.out.println("Server starting...");
        
        try
        {
            InetAddress addy = InetAddress.getLocalHost();
            System.out.println("Server address: " + addy.getHostAddress() + ":" + port + "\n");
        }
        catch (UnknownHostException e)
        {
            System.out.println("SERVER: Problem with resolving local host...");
        }
        
        serverSocket = new ServerSocket(port);
        //serverSocket.setReceiveBufferSize(87380);
        
        System.out.println("Standing by...");
        
        //Listen for socket connection requests; 
        //when connection is made, create a new socket and pass it to a new serverThreadHandler
        while (true)
        {
            socket = serverSocket.accept();
            
            if (socket.isConnected() == true)
            {
                serverThreadHandler.addServerThread(socket);
                System.out.println("Incoming connection...");
            }
            
            serverThreadHandler.cullDeadThreads();
        }
        
    }
    
}  

