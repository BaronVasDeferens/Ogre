/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.LinkedList;



enum ScenarioType
{
    MkIII, MkV, Custom, Test
}

enum VictoryCondition
{
    CommandPostDestroyed, OgreNeutralized, LastManStanding, None, Test
}

// SCENARIO
// 
public class Scenario 
{
    
    ScenarioType scenarioType;
    VictoryCondition player1VictoryCondition, player2VictoryCondition;
    
    LinkedList<Unit> allUnits;
    
    Scenario(int selection, Player player1, Player player2)
    {
        allUnits = new LinkedList();
        allUnits.clear();        
         
        scenarioType = ScenarioType.Test;
        
        switch (scenarioType)
        {
            //TEST (0)
            //Player One: defender
            //Player Two: Ogre, attacker
            case Test:
            default:
                
                //TEST SCENARIO

                player1.units.add(new HeavyTank(2));
                player1.units.add(new Howitzer(33));
                player1.units.add(new Infantry(8,2));
                player1.units.add(new Infantry(9,3));
                player1.units.add(new GEV(88));
                player1.units.add(new GEV(99));
                player1.units.add(new CommandPost(34));
                
                player1VictoryCondition = VictoryCondition.None;
                
                //Add player two's single ogre unit
                player2.units.add(new Ogre(3));
                player1VictoryCondition = VictoryCondition.None;
                
                player2.flipAllUnitImages();
                
                //Add all to AllUnits
                allUnits.addAll(player1.units);
                allUnits.addAll(player2.units);
                
                break;
        }
    }
    
    //GET ALL UNITS
    //Returns the units associated with the current scenario
    public LinkedList<Unit> getAllUnits()
    {
        return(allUnits);
    }
    
}
