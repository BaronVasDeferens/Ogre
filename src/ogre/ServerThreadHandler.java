package ogre;

/*
 Client Thread Handler
The real workhorse of the server-side set.
Maintains and serves the player lists and gamestates.

 */

import java.util.*;
import java.net.*;
/**
 *
 * @author Skot
 */
public class ServerThreadHandler
{
    IOList registeredPlayers;                   //the IOList of registered players
    
    HashSet<ServerThread> serverThreadList;     //a List of running ServerThreads
    HashSet<GameState> currentGames;
    
    HashSet<Player> onlinePlayers;
    
    String filename = "players.dat";
    
    
    public ServerThreadHandler()
    {
        serverThreadList = new HashSet();
        serverThreadList.clear();
        
        onlinePlayers = new HashSet();
        onlinePlayers.clear();
        
        registeredPlayers = new IOList(filename);
        registeredPlayers.readFromDisk();
        registeredPlayers.displayAll();
    }
    
    //ADD
    //Create and begin a new serverThread; add it to the list
    public void addServerThread(Socket sock)
    {
        ServerThread client = new ServerThread(sock, this);
        client.t.start();
        
        serverThreadList.add(client);
    }
    
    //REMOVE
    private void removeServerThread(ServerThread currentClient)
    {
        currentClient.killThread();
        serverThreadList.remove(currentClient);
    }
    

    //REGISTER PLAYER
    public boolean registerPlayer(TransportObject thisOne)
    {
        Player addMe = new Player(thisOne.username, thisOne.password, thisOne.emailAddress);
        return (registeredPlayers.addPlayer(addMe));
    }
    
    //LOGIN PLAYER
    public Player loginPlayer(TransportObject thisOne)
    {
        Player findMe = new Player(thisOne.username, thisOne.password);
        Player returnMe = registeredPlayers.getPlayerMatching(findMe);
        
        if (returnMe != null)
        {
            onlinePlayers.add(returnMe);
            return (returnMe);
        }
        else
            return null;

    }
    
    //GET REGISTERED USER LIST
    //Returns a LinkedLIst<String> object containing usernames
    public LinkedList getRegisteredUserList()
    {
        if (serverThreadList == null)
            return(null);
        else
        {
            LinkedList<Player> userList = new LinkedList();
            
            Iterator iter = registeredPlayers.iterator();
            
            Player player;
            
            while(iter.hasNext())
            {
                player = (Player)iter.next();
                userList.add(player);
            }
            
            return(userList);
        }
    }
    
    //GET CONNECTED USER LIST
    //Returns a LinkedLIst<String> object containing usernames
    public LinkedList getConnectedUserList()
    {
        if (serverThreadList == null)
            return(null);
        else
        {
            LinkedList<Player> userList = new LinkedList();
            
            Iterator iter = onlinePlayers.iterator();
            ServerThread srvrThrd;
            Player player;
            
            while(iter.hasNext())
            {
                srvrThrd = (ServerThread)iter.next();
                userList.add(srvrThrd.player);
            }
            
            return(userList);
        }
    }
    
    

}
