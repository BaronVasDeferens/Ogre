package ogre;

/*
 ServerThread
 by Scott West
 This class handles the data traffic between the client and the server.
 Server-side client connection management class
 */
import java.util.Date;
import java.net.*;
import java.io.*;
import java.util.*;


public class ServerThread implements Runnable
{
    ServerThreadHandler master;
    Socket socket;
    
    Player player;

    Date date;

    FileOutputStream fileOut;
    ObjectInputStream objectIn;
    ObjectOutputStream objectOut;
    InputStream in;
    OutputStream out;
    Thread t;
   
    public volatile boolean active = true;
    
    //CONSTRUCTOR
    //Accepts a socket (a new connection to the client) and a rference to the
    //ServerThreadHandler (neeed to trigger message dispatch)
    public ServerThread(Socket sckt, ServerThreadHandler mstr)
    {
        socket = sckt;
        master = mstr;
        date = new Date();
        
        //TODO: set the date, store it
        
        t = new Thread(this);
    }
    
    //SET USER
    public void setPlayer(Player plyr)
    {
        plyr = player;
    }
    
    //RUN
    @Override
    public void run()
    {
        System.out.println("Thread starting..");
        
        TransportObject transObj = null;
        
        //Initialize the streams
        if ((socket != null) && (active == true))
        {
            try
            {
                in = socket.getInputStream();
                objectIn = new ObjectInputStream(in);
                out = socket.getOutputStream();
                objectOut = new ObjectOutputStream(out);         
            }
            catch (IOException e)
            {
                System.out.println("ClientThread: error opening streams and sockets");
                active = false;
            }
        }
        
        else
            active = false;
        
        //*** MAIN LOOP ***
        //Begin listening for input from client connection...
        while (active == true)
        {
            if (socket != null)
            {    
                //Accept messages sent to this ClientThread
                try
                {
                    transObj = (TransportObject)objectIn.readObject();
                }
                catch (ClassNotFoundException | IOException e)
                {
                    active = false;
                    System.out.println("ServerThread ERROR: problem reading object"); 
                }
                
                
                //EXAMINE THE RECEIVED OBJECT:
           
                //REGISTRATION REQUEST
                if (transObj.isRegistration)
                {
                    //If the new player was successfully registered wth the system, log them in
                    if (master.registerPlayer(transObj))
                    {
                        player = master.loginPlayer(transObj);
                        
                        if (player != null)
                        {
                            System.out.println(player.name + " has registered and logged in!");
                            transObj = new TransportObject(player, master.getRegisteredUserList(), "Registration SUCCESS!");
                            
                            send(transObj);
                        }
                        
                        //Login FAILED
                        else
                        {
                            transObj = new TransportObject(null, null, "Login FAILED.");
                            send(transObj);
                        }
                    }
                    
                    //Could not register the user
                    else
                    {
                        transObj = new TransportObject(null, null, "Registration FAILED.");
                        send(transObj);
                    }
                }
                
                //LOGIN REQUEST
                else if (transObj.isLogin)
                {
                    
                }

            }
            
            //Check for connection
            if (socket.isConnected() == false)
            {
                active = false;
            }
            
        } //while active
        
        
        //Close out the connection
        closeStreamsAndSocket();
    }
    
    //SEND MESSAGE
    //Pushes a msg object to the client
    public void send(TransportObject sendMe)
    {
        try
        {
            objectOut.writeObject(sendMe);
        }
        catch (IOException e)
        {
            
        }
    }

    
    
    //CLOSE STREAMS AND SOCKETS
    //Kills the connection, closes the streams, and sets the socket to null.
    //If the client thread was running, kill it as well.
    public void closeStreamsAndSocket()
    {
        //Close the streams and socket
        try
        {
            if (objectOut != null)
                objectOut.close();
            if (objectIn != null)
                objectIn.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            
            if (socket != null)
                socket.close();
            
            socket = null;
            
            active = false;
            
            System.out.println("ServerThread ended.");
        }
        catch (IOException e)
        {
            System.out.println("ServerThread: problems closing streams");
            System.out.println(e.toString());
        }        
    }
    
    //KILL THREAD
    //Does this ever get called? I'm not even sure...
    public void killThread()
    {
        if (socket != null)
            closeStreamsAndSocket();
        
        t.interrupt();
        System.out.println(t.getState().toString());
    }
    
}
