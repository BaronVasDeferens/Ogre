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
public abstract class MovementStrategy {
    
    Unit thisUnit;
    Unit primaryTarget = null;
    Hex destination = null;
    
    MovementStrategy(Unit actor, Unit primary, Hex dest) {
        thisUnit = actor;
        primaryTarget = primary;
        destination = dest;
    }
    
    public abstract MoveEvent getMoveEvent(OgreGame gameMAster, GameState gameState);
}    
    

// DIRECT ROUTE
// An AI unit with this movement strategy will tend to plot a direct course (ish) toward its 
// specified target, preferring to always keep moving over remaining still

class DirectRoute extends MovementStrategy {

    DirectRoute(Unit actor, Unit primary, Hex dest) {  super(actor,primary, dest);  } 
    
    public MoveEvent getMoveEvent(OgreGame gameMaster, GameState gameState) {
    
        MoveEvent move = null;
        
        // There are some things we're going to NEED. Fail if they aren't provided.
        if ((gameMaster == null) || (gameState == null) || (thisUnit.disabled == true))
            return null;
        
        if (primaryTarget != null) {
        
            HexMap hexMap = gameMaster.hexMap;
            Hex from = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(thisUnit.yLocation, thisUnit.xLocation);
            destination = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
            
            LinkedList<Hex> possibleMoves = new <Hex>LinkedList();
            possibleMoves.clear();
            possibleMoves = gameMaster.hexMap.getHexesWithinRange(
                            from,
                            thisUnit.movement, 
                            false, 
                            (thisUnit.unitType == UnitType.Ogre),
                            gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer));
            
            // We now have a list of all the possible hexes to move to...done't we?
            if (possibleMoves != null) {
                Iterator possibleHexes = possibleMoves.iterator();
                Hex current = null;
                Hex bestHex = from;
                Hex targetHex = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
                
                while (possibleHexes.hasNext()) {
                    
                    current = (Hex)possibleHexes.next();
                    
                    if ( (Math.abs(targetHex.getCol() - current.getCol()) + Math.abs(targetHex.getRow() - current.getRow())) < 
                            (Math.abs(targetHex.getCol() - bestHex.getCol()) + Math.abs(targetHex.getRow() - bestHex.getRow()))) {
                        bestHex = current;
                    }
                }
                
                 hexMap.highlightHex(bestHex);
                 move = new MoveEvent("MOVE", thisUnit, from, bestHex, gameMaster.gamePhase, "", false);
                 
            }
            
        }
        
        return move;
        
    }
    
}
