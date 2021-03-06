/*
HEXMAP
Keeps track of a map of hexagons. Contains and manages several sizes/aspects of the same 
map (large, medium, small, for example).

A LITTLE REMINDER ABOUT MULTI-DIMENSIONAL ARRAYS FOR THE AUTHOR
Multi-dimensional arrays are a little tricky. Just keep in mind a few things:
--  When we make an multi-dimensional array, we're making a multiple LAYERS of sequences.
--  So, if you want to end up with a grid that is, say, 7 units wide by 3 units deep, the declaration would be:
--  array[3][7] , or three layers, each seven units long
--  Now, we're sort of slaved to the (x,y) notion of naming a particular position within the grid.
--  For instance, if you want the spot three units over and two down, you have to flip it around to array[2][3]

            TL;DR: think array[ROW][COLUMN] when CREATING
                   but think array[COLUMN][ROW] when ACCESSING WITH AN (X,Y) MENTALITY

                   or [LAYER][COLUMN]

 */

package ogre;

import java.io.Serializable;
import java.util.*;
import java.awt.Polygon;
import java.util.Iterator;

//HEXMAP
public class HexMap implements Serializable
{
    int rows, cols;
    
    Hex hexArray[][];
    
    LinkedList<Hex> hexList;
    LinkedList<Polygon> polyList;
    LinkedList<Hex> selectedHexes;
    LinkedList<Hex> adjacentHexes;
    LinkedList<Hex> highlightedHexes;
 
    int beginDrawingFromX, beginDrawingFromY;

    
    //Constructor
    public HexMap(int rws, int cls, int hexsize)
    {
        rows = rws;
        cols = cls;
        
        hexList = new <Hex>LinkedList();
        hexList.clear();
        
        polyList = new <Polygon>LinkedList();
        polyList.clear();
        
        selectedHexes = new <Hex>LinkedList();
        selectedHexes.clear();
        
        adjacentHexes = new <Hex>LinkedList();
        adjacentHexes.clear();
        
        highlightedHexes = new <Hex>LinkedList();
        highlightedHexes.clear();
        
        hexArray = new Hex[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                hexArray[i][j] = new Hex(i,j,false);
                hexList.add(hexArray[i][j]);
            }
        }

        
        //TODO:
        //This doesn't belong here! Move it after testing
        makeCrater(0,6);
        makeCrater(1,7);
        makeCrater(2,2);
        makeCrater(2,11);
        makeCrater(4,4);
        makeCrater(0,6);
        makeCrater(5,7);
        makeCrater(5,10);
        makeCrater(6,11);
        makeCrater(8,7);
        makeCrater(8,13);
        makeCrater(10,3);
        makeCrater(12,3);
        makeCrater(12,8);
        makeCrater(13,2);
        makeCrater(13,4);
        makeCrater(13,12);
        makeCrater(14,6);
        
        
        //addRidge(hexArray[3][4],6,hexArray[2][3],3);
        //addRidge(hexArray[3][4],1,hexArray[2][4],4);
        
        addRidge(0,1,3,1,1,6);
        addRidge(1,1,5,0,2,2);
        addRidge(1,5,3,2,6,6);
        addRidge(2,5,4,2,6,1);
        addRidge(2,3,3,3,3,6);
        addRidge(3,2,4,3,3,1);
        addRidge(3,2,3,4,3,6);
        addRidge(5,0,5,4,1,2);
        addRidge(5,0,4,5,1,1);
        addRidge(6,1,5,5,1,2);
        addRidge(7,0,3,8,1,6);
        addRidge(9,2,2,10,2,5);
        addRidge(9,2,4,9,3,1);
        addRidge(9,3,3,10,4,6);
        addRidge(9,4,1,9,3,4);
        addRidge(11,3,4,11,4,1);
        addRidge(11,4,2,12,4,5);
        addRidge(3,6,3,4,7,6);
        addRidge(3,7,6,2,7,3);
        addRidge(2,8,3,3,8,6);
        addRidge(1,8,3,2,9,6);
        addRidge(2,9,4,2,10,1);
        addRidge(2,10,2,3,9,5);
        addRidge(1,9,3,2,10,6);
        addRidge(2,10,5,1,10,2);
        addRidge(8,6,3,9,6,6);
        addRidge(11,5,4,11,6,1);
        addRidge(10,6,3,11,6,6);
        addRidge(10,7,2,11,6,5);
        addRidge(10,7,3,11,7,6);
        addRidge(10,7,4,10,8,1);
        addRidge(9,7,2,10,7,5);
        addRidge(13,6,4,13,7,1);
        addRidge(2,15,3,3,15,6);
        addRidge(2,15,4,2,16,1);
        addRidge(4,14,2,5,13,5);
        addRidge(4,13,3,5,13,6);
        addRidge(5,12,4,5,13,1);
        addRidge(1,12,2,2,12,5);
        addRidge(6,13,3,7,13,6);
        addRidge(6,15,3,7,15,6);
        addRidge(7,14,4,7,15,1);
        addRidge(7,15,2,8,15,5);
        addRidge(8,15,4,8,16,1);
        addRidge(9,14,3,10,15,6);
        addRidge(12,15,4,12,16,1);
        addRidge(12,15,3,13,15,6);
        addRidge(13,14,4,13,15,1);
        
        
    }
    
    
    //MAKE CRATER
    //in X,Y notation
    public void makeCrater(int col, int row)
    {
        Hex target = hexArray[row][col];
        
        if (target != null)
        {
            target.isCrater = true;
        }
    }
    
    
    //MAKE CRATER
    public void makeCrater(Hex target)
    {
        if (target != null)
        {
            target.isCrater = true;
        }
    }
    
    //ADD RIDGE
    public void addRidge(int x1, int y1, int f1, int x2, int y2, int f2)
    {
        Hex hex1, hex2;
        
        //TODO:
        //Bounds check: the responsible thing
        
        hex1 = hexArray[y1][x1];
        hex2 = hexArray[y2][x2];
        
        addRidge(hex1,f1,hex2,f2);
    }
    
    //ADD RIDGE
    public void addRidge(Hex h1, int f1, Hex h2, int f2)
    {
        if ((h1 != null) && (h2 != null))
        {
            h1.addRidge(h1, f1, h2, f2);
            h2.addRidge(h2, f2, h1, f1);
        }
    }
 
    
    //*** UNIT MANAGMENT ***
    //Adds a unit to a hex. Checks for crater, existing unit
    public boolean addUnit(Hex target, Unit toAdd)
    {
        if ((target.isCrater() == false) && (target.isOccupied() == false))
        {
            target.setOccupyingUnit(toAdd);
            target.occupyingUnit.xLocation = target.getCol();
            target.occupyingUnit.yLocation = target.getRow();
            //updateMapImage();
            return (true);
        }
        else
            return (false);
    }
    
    
    public boolean addUnit(int row, int col, Unit toAdd)
    {
        //return (addUnit(getHexFromCoords(row,col),toAdd));
        Hex target = hexArray[row][col];
        
        if ((target.isCrater() == false) && (target.isOccupied() == false))
        {
            hexArray[row][col].setOccupyingUnit(toAdd);
            target.occupyingUnit.xLocation = target.getCol();
            target.occupyingUnit.yLocation = target.getRow();
            return true;
        }
        
        else
            return false;
        
    }
    
    
    public void select(Hex thisOne)
    {
        if (this == null) { return; }
        
        if (thisOne.isOccupied())
        {
            selectedHexes.add(thisOne);
            thisOne.select();
        }
    }
    
    // Select Unoccupied Hex
    // For temporarily highlighting a hex
    public void highlightHex(Hex thisOne) {
        if (thisOne != null)
        {
            highlightedHexes.add(thisOne);
        }
    }
    
    public void removeHighlights() {
        highlightedHexes.clear();
    }
    
    public void deselect(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.remove(thisOne);
            thisOne.deselect();           
            //updateMapImage();
        }
    }
    
    
    public LinkedList<Hex> getAdjacentHexes(Hex thisHex)
    {
        return (getAdjacentHexes(thisHex.getRow(),thisHex.getCol()));
    }
    
    
    public LinkedList<Hex> getAdjacentHexes(Polygon thisOne)
    {
        Iterator hexes = hexList.iterator();
        Hex thisHex;
        
        while (hexes.hasNext())
        {
            thisHex = (Hex)hexes.next();
            
            if (thisHex.getPolygon() == thisOne)
            {
                return (getAdjacentHexes(thisHex.getRow(),thisHex.getCol()));
            }
        }
        
        return (null);
        
    }
    
    // ** GET ADJACENT HEXES
    //Returns a list of up to six adjacent hexes to the one specified
    //Based on the weird rules of hexes in 2D arrays
    //NOTE: this function was returning 8 hexes instead of 6 until Sets were introduced. 
    public LinkedList<Hex> getAdjacentHexes(int row, int col)
    {
        java.util.Set<Hex> adjHexes = new java.util.HashSet();
        adjHexes.clear();
        
        //All hexes are adjacent to thsoe ABOVE AND BELOW (the ones in the same column, +- 1);
        
        //ABOVE
        if ((row-1) >= 0)
            adjHexes.add(hexArray[row-1][col]);
        //BELOW
        if ((row+1) <= (rows-1))
            adjHexes.add(hexArray[row+1][col]);
        //RIGHT
        if ((col+1) <= (cols-1))
            adjHexes.add(hexArray[row][col+1]);
        //LEFT
        if((col-1) >= 0)
            adjHexes.add(hexArray[row][col-1]);
        
        //EVEN ROW RULES
        if ((col%2) == 0)
        {
            if (((row-1) >= 0) && ((col+1) <= (cols-1)))
                adjHexes.add(hexArray[row-1][col+1]);
            if (((row-1) >= 0) && ((col-1) >= 0))
                adjHexes.add(hexArray[row-1][col-1]);
            if((col-1) >= 0)
                adjHexes.add(hexArray[row][col-1]);
            if((col+1) <= (cols-1))
                adjHexes.add(hexArray[row][col+1]);
        }
        
        //ODD ROWS
        else if (col%2 != 0)
        {
            if (((row+1) <= (rows-1)) && ((col+1) <= (cols-1)))
                adjHexes.add(hexArray[row+1][col+1]);         
            if (((row+1) <= (rows-1)) && ((col-1) >= 0))
                adjHexes.add(hexArray[row+1][col-1]);
            if((col+1) <= (cols-1))
                adjHexes.add(hexArray[row][col+1]);
            if ((col-1) >= 0)
                adjHexes.add(hexArray[row][col-1]);
        }
                
        return (convertToHexLinkedList(adjHexes));
    }
    
    
    //GET HEXES WITHIN RANGE
    //Returns a list of hexes in a "shell" radius within range.
    //If the ignoreCrater flag is set to false, then craters are considered obstructions
    //Likewise, if ignoreRidges is set to false, hexes which share a ridge will be obstructions
    //Relies upon the recursive version of the function of the same name (below)
    public LinkedList<Hex> getHexesWithinRange(Hex fromHere, int distance, boolean ignoreCrater, boolean ignoreRidges, LinkedList<Hex> obstructedHexes)
    {
        //NOTE: the use of sets here was CRUCIAL to solving this problem.
        //A set, as you know, may not contain duplicates. Duplicate entries caused all manner of grief
        //in debugging. Lordy. I figured it out in the shower, addding weight to the good ol' "Shower Principle."
        //
        
        Set<Hex> tempAdjacents = new HashSet();
        Set<Hex> returnList = new HashSet();
        //LinkedList<Hex> returnList = new LinkedList();
        Set<Hex> doneThese = new HashSet(); //prevents the same hex being called multiple THOUSANDS of times.
        Set<Hex> ignoreThese = new HashSet();
        
        tempAdjacents.clear();
        returnList.clear();
        doneThese.clear();
        ignoreThese.clear();
        
        Iterator iter;
        Hex centerHex, thisHex, tempHex;
        
        if ((fromHere != null) && (distance > 0))
        {            
            //SCENARIO 1: BOTH CRATERS AND RIDGES ARE IGNORED (SHOOTING)
            //If there's no need to process the validity of each hex (crater, ridges), simply
            //add all the concentric rings
            if ((ignoreCrater == true) && (ignoreRidges == true))
            {
               tempAdjacents.addAll(getAdjacentHexes(fromHere));
               returnList.addAll(tempAdjacents);
               doneThese.add(fromHere);
                
                for (int i = 0; i < distance-1; i++)
                {
                    iter = tempAdjacents.iterator();
                    
                    while (iter.hasNext())
                    {
                        thisHex = (Hex)iter.next();
                        
                        if (!doneThese.contains(thisHex))
                        {
                             returnList.addAll(getAdjacentHexes(thisHex));
                             doneThese.add(thisHex);
                        }
                    }
                    
                    tempAdjacents.addAll(returnList);
                }    
                
            }
        
            //SCENARIO 2:
            //RIDGES and CRATERS should be processed/weeded out
            //for MOVEMENT. We should also consider hexes occupied by enemy units to be blocked
            else
            {
                
                //NOTE: strangely, casting the Sets to LinkedLists does not affect their "one instance per list" behavior.
                //(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, LinkedList<Hex> returnList)
                returnList.addAll(getHexesWithinRange(fromHere, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges, obstructedHexes));
                ignoreThese.clear();
                
                //NOTE: the ignoreList is cleared at the end of each "ring's" examination;
                //a hex which is not accessible from one hex (shares a ridge) might be accessible from another.
                //If those "ignored" hexes are allowed to persists from one concentric exam to another,
                //they will appaear as totally inaccessible-- not good.
                
                for (int i = 0; i < distance-1; i++)
                {
                     iter = returnList.iterator();
                     
                     while (iter.hasNext())
                     {
                         thisHex = (Hex)iter.next();
                         tempAdjacents.addAll(getHexesWithinRange(thisHex, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges, obstructedHexes));
                     }
                     
                     if (obstructedHexes != null)
                        tempAdjacents.removeAll(obstructedHexes);
                     
                     returnList.addAll(tempAdjacents);
                     tempAdjacents.clear();
                     ignoreThese.clear();
                }

            }
        }
        
        // Finally, weed out the enemy-occupied hexes
        if (obstructedHexes != null) {
            returnList.removeAll(obstructedHexes);
        }
        
        return (convertToHexLinkedList(returnList));
    }
    
    //GET HEXES WITHIN RANGE
    //My thinking was this:
    //A "center" hex and its 6 neighbors are examined. If there is a ridge between them (and ridges are not ignored),
    //then that hex is inaccessibe from the center hex and is "skipped" for a round of examinations.
    //That hex may be accessible from another neighbor hex, however, and if it can be found in the list of accessible
    //neighbors then it is removed from the skip list at the end.
    public LinkedList<Hex> getHexesWithinRange(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, boolean ignoreRidges, LinkedList<Hex> obstructedHexes)
    {
        
        LinkedList<Hex> returnList = new LinkedList();
        returnList.clear();
        
        if (center != null)
        {
            //Get the surrounding six hexes around the center
            Iterator iter = getAdjacentHexes(center).iterator();
            Hex thisHex;
            
            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();
                
                //If it is in neither the "done" nor "ignore" pile...
                if ((!doneThese.contains(thisHex) && (!ignoreThese.contains(thisHex))))
                {
                    //check for crater
                    if (thisHex.isCrater)
                        ignoreThese.add(thisHex);
                    //Shares a ridge
                    if ((ignoreRidges == false) && ((center.sharesRidgeWithThisHex(thisHex)) && (!doneThese.contains(thisHex))))
                        ignoreThese.add(thisHex);
                    //Contains an enemy Unit
                    //TODO: add this here. What do we need? An OgreUnit will be able to RAM...
                    if (obstructedHexes.contains(thisHex))
                        ignoreThese.add(thisHex);
                    
                    if (!ignoreThese.contains(thisHex))
                    {
                        returnList.add(thisHex);
                    }
                }
                
            }
            
            doneThese.add(center);
              
            
            //Reintorduce hexes which were "skipped" by putting them into the ignore pile if they were in fact
            //accessible from another hex in this "ring." The ignoreList is cleared when all hexes wihin the ring
            //have been examined.
            iter = ignoreThese.iterator();
            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();
                
                if (returnList.contains(thisHex))
                {
                    ignoreThese.remove(thisHex);
                }
            }
                
        }
        
        return returnList;
    }
    
    //DESELECT ALL SELECTED HEXES
    //Cycles through the selectedHexes list and deselects them all, clears the list
    public void deselectAllSelectedHexes()
    {
        Iterator iter = selectedHexes.iterator();
        Hex current;
        
        while (iter.hasNext())
        {
            current = (Hex)iter.next();
            current.deselect();
        }
        
        selectedHexes.clear();
    }
    
    //COMPUTE OVERLAPPING HEXES
    //Gets the overlapping "hexes in common" with  multi-firing wepaon solution...or something.
    //The "comon zone of fire" hexes will be listed in the adjacentHexes list
    public void computeOverlappingHexes(Player currentPlayer, OgreGame gameMaster)
    {
       adjacentHexes.clear();
       
       Iterator iter = selectedHexes.iterator();
       Hex thisHex;
       
       if (iter.hasNext())
       {
           thisHex = (Hex)iter.next();
           //Obtain a friendly unit from the current selections; cycle through til one is found
           while ((!currentPlayer.units.contains(thisHex.occupyingUnit)) && iter.hasNext())
           {
               thisHex = (Hex)iter.next();
           }
           
           //Get a single zone of fire from the friendly unit...
           if ((currentPlayer.units.contains(thisHex.occupyingUnit)) && (thisHex.occupyingUnit.unitWeapon != null))
           {
                adjacentHexes.addAll(getHexesWithinRange(thisHex, thisHex.occupyingUnit.unitWeapon.range,true,true, null));
           }
       }
       
       while (iter.hasNext())
       {
           thisHex =(Hex)iter.next();
           //...and get the "intersection" between the first unit's zone and the rest of the frinedly units' zones
           if ((currentPlayer.units.contains(thisHex.occupyingUnit)) && (thisHex.occupyingUnit.unitWeapon != null))
           {
                adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisHex.occupyingUnit.unitWeapon.range, true, true, null));
           }
       }
       
       //Add in any selectedOgreWeapons
       
       
       iter = selectedHexes.iterator();
       
           
        if (gameMaster.selectedOgreWeapons != null)
        {
            Iterator weaponIter = gameMaster.selectedOgreWeapons.iterator();
            Weapon thisWeapon;
            
            while (iter.hasNext())
            {
                thisHex = (Hex)iter.next();

                if (thisHex.occupyingUnit.unitType == UnitType.Ogre)
                {
                    OgreUnit thisOgre = (OgreUnit)thisHex.occupyingUnit;
                    
                    if ((weaponIter.hasNext()) && (adjacentHexes.size() <= 1))
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        adjacentHexes.addAll(getHexesWithinRange(thisHex, thisWeapon.range, true,true, null));
                    }
                    
                    while (weaponIter.hasNext())
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        
                        if (thisOgre.getWeapons().contains(thisWeapon))
                        {
                            adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisWeapon.range, true,true, null));
                        }
                    }
                }
            }
       }
        
            //updateMapImage();
    }
    
    //CONVERT TO HEX LINKED LIST
    //TODO: consider just overhauling any function arguments which require lists to instead
    //require Sets.
    public LinkedList<Hex> convertToHexLinkedList(Set convertMe)
    {
        LinkedList<Hex> returnList = new LinkedList();
        returnList.clear();
        Iterator iter = convertMe.iterator();
        Hex thisHex;
        
        while (iter.hasNext())
        {
            thisHex = (Hex)iter.next();
            returnList.add(thisHex);
        }
        
        return (returnList);
    }
    
        public LinkedList<Hex> getOccupiedHexes(Player player) {
            Iterator iter = player.units.iterator();
            Unit currentUnit;
            LinkedList returnList = new <Hex>LinkedList();
            returnList.clear();

            while (iter.hasNext()){
                currentUnit = (Unit)iter.next();
                returnList.add(hexArray[currentUnit.yLocation][currentUnit.xLocation]);
            }
            
            return returnList;
    }
    
}

