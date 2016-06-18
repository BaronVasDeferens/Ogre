package ogre;



/*
DATA WRITER
by Skot West
Creates and maintains a LinkedList of Objects. This list is then able to be written/read
to/from disk. The structure and content of the data structure are preserved.

 */

import java.util.*;
import java.io.*;

/**
 *
 * @author Skot
 */
public class IOList implements Serializable
{
    protected DataList<Object> dataList;
    protected String dataFileName;
    
    protected FileOutputStream out;
    protected FileInputStream in;
    protected ObjectOutputStream objectOut;
    protected ObjectInputStream objectIn;
    
    //DEFAULT CONSTRUCTOR
    public IOList()
    {
        dataList = new DataList();
        dataList.clear();
        
        dataFileName = null;
        
        out = null;
        in = null;
        objectOut = null;
        objectIn = null;

    }
    
    //CONSTRUCTOR WITH ARG
    //Sets a specified dataFileName which will be read from an saved to.
    public IOList(String arg)
    {
        this();
        dataFileName = arg;
    }
    
   
    //ITERTATOR
    //Returns an iterator across the 
    public Iterator iterator()
    {
        return dataList.iterator();
    }
    
    
    //CLEAR ALL
    //Clears all data but does not not overwrite the file
    public synchronized void clearAll()
    {
        if (dataList != null)
        {
            dataList.clear();
        }   
    }
    
    //DESTROY ALL DATA
    //CLears all data and overwrites the file. Requires the name of the filename as a security check.
    public synchronized void destroyAllData(String filename)
    {
        if (dataFileName.equals(filename))
        {
            dataList.clear();
            writeToDisk();
        }
    }
    
    
    //DISPLAY ALL
    //Prints all user data to System.out
    public synchronized void displayAll()
    {
        //OUTPUT DATA
        if (dataList != null)
        {
            Object temp;
            int size = dataList.size();
            System.out.println(size + " ENTRIES FOUND:");
            
            Iterator iter = dataList.iterator();
            
            while (iter.hasNext())
            {
                temp = (Object)iter.next();
                System.out.println(temp.toString());
            }
        }
    }
    
    
    //READ FROM DISK (NO ARGS)
    //Reads from the dataFileName supplied at instantiation.
    public synchronized boolean readFromDisk()
    {
        if (dataFileName == null)
            return false;
        else
            return (readFromDisk(dataFileName));
    }
    
    
    //READ FROM DISK (WITH ARG)
    //Reads a data structure from specified file
    public synchronized boolean readFromDisk(String file)
    {
        try
        {
            in = new FileInputStream(file);
            objectIn = new ObjectInputStream(in);
            
            dataList = (DataList)objectIn.readObject();
            
            in.close();
            return (true);
        }
        
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("IOList ERROR: problem reading " + file);
            System.out.println(e.toString());
            return (false);
        }
       
        
    }
    
    //WRITE TO DISK (NO ARGS)
    //Commits the contents of the list to a previously specified file
    public synchronized boolean writeToDisk()
    {
        if (dataFileName == null)
            return (false);
        else
            return (writeToDisk(dataFileName));
    }
    
    //WRITE TO DISK
    //Commits the contents of the data structure to disk
    //Returns true upon success.
    public synchronized boolean writeToDisk(String file)
    {
        //WRITE TO DISK
        try
        {
            out = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(out);
            
            objectOut.writeObject(dataList);
            
            out.close();
            return (true);   
        }
        
        catch(IOException e)
        {
            System.out.println("IOList: error writing to " + file);
            System.out.println(e.toString());
            return (false);
        }
        
    }
}
    

//**************
//PLAYER IO LIST
//**************

//Handles persistent player database
class PlayerIOList extends IOList
{
    
    //DEFAULT CONSTRUCTOR
    public PlayerIOList()
    {
        super();
    }
    
    //CONSTRUCTOR WITH ARG
    //Sets a specified dataFileName which will be read from an saved to.
    public PlayerIOList(String arg)
    {
        super(arg);
    }
    
    //ADD USER
    //Determines whether or not the candidate to add has a unique name
    //If so, add it to the list
    public synchronized boolean addPlayer(Player addMe)
    {
        if (addMe == null)
        {
            dataList = new ogre.DataList();
            dataList.clear();
            return (addPlayer(addMe));
        }
        
        else if (dataList.isEmpty())
        {
            dataList.add(addMe);
            writeToDisk();
            return (true);
        }
        
        else
        {
            if (hasEntryNamed(addMe.name) == false)
            {
                dataList.add(addMe);
                writeToDisk();
                return (true);
            }
            //Enrty with that name has already been found. Return false
            else
                return(false);
        }
    }
    

    
    //MATCHES
    //Returns true if an exact match (both name an password) is found
    public synchronized boolean matches(Player findMe)
    {
        if (dataList == null)
            return (false);
        else
        {
            Iterator iter = dataList.iterator();
            Player currentPlayer;
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if ((currentPlayer.name.equals(findMe.name)) && (currentPlayer.password.equals(findMe.password)))
                {
                    return (true);
                }
                
            }
            
            return(false);
        }
    }
    
    
    //GET PLAYER MATCHING
    //Returns the player from the records matching the one described
    public synchronized Player getPlayerMatching(Player findMe)
    {
        if (dataList == null)
            return (null);
        
        else
        {
            Iterator iter = dataList.iterator();
            Player currentPlayer;
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if ((currentPlayer.name.equals(findMe.name)) && (currentPlayer.password.equals(findMe.password)))
                {
                    return (currentPlayer);
                }
                
            }
            
            return(null);
        }
    }
    
    public synchronized String getEmailAddress(String playerName)
    {
        String address =  null;
        
        if (dataList == null)
            return (null);
        else
        {
            Iterator iter = dataList.iterator();
            Player currentPlayer;
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if (currentPlayer.name.equals(playerName))
                {
                    return (currentPlayer.emailAddress);
                }
                
            }
        }
        
        return address;
    }
    
    
    //HAS ENTRY NAMED
    //Scans through the records and returns truw if there is an entry matching the argument
    public synchronized boolean hasEntryNamed(String user)
    {
        if (dataList == null)
            return false;
        
        else if (dataList.size() == 0)
            return (false);
        else
        {
            Iterator iter = dataList.iterator();
            Player currentPlayer;
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if ((currentPlayer.name.equals(user)))
                {
                    return (true);
                }
            }
            
            return(false);
        }
    }
    
    //REMOVE USER
    //Returns true if the user 
    public synchronized boolean remove(String deleteMe)
    {        
        if (dataList == null)
            return false;
        else
        {
            Player currentPlayer;
            Iterator iter = dataList.iterator();
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if(currentPlayer.name.equals(deleteMe))
                {
                    dataList.remove(currentPlayer);
                    writeToDisk();
                    return(true);
                }
            }
            
            return(false);
        }
    }
    
    //DISPLAY ALL
    //Prints all user data to System.out
    @Override
    public synchronized void displayAll()
    {
        //OUTPUT DATA
        if (dataList != null)
        {
            int size = dataList.size();
            System.out.println(size + " ENTRIES FOUND:");
            
            Player currentPlayer;
            Iterator iter = dataList.iterator();
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                System.out.print("USERNAME: " + currentPlayer.name);
                System.out.print("\t\t EMAIL: " + currentPlayer.emailAddress);
                System.out.println("\t PASSWORD: " + currentPlayer.password);
            }
        }
    }
      
}


//GAME STATE IO LIST
//For managing a persistent list of GameStates
class GameStateIOList extends IOList
{
    //Default constructor
    //Requires a file name to store the games
    GameStateIOList(String file)
    {
        super(file);
    }
    
    //GET GAME STATES
    //Returns a list of all games matching the playerName to ether player or matching the id number
    public synchronized HashSet<GameState> getGamesList(String playerName, int id)
    {
        HashSet<GameState> gameList = new HashSet();
        gameList.clear();
        
        Iterator iter = dataList.iterator();
        GameState thisState;
        
        if (playerName == null)
        {
            return gameList;
        }
        
        else
        {
            while (iter.hasNext())
            {
                thisState = (GameState)iter.next();

                //If the id o either play names match, add it to the list
                if ((thisState.idNumber == id) || (thisState.playerOne.name.equals(playerName)) || (thisState.playerTwo.name.equals(playerName)))
                {
                    gameList.add(thisState);
                }
            }
        }
        
        return (gameList);
    }
    
    //GET GAME BY ID
    //Returns a GameState matching the id provided
    private synchronized GameState getGameByID(int id)
    {
        GameState thisState, returnState = null;
        Iterator iter = dataList.iterator();
        
        while (iter.hasNext())
        {
            thisState = (GameState)iter.next();
            
            if (thisState.idNumber == id)
            {
                returnState = thisState;
            }
        }
        
        return returnState;
    }
    
    
    public synchronized void removeGameState(GameState removeMe)
    {
        
    }
    
    private synchronized void remove(GameState removeMe)
    {
        
    }
    
    public synchronized void updateGameState(GameState updateMe)
    {
        GameState targetGame = getGameByID(updateMe.idNumber);
        
        //If a game matching the id is found, strike it from the list and replace it with the updated version
        //and commit to disk
        if (targetGame != null)
        {
            dataList.remove(targetGame);
            dataList.add(updateMe);
            writeToDisk();
            System.out.println("UPDATED game #" + updateMe.idNumber + ": " + updateMe.playerOne.name + " vs " + updateMe.playerTwo.name);
        }
        
        else
        {
            dataList.add(updateMe);
            System.out.println("ADDED game #" + updateMe.idNumber + ": " + updateMe.playerOne.name + " vs " + updateMe.playerTwo.name);
            writeToDisk();
        }
    }
    
        //DISPLAY ALL
    //Prints all user data to System.out
    public synchronized void displayAll()
    {
        //OUTPUT DATA
        if (dataList != null)
        {
            GameState temp;
            int size = dataList.size();
            System.out.println(size + " ENTRIES FOUND:");
            
            Iterator iter = dataList.iterator();
            
            while (iter.hasNext())
            {
                temp = (GameState)iter.next();
                System.out.println("#" + temp.idNumber + " / " + temp.playerOne.name + " vs " + temp.playerTwo.name + " / turn " + temp.turnNumber);
            }
        }
    }
    
    
    //READ FROM DISK (NO ARGS)
    //Reads from the dataFileName supplied at instantiation.
    public synchronized boolean readFromDisk()
    {
        if (dataFileName == null)
            return false;
        else
            return (readFromDisk(dataFileName));
    }
    
    
    //READ FROM DISK (WITH ARG)
    //Reads a data structure from specified file
    public synchronized boolean readFromDisk(String file)
    {
        try
        {
            in = new FileInputStream(file);
            objectIn = new ObjectInputStream(in);
            
            dataList = (DataList)objectIn.readObject();
            
            in.close();
            return (true);
        }
        
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("IOList ERROR: problem reading " + file);
            System.out.println(e.toString());
            return (false);
        }
       
        
    }
    
}





//DATA LIST
//This is basically a serialized HashSet (no duplicate entries!)
class DataList<Object> extends HashSet implements Serializable
{ 
    
}





