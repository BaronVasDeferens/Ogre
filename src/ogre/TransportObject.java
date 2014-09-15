/*
TransportObject
THe basic unit of communication between the client and server
 */

package ogre;


import java.io.Serializable;
import java.util.LinkedList;
/**
 *
 * @author skot
 */
public class TransportObject implements Serializable
{
    //private static final long serialVersionUID = -6903933977591709194L;
    private static final long serialVersionUID = -1301313013130130130L;
    
    boolean isLogin;
    boolean isRegistration;
    boolean isLogout;
    
    String username;
    String password;
    String emailAddress;
    
    String message;
    
    Player player;
    LinkedList<GameState> gameStateList;
    LinkedList<Player> registeredPlayers; 
    
    TransportObject(String usrnm, String psswrd, String email, boolean login, boolean isReg)
    {
        username = usrnm;
        password = psswrd;
        emailAddress = email;
        isRegistration = isReg;
        isLogin = login;
        player = null;
        gameStateList = null;
        registeredPlayers = null;
        
        message = null;
    }
    
    TransportObject(Player plyr, LinkedList<Player> regPlayers, String msg)
    {
        player = plyr;
        registeredPlayers = regPlayers;
        message = msg;
    }
}
