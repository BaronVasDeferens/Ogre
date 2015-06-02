/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.util.Iterator;

/**
 *
 * @author skot
 */
public class PlayerAI extends Player {
     
    PlayerAI(String name) { super(name); }
    
    public void act(OgreGame gameMaster, GameState gameState) {
        
        // Decide what's going on.
        switch (gameMaster.phaseType) {
            case SETUP:
                break;
            case MOVE:
                move(gameMaster, gameState);
                endTurn(gameMaster, gameState);
                break;
            case SHOOT:
                break;
            case SECONDMOVE:
                break;
            default:
                endTurn(gameMaster, gameState);
        }
        
    }
    
    private void move(OgreGame gameMaster, GameState gameState) {
        
        // Makes each unit perform a move
        
        // Iterate across all units under AI control
        Iterator allUnits = gameState.currentPlayer.units.iterator();
        Unit currentUnit;
        Hex currentHex = null;
        Hex targetHex = null;
        MoveEvent me;
        java.util.Random rando = new java.util.Random();
        
        // Announce the move:
        gameMaster.reportArea.append(gameState.currentPlayer.name + " is MOVING...\n");
        
        while (allUnits.hasNext()) {
            
            // select the hex the current unit is in, all hexes within movement range, update readouts
            currentUnit = (Unit)allUnits.next();
            currentHex = gameMaster.hexMap.hexArray[currentUnit.yLocation][currentUnit.xLocation];
            
            // Perform any AI moves
            if (currentUnit.moveStrategy != null) {
                
                me = currentUnit.moveStrategy.getMoveEvent(gameMaster, gameState);
                
                if (me != null) {
                    gameMaster.hexMap.select(currentHex);
                    gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getHexesWithinRange(currentHex,currentHex.getUnit().movement,false,false, gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer)));
                    gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                    gameMaster.updateUnitReadouts(currentUnit);

                    delay(1000);

                    gameMaster.hexMap.highlightHex(me.destination);
                    gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                    delay(500);

                     // Upon a successful move, report it
                    if (gameMaster.move(me)) {
                        gameMaster.reportArea.append(me.message + "\n");
                    }

                    gameMaster.hexMap.deselectAllSelectedHexes();
                    gameMaster.hexMap.removeHighlights();
                    gameMaster.hexMap.adjacentHexes.clear();
                    gameMaster.ogrePanel.hexMapRenderer.updateMapImage();

                    currentHex = null;
                    targetHex = null;
                    delay(1000);
                }
            }
            
            // Only make a move if the current unit isn't disabled and has somewhere to go
            else if((currentHex != null) && (currentUnit.disabled == false)) {
                gameMaster.hexMap.deselectAllSelectedHexes();
                gameMaster.hexMap.adjacentHexes.clear();
                
                // Center on the active unit
                //TODO: center on currentUnit
                
                gameMaster.hexMap.select(currentHex);
                gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getHexesWithinRange(currentHex,currentHex.getUnit().movement,false,false, gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer)));
                gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                gameMaster.updateUnitReadouts(currentUnit);
                
                delay(1000);
                   
                // Show all viable moves
                gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getHexesWithinRange(currentHex, currentUnit.movement, false, true, gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer)));
                targetHex = gameMaster.hexMap.adjacentHexes.get(rando.nextInt(gameMaster.hexMap.adjacentHexes.size()));
                
                // If the hex is valid, try making a move
                if (targetHex != null) {
                    
                    // Highlight the target hex...
                    gameMaster.hexMap.highlightHex(targetHex);
                    gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                    delay(500);
                    
                    me = new MoveEvent("MOVE", currentUnit, currentHex, targetHex, gameMaster.gamePhase, "", false);

                    // Upon a successful move, report it
                    if (gameMaster.move(me)) {
                        gameMaster.reportArea.append(me.message + "\n");
                    }

                }
                
                
                gameMaster.hexMap.deselectAllSelectedHexes();
                gameMaster.hexMap.removeHighlights();
                gameMaster.hexMap.adjacentHexes.clear();
                gameMaster.ogrePanel.hexMapRenderer.updateMapImage();

                currentHex = null;
                targetHex = null;
                delay(1000);
            }
            
        }
        
        gameMaster.reportArea.append(gameState.currentPlayer.name + " MOVE finished.\n");
        
    }
    
    private void shoot(OgreGame gameMaster, GameState gameState) {
        
    }
    
    private void secondMove(OgreGame gameMaster, GameState gameState) {
        
    }
    
    private void endTurn(OgreGame gameMaster, GameState gameState) {
     
        gameMaster.hexMap.deselectAllSelectedHexes();
        gameMaster.hexMap.adjacentHexes.clear();
        gameMaster.updateUnitReadouts(null);
    }
    
    private void delay(int millis) {
        try { Thread.sleep(millis); }
        catch (Exception e) { }
    }
}
