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
    
    OgreGame myMaster;
    LoginObject activePlayerCredentials;
    
    Socket sckt = null;
    InputStream in = null;
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    
    //LOGIN MANAGER (WITH WINDOW UI)
    LoginManager(String srvr, int prt, OgreGame master, LoginObject activUsr)
    {
        server = srvr;
        port = prt;
        myMaster = master;
        activePlayerCredentials = activUsr;
    }
    
    //DISPLAY UI
    //Activate the user interface
    public void displayUI()
    {
        loginFrame = new LoginFrame(this);
        loginFrame.setDefaultCloseOperation(LoginFrame.DISPOSE_ON_CLOSE);
        loginFrame.setTitle("Login To Ogre");
        loginFrame.setVisible(true);
    }
        
    
    //CONNECT TO SERVER
    //Handles the connection to (and establishemnt of) network ommunication resources
    public boolean connectToServer()
    {
        boolean connectionSuccess = false;
        
        if (loginFrame != null)
        {
            loginFrame.feedbackTextArea.setText("");
            loginFrame.feedbackTextArea.append("Connecting to server...");
            loginFrame.feedbackTextArea.append("\n");
        }
        
        try
        {
            sckt = new Socket(server, port);

            //Acquitre input and ouput streams
            in = sckt.getInputStream();
            out = sckt.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            objectIn = new ObjectInputStream(in);

            //loginFrame.feedbackTextArea.append("connected!\n");
            connectionSuccess = true;

        }

        catch(java.io.IOException e)
        {
            //loginFrame.feedbackTextArea.append("ERROR: Uh-oh! No server found.\n");
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
            LoginObject loginObj = new LoginObject(username, password);
            TransportObject responseObject = null;

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
                responseObject = null;

                //TODO: include a timeout here
                while (answerReceived == false)
                {
                    try
                    {
                        responseObject = (TransportObject)objectIn.readObject();
                    }
                    
                    catch (ClassNotFoundException | IOException e)
                    {

                    }

                    if (responseObject != null)
                    {
                        loginFrame.feedbackTextArea.append(responseObject.message);
                        
                        //Reponse recieved:
                        //if it is a generic transportObject, it is a failure notice
                        //if it is a loginObject, it contains login data
                        if (responseObject.isLoginRequest)
                        {
                            loginObj = (LoginObject)responseObject;
                            responseObject = null;
                            
                            if (loginObj.player != null)
                            {
                                activePlayerCredentials = loginObj;
                                answerReceived = true;
                            }
                           
                        }
                        
                        else
                        {
                            loginObj = null;
                            answerReceived = true;
                        }

                    }
               
                }//while

                if ((answerReceived == true) && (loginObj != null))
                {
                    //Login success
                    myMaster.activePlayerCredentials = activePlayerCredentials;
                    AOK = true;
                }
                
            }
        }
        
        else
            loginFrame.feedbackTextArea.append("Could not connect.\n");
        
        disconnectFromServer();
        return (AOK);
    }
    
    
    //LOGIN
    public LoginObject getLoginObject(String username, String password)
    {
        LoginObject returnObject = null;
        
        if (connectToServer())
        {
            //Generate a loginObject
            LoginObject loginObj = new LoginObject(username, password);
            TransportObject responseObject = null;

            //Transmit a connection request object and wait for receipt of answer object
            if (objectOut != null)
            {
                try
                {
                    objectOut.writeObject(loginObj);
                }
                catch (IOException e)
                {
                    disconnectFromServer();
                }

                //Await a response from server...
                boolean answerReceived = false;
                loginObj = null;
                responseObject = null;

                //TODO: include a timeout here
                while (answerReceived == false)
                {
                    try
                    {
                        responseObject = (TransportObject)objectIn.readObject();
                    }
                    
                    catch (ClassNotFoundException | IOException e)
                    {

                    }

                    if (responseObject != null)
                    {                        
                        //Reponse recieved:
                        //if it is a generic transportObject, it is a failure notice
                        //if it is a loginObject, it contains login data
                        if (responseObject.isLoginRequest)
                        {
                            loginObj = (LoginObject)responseObject;
                            responseObject = null;
                            answerReceived = true;

                        }
                        
                        else
                        {
                            loginObj = null;
                            answerReceived = true;
                        }

                    }
               
                }//while

                if ((answerReceived == true) && (loginObj != null))
                {
                    //Login success
                    returnObject = (LoginObject)loginObj;
                }
                
            }
        }
        
        disconnectFromServer();
        return (returnObject);
    }
    
    
    public void postLoginScreen()
    {
        if (myMaster.activePlayerCredentials != null)
        {
            myMaster.myFrame.setTitle("OGRE - Logged in as " + myMaster.activePlayerCredentials.player.name);
        }

        //If the player has no current games, take them to the new game screen
        if (myMaster.activePlayerCredentials.gameStateList.size() == 0)
        {
            myMaster.createNewGame();
        }
        
        else
        {
            myMaster.displayMyGames();
        }
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
            
            //loginFrame.feedbackTextArea.append("You have safely disconected from the server.");
        }

        catch(IOException e)
        {
            //loginFrame.feedbackTextArea.append("ERROR: problems closing streams and socket.");
        }
    }
    
    
        
    
}
