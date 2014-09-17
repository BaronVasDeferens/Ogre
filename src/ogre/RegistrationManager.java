/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author skot
 */
public class RegistrationManager
{
    Player player;
    
    TransportObject loginObj;
    TransportObject activePlayerCredentials = null;
    
    OgreGame myMaster;
    
    RegisterFrame regFrame;
    String server;
    int port;
    
    Socket sckt = null;
    InputStream in = null;
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    
    
    RegistrationManager(String srvr, int prt, OgreGame master, TransportObject actvPlyr)
    {
        server = srvr;
        port = prt;
        
        myMaster = master;
        activePlayerCredentials = actvPlyr;
        
        regFrame = new RegisterFrame(this);
        regFrame.setDefaultCloseOperation(LoginFrame.DISPOSE_ON_CLOSE);
        regFrame.setTitle("Register New Ogre Player");
        regFrame.setVisible(true);
    }
    
    //CONNECT TO SERVER
    public boolean connectToServer()
    {
        boolean connectionSuccess = false;
        
        regFrame.feedbackTextArea.setText("");
        regFrame.feedbackTextArea.append("Connecting to server...");
        regFrame.setTitle("Register With Ogre");
        regFrame.feedbackTextArea.append("\n");
            
        try
        {
            sckt = new Socket(server, port);

            //Acquitre input and ouput streams
            in = sckt.getInputStream();
            out = sckt.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            objectIn = new ObjectInputStream(in);

            regFrame.feedbackTextArea.append("connected!\n");
            connectionSuccess = true;

        }

        catch(java.io.IOException e)
        {
            regFrame.feedbackTextArea.append("ERROR: Uh-oh! No server found.\n");
        }
        
        return (connectionSuccess);
    }
    
    //REGISTER PLAYER
    public boolean registerPlayer(String usrnm, String pswd, String emailAddy)
    {
        boolean AOK = false;
        
        if (connectToServer())
        {
            loginObj = new TransportObject(usrnm, pswd, emailAddy, true, true);

            try
            {
                sckt = new Socket(server, port);

                //Acquitre input and ouput streams
                in = sckt.getInputStream();
                out = sckt.getOutputStream();
                objectOut = new ObjectOutputStream(out);
                objectIn = new ObjectInputStream(in);

                regFrame.feedbackTextArea.append("Connected to server...\n");

            }

            catch (java.io.IOException e)
            {
                regFrame.feedbackTextArea.append(e.toString() + "\n");
            }

            //Push out a loginMessage
            if ((sckt != null) && (objectOut != null))
            {
                try
                {   
                    objectOut.writeObject(loginObj);
                }

                catch(java.io.IOException e)
                {
                    regFrame.feedbackTextArea.append("ERROR: server communication problems.\n");
                }

                //Prepare to wait for answer from the server
                boolean answerReceived = false;
                loginObj = null;

                if (objectIn != null)
                {
                    //TODO: add a time out
                    while (answerReceived == false)
                    {
                        try
                        {
                            activePlayerCredentials = (TransportObject)objectIn.readObject();
                        }
                        catch (ClassNotFoundException | IOException e)
                        {

                        }

                        if (activePlayerCredentials != null)
                        {
                            regFrame.feedbackTextArea.append(activePlayerCredentials.message);
                            answerReceived = true;
                        }
                    }

                    if ((answerReceived == true) && (activePlayerCredentials.player != null))
                    {
                        regFrame.feedbackTextArea.append("Welcome to Ogre. One moment, please...\n");
                        player = activePlayerCredentials.player;
                        myMaster.activePlayerCredentials = activePlayerCredentials;
                        AOK = true;

                        //Deploy the myGames window
                    }

                    else
                    {
                        //Close data connections and streams.
                        regFrame.feedbackTextArea.append("Login error. Please try again.\n");
                        disconnectFromServer();
                    }
                }
            }

            else
                disconnectFromServer();
        }

        return (AOK);
    }
    
     //DISCONNECT FROM SERVER
    //Safely closes socket and streams.
    public void disconnectFromServer()
    {
        try 
        {
            sckt.close();
            sckt = null;                           
            in.close();
            in = null;
            out.close();
            out = null;
            objectIn.close();
            objectIn = null;
            objectOut.close();
            objectOut = null;
            
            regFrame.feedbackTextArea.append("You have safely disconected from the server.");
        }

        catch(IOException e)
        {
            regFrame.feedbackTextArea.append("ERROR: problems closing streams and socket.");
        }
    }
}        
          
