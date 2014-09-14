package ogre;



/*
DATA WRITER
by Skot West
Creates and maintains a LinkedList of Objects. This list is then able to be written/read
to/from disk. The structre and content of the data structure are preserved.

NOTE: this version of IOList has been modified to operate on Player objects only.
I may come back later and make this data structure capable of handling any type of Object.
 */
import java.util.*;
import java.io.Serializable;
import java.io.*;

/**
 *
 * @author Skot
 */
public class IOList 
{
    private DataList<Player> playerList;
    
    private String dataFileName = null;
    
    private FileOutputStream out;
    private FileInputStream in;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    
    //DEFAULT CONSTRUCTOR
    public IOList()
    {
        playerList = new DataList();
        playerList.clear();
        
        dataFileName = null;
    }
    
    //CONSTRUCTOR WITH ARG
    //Sets a specified dataFileName which will be read from an saved to.
    public IOList(String arg)
    {
        this();
        dataFileName = arg;
    }
    
    //ADD USER
    //Determines whether or not the candidate to add has a unique name
    //If so, add it to the list
    public boolean addPlayer(Player addMe)
    {
        if (addMe == null)
            return false;
        
        else if (playerList.isEmpty())
        {
            playerList.add(addMe);
            writeToDisk();
            return (true);
        }
        else
        {
            if (hasEntryNamed(addMe.name) == false)
            {
                playerList.add(addMe);
                writeToDisk();
                return (true);
            }

            else
                return(false);
        }
    }
    

    
    //MATCHES
    //Returns true if an exact match (both name an password) is found
    public boolean matches(Player findMe)
    {
        if (playerList == null)
            return (false);
        else
        {
            Iterator iter = playerList.iterator();
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
    public Player getPlayerMatching(Player findMe)
    {
        if (playerList == null)
            return (null);
        
        else
        {
            Iterator iter = playerList.iterator();
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
    
    //HAS ENTRY NAMED
    //Scans through the records and returns truw if there is an entry matching the argument
    public boolean hasEntryNamed(String user)
    {
        if (playerList == null)
            return false;
        
        else if (playerList.size() == 0)
            return (false);
        else
        {
            Iterator iter = playerList.iterator();
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
    
    //GET
    public Player get(String name)
    {
        return null;
    }
    
    public Iterator iterator()
    {
        return playerList.iterator();
    }
    
    
    //REMOVE USER
    public boolean remove(String deleteMe)
    {        
        if (playerList == null)
            return false;
        else
        {
            Player currentPlayer;
            Iterator iter = playerList.iterator();
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                if(currentPlayer.name.equals(deleteMe))
                {
                    playerList.remove(currentPlayer);
                    writeToDisk();
                    return(true);
                }
            }
            
            return(false);
        }
    }
    
    //REMOVE ALL USERS
    public void clearAllPlayers()
    {
        if (playerList != null)
        {
            playerList.clear();
            writeToDisk();
        }
        
    }
    
    
    //OUTPUT
    //Prints all user data to System.out
    public void displayAll()
    {
        //OUTPUT DATA
        if (playerList != null)
        {
            Player temp;
            int size = playerList.size();
            System.out.println(size + " ENTRIES FOUND:");
            
            Player currentPlayer;
            Iterator iter = playerList.iterator();
            
            while (iter.hasNext())
            {
                currentPlayer = (Player)iter.next();
                
                System.out.print("USERNAME: " + currentPlayer.name);
                System.out.print("\t EMAIL: " + currentPlayer.emailAddress);
                System.out.println("\t PASSWORD: " + currentPlayer.password);

            }
        }
    }
    

    
    //READ FROM DISK (NO ARGS)
    //Reads from the dataFileName supplied at instantiation.
    public boolean readFromDisk()
    {
        if (dataFileName == null)
            return false;
        else
            return (readFromDisk(dataFileName));
    }
    
    
    //READ FROM DISK (WITH ARG)
    //Reads a data structure from specified file
    public boolean readFromDisk(String file)
    {
        try
        {
            in = new FileInputStream(file);
            objectIn = new ObjectInputStream(in);
            
            playerList = (DataList)objectIn.readObject();
            
            in.close();
            return (true);
        }
        
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("IOList ERROR: problem reading " + file);
            return (false);
        }
       
        
    }
    
    //WRITE TO DISK (NO ARGS)
    public boolean writeToDisk()
    {
        if (dataFileName == null)
            return (false);
        else
            return (writeToDisk(dataFileName));
    }
    
    //WRITE TO DISK
    //Commits the contents of the data structure to disk
    //Returns true upon success.
    public boolean writeToDisk(String file)
    {
        //WRITE TO DISK
        try
        {
            out = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(out);
            
            objectOut.writeObject(playerList);
            
            out.close();
            return (true);
            
        }
        
        catch(IOException e)
        {
            System.out.println("IOList: error writing to " + file);
            return (false);
        }
        
    }
      
}

//DATA LIST
//This is basically a serialized HashSet (no duplicate entries!)
class DataList<Object> extends HashSet implements Serializable
{
    
    
}