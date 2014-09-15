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
public class LoginManager 
{
    LoginFrame loginFrame;
    String server;
    int port;
    
    Socket sckt = null;
    InputStream in = null;
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    
    
    LoginManager(String srvr, int prt)
    {
        server = srvr;
        port = prt;
        
        loginFrame = new LoginFrame(this);
        loginFrame.setDefaultCloseOperation(LoginFrame.DISPOSE_ON_CLOSE);
        loginFrame.setVisible(true);
    }
    
    //CONNECT TO SERVER
    //Handles the connection to (and establishemnt of) network ommunication resources
    public boolean connectToServer()
    {
        boolean connectionSuccess = false;
        
        loginFrame.feedbackTextArea.setText("");
        loginFrame.feedbackTextArea.append("Connecting to server...");
        loginFrame.feedbackTextArea.append("\n");
            
        try
        {
            sckt = new Socket(server, port);

            //Acquitre input and ouput streams
            in = sckt.getInputStream();
            out = sckt.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            objectIn = new ObjectInputStream(in);

            loginFrame.feedbackTextArea.append("connected!\n");
            connectionSuccess = true;

        }

        catch(java.io.IOException e)
        {
            loginFrame.feedbackTextArea.append("ERROR: Uh-oh! No server found.\n");
        }
        
        return (connectionSuccess);
    }  
    
    //LOGIN
    public boolean login(String username, String password)
    {
        boolean AOK = false;
        
        if (connectToServer())
        {

            //Generate a loginObject
            TransportObject loginObj = new TransportObject(username, password, null, true, false);

            //Transmit a connection request object and wait for receipt of answer object
            if (objectOut != null)
            {
                try
                {
                    objectOut.writeObject(loginObj);
                }
                catch (IOException e)
                {
                    loginFrame.feedbackTextArea.append("ERROR: cannot communicate with server.\n");
                    disconnectFromServer();
                }

                //Await a response from server...
                boolean answerReceived = false;
                loginObj = null;

                //TODO: include a timeout here
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
                        loginFrame.feedbackTextArea.append(loginObj.message);
                        answerReceived = true;
                    }

                }

                if ((answerReceived == true) && (loginObj.player != null))
                {
                    //loginFrame.feedbackTextArea.append("Welcome to Ogre. One moment, please...\n");
                    AOK = true;

                    //Deploy the myGames window HERE
                }
            }
        }
        
        else
            loginFrame.feedbackTextArea.append("Could not connect.\n");
        
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
            
            loginFrame.feedbackTextArea.append("You have safely disconected from the server.");
        }

        catch(IOException e)
        {
            loginFrame.feedbackTextArea.append("ERROR: problems closing streams and socket.");
        }
    }
    
    
        
    
}
