/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.net.*;
import java.io.*;

public class GameStateUploadManager 
{
    
    LoginObject currentPlayerCredentials = null;
    
    Socket socket = null;
    String server;
    int port;
    
    OutputStream out = null;
    ObjectOutputStream objectOut = null;
    
    GameStateUploadManager(String srvr, int prt, LoginObject credentials)
    {
        currentPlayerCredentials = credentials;
        
        server = srvr;
        port = prt;
    }
    
    
    //UPLOAD GAME STATE
    //Manager the communication with and transfer of data to the server
    public boolean uploadGameState(GameState sendMe)
    {
        boolean success = false;
        
        if (sendMe == null)
            return false;
        
        if (currentPlayerCredentials == null)
            return false;
        
        else if (currentPlayerCredentials.player == null)
        {
            return false;
        }
        
        else
        {
            //Connect to server, obtain streams
            try
            {
                socket = new Socket(server,port);
                socket.setSoLinger(true, 60);
                out = socket.getOutputStream();
                objectOut = new ObjectOutputStream(out);
            }
            
            catch (IOException e)
            {
                //TODO: thrown an error msg
                System.out.println(e.toString());
            }
            
            //Create upload package and transmit
            GameStateUploadObject uploadPackage = new GameStateUploadObject(currentPlayerCredentials, sendMe);
            
            //Transmit data and close the streams
            if ((socket != null) && (objectOut != null))
            {
                try
                {
                    objectOut.writeObject(uploadPackage);
                    success = true;
                }

                catch (IOException e)
                {
                    //TODO: thrown an error msg
                     System.out.println(e.toString());
                     success = false;
                }

                try
                {
                    objectOut.close();
                    out.close();
                    socket.close();

                    socket = null;
                    objectOut = null;
                    out = null;

                }

                catch (IOException e)
                {
                    //TODO: thrown an error msg
                     System.out.println(e.toString());
                }
            }
              
        }
        
        return (success);
    }
}
