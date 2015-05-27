/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

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
        
        // make a dumb move for now
        
        // Find a valid, unmoved unit
        Unit currentUnit = gameState.currentPlayer.units.peekFirst();
        Hex currentHex = null;
        
        if (currentUnit == null)
            System.out.println("no unit to select...");
        else {
            // select the hex the current unit is in, all hexes within range, update readouts, and sit
            //currentHex = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords( currentUnit.xLocation, currentUnit.yLocation);
            currentHex = gameMaster.hexMap.hexArray[currentUnit.yLocation][currentUnit.xLocation];
            
            if (currentHex == null)
                System.out.println("no hex found for current unit");
            
            else {
                gameMaster.hexMap.deselectAllSelectedHexes();
                gameMaster.hexMap.adjacentHexes.clear();
                gameMaster.hexMap.select(currentHex);
                gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getHexesWithinRange(currentHex,currentHex.getUnit().movement,false,false, gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer)));
                gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                gameMaster.updateUnitReadouts(currentUnit);
                
                try {
                    Thread.sleep(2000);
                }
                catch (Exception e) {
                    
                }
                
                // attempt to move to a hex
                //public MoveEvent(String typ, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
    
                MoveEvent me;
                Hex targetHex;
                //gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getAdjacentHexes(currentHex));
                gameMaster.hexMap.adjacentHexes.addAll(gameMaster.hexMap.getHexesWithinRange(currentHex, currentUnit.movement, false, true, gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer)));
                
                
                targetHex = gameMaster.hexMap.adjacentHexes.peekFirst();
                
                if (targetHex == null) {
                    gameMaster.hexMap.deselectAllSelectedHexes();
                    gameMaster.hexMap.adjacentHexes.clear();
                    gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                    return;
                }    
                
                targetHex = gameMaster.hexMap.adjacentHexes.poll();
                me = new MoveEvent("MOVE", currentUnit, currentHex, targetHex, gameMaster.gamePhase, "", false);
                
                while ((gameMaster.move(me) == false) && (targetHex != null)) {
                    targetHex = gameMaster.hexMap.adjacentHexes.poll();
                    me = new MoveEvent("MOVE", currentUnit, currentHex, targetHex, gameMaster.gamePhase, "", false);     
                }
                gameMaster.hexMap.deselectAllSelectedHexes();
                gameMaster.hexMap.adjacentHexes.clear();
                gameMaster.ogrePanel.hexMapRenderer.updateMapImage();
                
            
            }
        
        }
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
}
