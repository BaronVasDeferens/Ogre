/*
TransportObject
The basic unit of communication between the Ogre clients and server
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
    
    //Seven basic client server interactions are covered by these flags:
    boolean isRegistration;         //true when creating a new user entry
    boolean isLogin;                //true when logging in
    boolean isLogout;               //true when logging out
    boolean checkoutGameState;      //true when 
    
    
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
