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
                HeavyTank tank1 = new HeavyTank(1);
                allUnits.add(tank1);
                player1.units.add(tank1);

                Howitzer how = new Howitzer(2);
                allUnits.add(how);
                player1.units.add(how);

                GEV gev = new GEV(3);
                allUnits.add(gev);
                player1.units.add(gev);

                Infantry troop1 = new Infantry(4,1);
                allUnits.add(troop1);
                player1.units.add(troop1);

                Infantry troop2 = new Infantry(8,2);
                allUnits.add(troop2);
                player1.units.add(troop2);
                
                MissileTank msl1 = new MissileTank(5);
                allUnits.add(msl1);
                player1.units.add(msl1);

                CommandPost cmdPost = new CommandPost(7);
                allUnits.add(cmdPost);
                player1.units.add(cmdPost);
                
                //Add player two's single ogre unit
                Ogre ogre3 = new Ogre(3);
                allUnits.add(ogre3);
                player2.units.add(ogre3);
                
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
