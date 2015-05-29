/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;


import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
/**
 *
 * @author skot
 */
public class MovementStrategy {
    
    Unit thisUnit;
    Unit primaryTarget = null;
    Hex destination = null;
    
    MovementStrategy(Unit actor, Unit primary, Hex dest) {
        thisUnit = actor;
        primaryTarget = primary;
        destination = dest;
    }
    
    public MoveEvent getMoveEvent(OgreGame gameMaster, GameState gameState) {
        

        MoveEvent move = null;
        
        // There are some things we're going to NEED. Fail if they aren't provided.
        if ((gameMaster == null) || (gameState == null) || (thisUnit.disabled == true))
            return null;
        
        if (primaryTarget != null) {
            
            HexMap hexMap = gameMaster.hexMap;
            Hex from = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(thisUnit.yLocation, thisUnit.xLocation);
            
            // Determine the distance to target and compute a path
            // Generate a target hex for each of the units movement points; each iteration considers
            // the distance from the prior iterations position; the final version is used as the destination hex
            
            Hex candidateHex = from;
            destination = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
            LinkedList<Hex> adjHexes = new <Hex>LinkedList();
            adjHexes.clear();
            LinkedList<Hex> shortList = new <Hex>LinkedList();
            LinkedList<Hex> beenThere = new <Hex>LinkedList();
            beenThere.clear();
            
            for (int i = 0; i < thisUnit.movement; i++) {
                
                adjHexes = hexMap.getAdjacentHexes(candidateHex);
                //adjHexes.removeAll(beenThere);
                
                // Can we reach our destination? If we can move into it, do so
                if ((adjHexes.contains(destination)) && (destination.isOccupied() == false)) {
                    candidateHex = destination;
                    break;
                }
                
                // Are we a ramming Ogre? 
                else if ((adjHexes.contains(destination)) && (destination.isOccupied() == true) && (thisUnit.unitType == UnitType.Ogre)) {
                    gameMaster.reportArea.append("AI Ogre considers ramming...\n");
                    return null;
                }
                
                
                else {
                    // Perform a rough estimate of which way to travel
                    shortList.clear();
                    Iterator adjIter = adjHexes.iterator();
                    Hex tmpHex;
                    int adjCol, adjRow;
                    
                    // Iterate across all the adjacent hexes and see if there is 
                    while (adjIter.hasNext()) {
                        
                        tmpHex = (Hex)adjIter.next();
                        
                        adjRow = candidateHex.getRow() - destination.getRow();
                        adjCol = candidateHex.getCol() - destination.getCol();
                        
                        // System.out.println("adjCol: " + adjCol + ", adjRow: " + adjRow);
                        
                        // Same row or same column? Add it to the short list 
//                        if ((tmpHex.getRow() == adjRow) || (tmpHex.getCol() == adjCol)) {
//                            shortList.add(tmpHex);
//                            System.out.println("Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
//                        }
                        
                        if ((adjCol > 0) && (tmpHex.getCol() < candidateHex.getCol())) {
                            shortList.add(tmpHex);
                            System.out.println("1 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                        }
                        
                        else if ((adjCol < 0) && (tmpHex.getCol() > candidateHex.getCol())) {
                            shortList.add(tmpHex);
                            System.out.println("2 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                        }
                        
                        if ((adjRow > 0) && (tmpHex.getRow() < candidateHex.getRow())) {
                            shortList.add(tmpHex);
                            System.out.println("3 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                        }
                        
                        else if ((adjRow < 0) && (tmpHex.getRow() > candidateHex.getRow())) {
                            shortList.add(tmpHex);
                            System.out.println("4 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                        }
                    }
                    
                    // If there was anything in the short list, choose a random hex and use it for next iteration
                    if (shortList.size() > 0) {
                        Random rando = new Random();
                        candidateHex = shortList.get(rando.nextInt(shortList.size()));
                        
                        while ((beenThere.contains(candidateHex)) && (shortList.size() > 0) && (candidateHex.isOccupied())) {
                            candidateHex = shortList.poll();
                            System.out.println("************* no good....");
                        }
                        
                        System.out.println(thisUnit.unitName + " (" + i + ") CHOSE " + thisUnit.xLocation + "," + thisUnit.yLocation + " -> " + candidateHex.getCol() + "," + candidateHex.getRow());
                        beenThere.addAll(adjHexes);
                        shortList.clear();
                    }
                    
                }
                
                
            }
            
            // If there were no legal moves, return null
            if (candidateHex == null)
                return null;
            
            //MoveEvent(String typ, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
            move = new MoveEvent("MOVE", thisUnit, from, candidateHex, gameMaster.gamePhase, "", false);
            
        }
        
        else {
            
        }
        
        return move;

    }
    
}