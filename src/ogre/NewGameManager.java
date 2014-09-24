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

import javax.swing.JOptionPane;

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
        
        //get the opponent reference
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
        
        else
            JOptionPane.showMessageDialog(newGameFrame, "Opponent is null",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        
        if (newScenario != null)
        {
            newGameState = new GameState(userCredentials.player, opponent, newScenario);
        }
        else
            JOptionPane.showMessageDialog(newGameFrame, "scenario is null",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        
        if (newGameState != null)
        {
            //TODO: Check to see if there is 
            
            if (myMaster.setCurrentGameState(newGameState))
                newGameFrame.setVisible(false);
            
            else
                JOptionPane.showMessageDialog(newGameFrame, "could not set game state",
                    "ERROR", JOptionPane.WARNING_MESSAGE);
        }
        
        else
            JOptionPane.showMessageDialog(newGameFrame, "gamestate is null",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        
    }
    
    
}
