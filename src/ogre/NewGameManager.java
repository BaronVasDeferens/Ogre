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
        
        // Create an AI game or vs remote player
        if (opponentName.equals("Ogre AI"))
            opponent = new PlayerAI("Ogre AI");
        
        else {
        
            //get the opponent reference
            while (iter.hasNext())
            {
                thisPlayer = (Player)iter.next();

                if (thisPlayer.name.equals(opponentName))
                {
                    opponent = thisPlayer;
                }
            }
        }
        
        //Final check: if all is ready, create a new gameState and upload it to the server
        if (opponent != null)
        {
            newGameState = new GameState(userCredentials.player, opponent, chosenSecanrio, myMaster.HEX_ROWS, myMaster.HEX_COLS, myMaster.hexSide);
            
            GameStateUploadManager gameUploader = new GameStateUploadManager(myMaster.server, myMaster.port, userCredentials);
            
            // disable game saving on server if ai
            if ((opponent instanceof PlayerAI) == false)
                gameUploader.uploadGameState(newGameState); 
            
        }
        
        else
            JOptionPane.showMessageDialog(newGameFrame, "scenario is null",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        
        if (newGameState != null)
        {            
            //If the instantiation was successful, set it to the current state and close the window
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
