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
                Unit maker;
                
                //5 heavy tanks
                maker = new HeavyTank();
                maker.setLocation(12,1);
                player1.units.add(maker);
                
                maker = new HeavyTank();
                maker.setLocation(12,5);
                player1.units.add(maker);

                maker = new HeavyTank();
                maker.setLocation(11,9);
                player1.units.add(maker);
                
                maker = new HeavyTank();
                maker.setLocation(14,12);
                player1.units.add(maker);
                
                maker = new HeavyTank();
                maker.setLocation(14,6);
                player1.units.add(maker);

                //2 howitzers
                maker = new Howitzer();
                maker.setLocation(6,5);
                player1.units.add(maker);
                
                maker = new Howitzer();
                maker.setLocation(5,10);
                player1.units.add(maker);
                
                maker = new Howitzer();
                maker.setLocation(6,5);
                player1.units.add(maker);
                
                //6 3-man infantry
                maker = new Infantry(3);
                maker.setLocation(13,1);
                player1.units.add(maker);
                
                maker = new Infantry(3);
                maker.setLocation(14,3);
                player1.units.add(maker);
                
                maker = new Infantry(3);
                maker.setLocation(14,5);
                player1.units.add(maker);
                
                maker = new Infantry(3);
                maker.setLocation(14,10);
                player1.units.add(maker);
                
                maker = new Infantry(3);
                maker.setLocation(14,14);
                player1.units.add(maker);
                
                maker = new Infantry(3);
                maker.setLocation(6,7);
                player1.units.add(maker);
                
                //1 2-man infantry
                maker = new Infantry(2);
                maker.setLocation(7,4);
                player1.units.add(maker);
                
                //3 GEVs
                maker = new GEV();
                maker.setLocation(14,0);
                player1.units.add(maker);
                
                maker = new GEV();
                maker.setLocation(15,7);
                player1.units.add(maker);
                
                maker = new GEV();
                maker.setLocation(15,11);
                player1.units.add(maker);
                
                //1 command post
                maker = new CommandPost();
                maker.setLocation(1,2);
                player1.units.add(maker);
             
                //Add player two's single ogre unit
                maker = new Ogre(3);
                maker.setLocation(19,7);
                player2.units.add(maker);
                
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