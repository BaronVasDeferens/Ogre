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
    
    RegisterFrame regFrame;
    String server;
    int port;
    
    Socket sckt = null;
    InputStream in = null;
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    
    
    RegistrationManager(String srvr, int prt)
    {
        regFrame = new RegisterFrame(this);
        regFrame.setDefaultCloseOperation(LoginFrame.DISPOSE_ON_CLOSE);
        regFrame.setVisible(true);
    }
    
    //REGISTER PLAYER
    public boolean registerPlayer(String usrnm, String pswd, String emailAddy)
    {
        boolean AOK = false;
        
        
        regFrame.feedbackTextArea.setText("");
        regFrame.feedbackTextArea.append("Connecting to server...");
        regFrame.feedbackTextArea.append("\n");
            
        //Generate a loginObject
        TransportObject loginObj = new TransportObject(usrnm, pswd, emailAddy, true, true);

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

        catch(java.io.IOException e)
        {
            regFrame.feedbackTextArea.append("ERROR: NO SERVER FOUND\n");
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
                        loginObj = (TransportObject)objectIn.readObject();
                    }
                    catch (ClassNotFoundException | IOException e)
                    {

                    }

                    if (loginObj != null)
                    {
                        regFrame.feedbackTextArea.append(loginObj.message);
                        answerReceived = true;
                    }
                }

                if ((answerReceived == true) && (loginObj.player != null))
                {
                    regFrame.feedbackTextArea.append("Welcome to Ogre. One moment, please...\n");
                    player = loginObj.player;
                    AOK = true;

                    //Deploy the myGames window
                }

                else
                {
                    //Close data connections and streams.
                    regFrame.feedbackTextArea.append("Login error. Please try again.\n");
                    
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
                    }

                    catch(IOException e)
                    {

                    }

                }
            }
        }

        return (AOK);
    }    
}        
          
