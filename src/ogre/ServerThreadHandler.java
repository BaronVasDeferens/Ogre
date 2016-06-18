package ogre;

/*
 Client Thread Handler
The real workhorse of the server-side set.
Maintains and serves the player lists and gamestates.

 */

import java.util.*;
import java.net.*;
import java.io.*;
import java.io.PrintWriter;
import java.time.*;
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
    String logFileName = "logfile.txt";

    int nextThreadId = 0;
    
    public ServerThreadHandler()
    {
        serverThreadList = new HashSet();
        serverThreadList.clear();
        
        onlinePlayers = new HashSet();
        onlinePlayers.clear();
        
        currentGames = new GameStateIOList(gameFile);
        currentGames.readFromDisk();
        currentGames.displayAll();
        
        registeredPlayers = new PlayerIOList(playerFile);
        registeredPlayers.readFromDisk();
        registeredPlayers.displayAll();
        
        addLogEntry(lineSeperator());
    }
    
    //ADD
    //Create and begin a new serverThread; add it to the list
    public void addServerThread(Socket sock)
    {
        nextThreadId++;
        
        addLogEntry("connection from: " + sock.getInetAddress().toString());
        addLogEntry("created new ServerThread id: " + nextThreadId);

        ServerThread client = new ServerThread(nextThreadId, sock, this);
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
        addLogEntry("player registered: " + thisOne.username + " PW: " + thisOne.password + " EMAIL: " + thisOne.emailAddress);
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
            addLogEntry("player " + thisOne.username + " logged in");
        }
        else
           addLogEntry(thisOne.username + " NOT FOUND");
        
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
        addLogEntry("updating currentGames");
        sendNotificationEmail(updateMe);
    }
    
    
    //SEND NOTIFICATION EMAIL
    //Sends an email to the player whoe turn it has just become
    private void sendNotificationEmail(GameState updateMe)
    {
        //Check to see if the game is "open" (player hasn't finished taking their turn)
        if (updateMe.isOpen == false)  
        {
            String recipient, mailMsg, opponent;
            
            Runtime runtime;
            Process process = null;
            OutputStream out;
            InputStream in;

            
            //The cureentPlayer is the player whose turn it will be when the game is downloaded next
            //Notify the currentPlayer by finding thier email address...
            recipient = registeredPlayers.getEmailAddress(updateMe.currentPlayer.name);

            //Find the name of thier opponent...
            if (updateMe.currentPlayer.name.equals(updateMe.playerOne.name))
                opponent = updateMe.playerTwo.name;
            else
                opponent = updateMe.playerOne.name;
            
            mailMsg = "bash ogremail.sh " + recipient + " " + opponent + " " + updateMe.idNumber;
            
            //Dispatch a quick email! Uses the following script:
            /*
            #!bin/bash
            echo | mailx -s "OGRE: $2 has finished a turn (#$3)" $1 > /dev/null
            echo notification sent to $1
            */
            try
            {
                runtime = Runtime.getRuntime();
                process = runtime.exec(mailMsg);
                process.waitFor();

                in = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                
                while ( (line = br.readLine()) != null)
                {
                    System.out.println(line);    
                } 

                br.close();
                isr.close();
                in.close();
                
                addLogEntry("email dispatched to: " + recipient);
            }
            catch (IOException | InterruptedException e)
            {
                System.out.print(e.toString());
                addLogEntry("email dispatch FAILED ;" + recipient);
            }  
        }
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
        
        //System.out.println("Removed " + deadThreads.size() + " dead threads");
        serverThreadList.removeAll(deadThreads);
        deadThreads.clear();
        
        addLogEntry("dead threads cleared");
    }

    
    // Opens and appends a log to a logfile
    public synchronized void addLogEntry(String newEntry) {
        
        File logFile = new File(logFileName);
        
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                System.out.println("SYSTEM FAILURE: failed to create logfile.txt");
                System.out.println(e.toString());
            }    
        }
        
        try {
            PrintWriter PW = new PrintWriter(new FileOutputStream(logFile, true));
            PW.write(LocalDateTime.now() + " : " + newEntry + "\n");
            PW.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
        
        
    }
    
    
    
    // Adds a seperator fr a new instance and a timestamp
    private String lineSeperator() {
        
        String returnString =
            "\n"+ 
            "*******************************************" + 
            " OGRE SERVER STARTED " + 
            "******************************************* \n";  
        
        return returnString;
    }
    
}
