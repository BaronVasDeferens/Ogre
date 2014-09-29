/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.LinkedList;
import java.io.Serializable;



enum ScenarioType
{
    MkIII, MkV, Custom, Test
}

class ScenarioDescription
{
    public String description(ScenarioType selection)
    {
        String returnString = null;
        
        switch (selection)
        {
            case MkIII:
                returnString = "A single Ogre MkIII vs 20 points of armor and infantry. Goal is to destroy/defend an immboile command post.";
                break;
            case MkV:
                returnString = "A single Ogre MkV vs 40 points of armor, infantry; goal is to defend/destroy an immobile command post.";
                break;
            case Test:
                returnString = "Freeform scenario. No set victory conditions. For testing.";
                break;
            case Custom:
                returnString = "Design your own scenario!";
                break;
            default:
                returnString = "err";
                break;
        }
        
        return returnString;   
    }
}


enum VictoryCondition
{
    CommandPostDestroyed, OgreNeutralized, LastManStanding, None, Test
}

// SCENARIO
// Encapsulates the units and victory conditions of a game
public class Scenario implements Serializable
{
    
    ScenarioType scenarioType;
    VictoryCondition player1VictoryCondition, player2VictoryCondition;
    
    LinkedList<Unit> allUnits;
    
    Scenario(Player player1, Player player2, ScenarioType type)
    {
        allUnits = new LinkedList();
        allUnits.clear();        
         
        scenarioType = type;
        
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