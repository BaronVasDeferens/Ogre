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
    OgreGame myMaster;

    //userCredentials: will be a reference passed from the OgreGame; contains the list of registered users, etc
    LoginObject userCredentials;        
    
    CreateNewGameFrame newGameFrame;
    
    GameState newGameState = null;
    
    NewGameManager(LoginObject trnsObj, OgreGame myMsr)
    {
        myMaster = myMsr;
        userCredentials = trnsObj;
        newGameFrame = new CreateNewGameFrame(this, userCredentials);
        newGameFrame.setVisible(true);
    }
    
    
    public void createNewGame(String opponentName, ScenarioType chosenSecanrio) //add options later
    {
        //Obtains refrerene to specified opponent
        Player thisPlayer, opponent = null;
        Scenario newScenario = null;
        java.util.Iterator iter = userCredentials.registeredPlayers.iterator();
        
        while (iter.hasNext())
        {
            thisPlayer = (Player)iter.next();
            
            if (thisPlayer.name.equals(opponentName))
            {
                opponent = thisPlayer;
            }
        }
        
        if (opponent != null)
        {
            newScenario = new Scenario(userCredentials.player, opponent, ScenarioType.Test);
        }
        
        if (newScenario != null)
        {
            newGameState = new GameState(userCredentials.player, opponent, newScenario);
        }
        
        if (newGameState != null)
        {
            myMaster.setCurrentGameState(newGameState);
        }
        
    }
    
    
}
