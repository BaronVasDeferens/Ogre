/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.util.*;
/**
 *
 * @author skot
 */
public class UnitPlacementManager 
{
    public static void placeUnits(HexMap hexMap, LinkedList<Unit> allUnits)
    {
        //create a unit placement frame 
        Iterator iter = allUnits.iterator();
        Unit thisUnit;
        
        
        int x = 0, y = 0;
        while (iter.hasNext())
        {
            thisUnit = (Unit)iter.next();
            hexMap.addUnit(x,y, thisUnit);
            x++;
            y++;
        }
        
    }
}
