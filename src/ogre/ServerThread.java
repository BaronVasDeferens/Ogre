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
        player = null;
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
                    //System.out.println("ServerThread ERROR: problem reading object"); 
                }
                
                
                //EXAMINE THE RECEIVED OBJECT:
                if (transObj == null)
                {
                    //do nothing
                }
                
                //********************
                //REGISTRATION REQUEST
                else if (transObj.isRegistrationRequest)
                {
                    RegistrationObject regObj = (RegistrationObject)transObj;
                    
                    //Attempt to register:
                    if (master.registerPlayer(regObj))
                    {
                        //Success. Now, obtain a reference 
                        //Send a reply in a LoginObject
                        LoginObject loginObj = new LoginObject(regObj.username, regObj.password);
                        player = master.loginPlayer(loginObj);
                        
                        if (player != null)
                        {
                            System.out.println(player.name + " has registered");
                            
                            //Return a loginObject with an empty gamestate list and all registered users
                            loginObj = new LoginObject(player, "Registration SUCCESS!", master.getGamesList(player.name), master.getRegisteredPlayerList(player));
                            
                            send(loginObj);  
                        }
                        
                        //Login FAILED
                        //Send a generic transportobject
                        else
                        {
                            TransportObject loginFail = new TransportObject(transObj.username, "Login FAILED!");
                            send(loginFail);
                        }
                    }
                    
                    //Could not register the user
                    //Send a generic transportobject
                    else
                    {
                        TransportObject loginFail = new TransportObject(transObj.username, transObj.username +  "is already registered. Registration failed.");
                        send(loginFail);
                    }
                    
                    transObj = null;
                }
                //*************
                //LOGIN REQUEST
                else if (transObj.isLoginRequest)
                {
                    
                    //Attempt to obtain reference to prior registered user
                    player = master.loginPlayer((LoginObject)transObj);
                        
                    //Login SUCCESS
                    //Include player reference, message, current games, and list of regisered players
                    if (player != null)
                    {
                        System.out.println(player.name + " logged in");
                        LoginObject loginObj = new LoginObject(player, "Login SUCCESS!", master.getGamesList(player.name),master.getRegisteredPlayerList(player));
                        
                        send(loginObj);
                    }

                    //Login FAILED
                    //Send generic transportobject and no user reference
                    else
                    {
                        transObj = new TransportObject(null, "Login FAILED.");
                        send(transObj);
                        active = false;
                    }
                    
                    transObj = null;
                }
                
                //********************
                //GAME CHECKOUT REQUEST
                
                
                
                //*******************
                //GAME UPLOAD REQUEST
                //Accepts a GameUploadObject and either stores or updates a gamestate with those same properties
                else if (transObj.commitGameStateRequest)
                {
                    GameStateUploadObject gameUpload = null;
                    
                    gameUpload = (GameStateUploadObject)transObj;
                    
                    if (gameUpload != null)
                    {
                        //Send the gameState to be updated
                        master.updateGameState(gameUpload.gameStateToCommit);
                    }
                    
                    
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
