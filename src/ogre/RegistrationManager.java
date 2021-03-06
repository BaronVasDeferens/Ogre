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
    
    LoginObject loginObj;
    TransportObject transObj;
    LoginObject activePlayerCredentials = null;
    
    OgreGame myMaster;
    
    RegisterFrame regFrame;
    String server;
    int port;
    
    Socket sckt = null;
    InputStream in = null;
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    
    
    RegistrationManager(String srvr, int prt, OgreGame master, LoginObject actvPlyr)
    {
        server = srvr;
        port = prt;
        
        myMaster = master;
        activePlayerCredentials = actvPlyr;
        transObj = null;
        
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
            RegistrationObject regObj = new RegistrationObject(usrnm, pswd, emailAddy);

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
                    objectOut.writeObject(regObj);
                }

                catch(java.io.IOException e)
                {
                    regFrame.feedbackTextArea.append("ERROR: server communication problems.\n");
                }

                //Prepare to wait for answer from the server
                boolean answerReceived = false;
                loginObj = null;

                //Check the object reader:
                if (objectIn != null)
                {
                    //TODO: add a time out
                    while (answerReceived == false)
                    {
                        try
                        {
                            activePlayerCredentials = (LoginObject)objectIn.readObject();
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
                        regFrame.feedbackTextArea.append("Welcome to Ogre...\n");
                        player = activePlayerCredentials.player;
                        myMaster.activePlayerCredentials = activePlayerCredentials;
                        myMaster.myFrame.setTitle("OGRE - Logged in as " + activePlayerCredentials.player.name);
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
        if (sckt != null)
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

                //regFrame.feedbackTextArea.append("You have safely disconected from the server.");
            }

            catch(IOException e)
            {
                //regFrame.feedbackTextArea.append("ERROR: problems closing streams and socket.");
            }
        }
    }
}        
          
