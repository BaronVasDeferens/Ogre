/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;


import java.util.LinkedList;
/**
 *
 * @author Skot
 */
public class Scenario 
{
    
    LinkedList<Unit> allUnits;
    
    Scenario(int selection, Player player1, Player player2)
    {
        allUnits = new LinkedList();
        allUnits.clear();        
        
        
        switch (selection)
        {
            //TEST (0)
            //Player One: defender
            //Player Two: Ogre, attacker
            case 0:
            default:
                
                //TEST SCENARIO
                player1.units.add(new Infantry(4,1));
                player1.units.add(new Infantry(8,2));
                player1.units.add(new Infantry(9,3));

                //Add player two's single ogre unit
                player2.units.add(new Ogre(3));
                
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
