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
    
    Scenario(int selection)
    {
        allUnits = new LinkedList();
        allUnits.clear();        
        
        switch (selection)
        {
            //TEST (0)
            case 0:
            default:
                HeavyTank tank1 = new HeavyTank(1);
                allUnits.add(tank1);

                Howitzer how = new Howitzer(2);
                allUnits.add(how);

                GEV gev = new GEV(3);
                allUnits.add(gev);

                Infantry troop1 = new Infantry(4,1);
                allUnits.add(troop1);

                Infantry troop2 = new Infantry(8,2);
                allUnits.add(troop2);

                MissileTank msl1 = new MissileTank(5);
                allUnits.add(msl1);

                Ogre ogremk3 = new Ogre(3);
                allUnits.add(ogremk3);

                CommandPost cmdPost = new CommandPost(7);
                allUnits.add(cmdPost);
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
