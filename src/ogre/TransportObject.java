/*
TransportObject
The basic unit of communication between the Ogre clients and server
 */

package ogre;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.*;
/**
 *
 * @author skot
 */
public class TransportObject implements Serializable
{
    //private static final long serialVersionUID = -6903933977591709194L;
    private static final long serialVersionUID = -1301313013130130130L;
    
    //Seven basic client-server interactions are covered by these flags:
    boolean isRegistrationRequest;         //true when creating a new user entry
    boolean isLoginRequest;                //true when logging in
    boolean isLogoutRequest;               //true when logging out
    boolean createNewGameRequest;          //true when sending a new gameState to be written
    boolean checkoutGameStateRequest;      //true when requesting a specific game for checkout
    boolean commitGameStateRequest;        //true when commiting a gameState to server

    String username;
    String message;
    
    TransportObject()
    {
        isRegistrationRequest = false;
        isLoginRequest = false;
        isLogoutRequest = false;
        createNewGameRequest = false;
        checkoutGameStateRequest = false;
        commitGameStateRequest = false;
        
        username = null;
        message = null;
    }
    
    TransportObject(String usrNm, String msg)
    {
        this();
        
        username = usrNm;
        message = msg;
    }
    
}


class RegistrationObject extends TransportObject
{
    String password;
    String emailAddress;
    
    RegistrationObject(String usrnm, String passwd, String emailAddy)
    {
        super(usrnm, null);
        password = passwd;
        emailAddress = emailAddy;
        
        isRegistrationRequest = true;
        isLoginRequest = true;
        
    }
}


class LoginObject extends TransportObject
{
    String password;
    String emailAddress;
    
    Player player;
    
    HashSet<GameState> gameStateList;
    HashSet<Player> registeredPlayers;
    
    LoginObject(String usr, String passwd)
    {
        super(usr, null);
        password = passwd;
        
        player = null;
        gameStateList = null;
        registeredPlayers = null;
        
        isLoginRequest = true;
    }
    
    LoginObject(Player plyr, String msg)
    {
        super();
        player = plyr;
        message = msg;
        
        gameStateList = null;
        registeredPlayers = null;
        
        isLoginRequest = true;
    }
    
    LoginObject (Player plyr, String msg, HashSet games, HashSet players)
    {
        this(plyr, msg);
        
        gameStateList = games;
        registeredPlayers = players;
    }
}

class GameStateUploadObject extends TransportObject
{
    
    GameState gameStateToCommit;
    
    GameStateUploadObject(LoginObject creds, GameState state)
    {
        super(creds.username, null);
        
        commitGameStateRequest = true;
        gameStateToCommit = state;
    }
}