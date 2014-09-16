/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
