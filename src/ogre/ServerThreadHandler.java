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
    PlayerIOList registeredPlayers;                   //the IOList of registered players
    GameStateIOList currentGames;
    
    HashSet<ServerThread> serverThreadList;     //a List of running ServerThreads
    HashSet<Player> onlinePlayers;
    
    String playerFile = "players.dat";
    String gameFile = "games.dat";
    
    
    public ServerThreadHandler()
    {
        serverThreadList = new HashSet();
        serverThreadList.clear();
        
        onlinePlayers = new HashSet();
        onlinePlayers.clear();
        
        currentGames = new GameStateIOList(gameFile);
        
        registeredPlayers = new PlayerIOList(playerFile);
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
    public boolean registerPlayer(RegistrationObject thisOne)
    {
        Player addMe = new Player(thisOne.username, thisOne.password, thisOne.emailAddress);
        return (registeredPlayers.addPlayer(addMe));
    }
    
    //LOGIN PLAYER
    public Player loginPlayer(LoginObject thisOne)
    {
        Player findMe = new Player(thisOne.username, thisOne.password);
        Player returnMe = registeredPlayers.getPlayerMatching(findMe);
        
        if (returnMe != null)
        {
            onlinePlayers.add(returnMe);
        }
        
        return (returnMe);

    }
    
    //GET REGISTERED USER LIST
    //Returns a LinkedList<String> object containing usernames
    //If a Player argument was supplied, do not include it in the list.
    //If the Player argument is null, include everyone
    public HashSet<Player> getRegisteredPlayerList(Player excludeMe)
    {
        if (serverThreadList == null)
            return(null);
        else
        {
            HashSet<Player> userList = new HashSet();
            
            Iterator iter = registeredPlayers.iterator();
            
            Player player;
            
            if (excludeMe == null)
            {
                while(iter.hasNext())
                {
                    player = (Player)iter.next();
                    userList.add(player);
                }
            }
            
            else
            {
                while(iter.hasNext())
                {
                    player = (Player)iter.next();
                    
                    if (!excludeMe.name.equals(player.name))
                        userList.add(player);
                }
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
    
    //GET GAME STATES
    //Returns a LL populated with all stored GameStates with a player matching the given String argument
    public HashSet<GameState> getGamesList(String username)
    {
        return (currentGames.getGamesList(username, 0));
    }
    
    //UPDATE GAME STATE
    //Submits a gamestate to be updated
    public void updateGameState(GameState updateMe)
    {
        currentGames.updateGameState(updateMe);
    }
    
    
    public void cullDeadThreads()
    {
        Iterator iter = serverThreadList.iterator();
        HashSet deadThreads = new HashSet();
        deadThreads.clear();
        ServerThread thisThread;
        
        while (iter.hasNext())
        {
            thisThread = (ServerThread)iter.next();
            
            if (thisThread.active == false)
                deadThreads.add(thisThread);
        }
        
        System.out.println("Removed " + deadThreads.size() + " dead threads");
        serverThreadList.removeAll(deadThreads);
        deadThreads.clear();
    }

}
