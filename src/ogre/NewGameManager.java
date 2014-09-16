/*
NEW GAME MANAGER
Accepts:
Handles the creation of GameStates from user's selections of:
-- opponent
-- map size/type
-- victory conditions
-- missions
-- point values, etc

 */

package ogre;

/**
 *
 * @author skot
 */
public class NewGameManager 
{
    //userCredentials: will be a reference passed from the OgreGame; contains the list of registered users, etc
    TransportObject userCredentials;        
    
    CreateNewGameFrame newGameFrame;
    
    GameState newGameState;
    
    NewGameManager(TransportObject trnsObj)
    {
        userCredentials = trnsObj;
        newGameFrame = new CreateNewGameFrame(this, userCredentials);
        newGameFrame.setVisible(true);
    }
    
    
}
