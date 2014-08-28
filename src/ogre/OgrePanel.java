/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.util.Enumeration;

/**
 *
 * @author Skot
 */
public class OgrePanel extends javax.swing.JPanel implements Runnable, KeyListener, MouseListener, MouseWheelListener {

    //CONSTANTS
    private final int SLEEP_INTERVAL = 5;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    public final int VIEW_WINDOW_WIDTH = 800;
    public final int VIEW_WINDOW_HEIGHT = 600;
    
    //DRAWING STUFF
    private Thread animator;
    private volatile boolean running = false;    
    private volatile boolean gameOver = false;
    private Graphics dbg;
    private Image dbImage = null;
    
    java.util.Random rando;
    
    HexMap hexMap;
    int hexSide;
    
    OgreGame gameMaster;
    
    //User-interaction flags
    boolean scrolling = false;
    int scrollingX, scrollingY;     //stores prior position of mouse to compare
    int currentWindowX, currentWindowY;   //stores current position of the upper corner of the view window
    float zoomFactor = 1.0F;
    
    //LinkedList<Unit> allUnits = null;
    //LinkedList<Unit> selectedUnits = null;
    //LinkedList<Hex> adjacentHexes = null;
    
    int gamePhase = 11;
    
    //CONSTRUCTOR
    public OgrePanel() 
    {
        initComponents();
        
        setFocusable(true);
        requestFocus();
        //readyForTermination();
        
        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
         
        rando = new java.util.Random();
        
        setBackground(Color.RED);
        
        setPreferredSize(new Dimension(VIEW_WINDOW_WIDTH,VIEW_WINDOW_HEIGHT));

        currentWindowX = 0;
        currentWindowY = 0;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>                        
    // Variables declaration - do not modify                     
    // End of variables declaration                   

    
    public void setMaster(OgreGame msr)
    {
        gameMaster = msr;
        startGame();
    }
    
    public void setHexMap(HexMap hxmp)
    {
        hexMap = hxmp;
        hexSide = hexMap.getHexSize();

    }
    
    //*** ADD NOTIFY
    //A mystery
    @Override
    public void addNotify()
    {
        //Creates the peer, starts the game
        super.addNotify();
        //startGame();
    }
    
    @Override    
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (animator != null)
        {
            running = false;
        }
    }
   
    //*** START GAME
    private void startGame()
    {
        if ((animator == null) || (running == false))
        {
           animator = new Thread(this);
           animator.start();
        }
    }
    
    
     
    //*** STOP GAME
    public void stopGame()
    {
        running = false;
    }
    
    
    //*** RUN
    //Update, render, sleep
    @Override
    public void run()
    {
        running = true;
        while (running == true)
        {
            gameUpdate();
            gameRender();
            paintScreen();
            
            //Nighty-night
            try
            {
                Thread.sleep(SLEEP_INTERVAL);
            }
            
            catch (InterruptedException ex)
            {
                
            }
        }
        
        System.exit(0);
    }

    
    //*** GAME UPDATE
    private void gameUpdate()
    {
        if (gameOver == false)
        {
            //hexMap.updateMapImage();
        }
    }//gameover != true
    
    
    //GAME RENDER
    private void gameRender()
    {
        
        if (dbImage == null)
        {
            dbImage = createImage(VIEW_WINDOW_WIDTH, VIEW_WINDOW_HEIGHT);
        }
        
        if (dbImage == null)
        {
            gameMaster.reportArea.append("ERROR: dbImage is null");
            return;
        }
        
        else
        {
            dbg = dbImage.getGraphics();
        }
        
        
        //Graphics bigMapGraphics = hexMap.getImage().getGraphics();
        BufferedImage temp = hexMap.getImage();
        Graphics bigMapGraphics = temp.getGraphics();

        //make sure the new image is big enough
        if (((currentWindowX + 800) <= temp.getWidth()) && ((currentWindowY + 600) <= temp.getHeight()))
            temp = temp.getSubimage(currentWindowX, currentWindowY, 800, 600);
        
        //Otherwise set the viewing window to the upper left corner 
        else
        {
            currentWindowX = 0;
            currentWindowY = 0;
            temp = temp.getSubimage(currentWindowX, currentWindowY, 800, 600);
        }

        dbg.drawImage(temp,0,0,800,600, this);

        
        if (scrolling)
        {

            dbg.setColor(Color.RED);
            dbg.drawString("SCROLLING", 10,10);
            dbg.drawString("scrollingX:"+scrollingX, 10, 20);
            dbg.drawString("scrollingY:"+scrollingY, 10, 30);
            dbg.drawString("hexSide:" + hexSide,10,40);

            
            java.awt.PointerInfo pInfo = java.awt.MouseInfo.getPointerInfo();
            
            //check for horizontal scroll
            //Compare where the mouse was at initial click against its current location.
            //If they're different AND the addition of the difference to the current position of the window
            //does not exeed the max size of the big map MINUS the size of the view window (or is less than zero)
            //apply the difference.
            if ((pInfo.getLocation().x != scrollingX) && 
               ((currentWindowX + pInfo.getLocation().x - scrollingX) >= 0) && 
               ((currentWindowX + pInfo.getLocation().x - scrollingX) <= (hexMap.getImage().getWidth() - PANEL_WIDTH))) 
            {
                currentWindowX += (pInfo.getLocation().x - scrollingX)/5;
            }
            
            //Same for vertical scroll
            if ((pInfo.getLocation().y != scrollingY) && 
               ((currentWindowY + pInfo.getLocation().y - scrollingY) >= 0) && 
               ((currentWindowY + pInfo.getLocation().y - scrollingY) <= (hexMap.getImage().getHeight() - PANEL_HEIGHT))) 
            {
                currentWindowY += (pInfo.getLocation().y - scrollingY)/5;
            }
           
        }
        
        if (gameOver == true)
        {
            gameOverMsg(dbg);
        }
        
        bigMapGraphics.dispose();
    }
    
    //PAINT COMPONENT
    @Override
    public void paintComponent(Graphics g)
    {
        if (dbImage != null)
        {
           g.drawImage(dbImage, 0, 0, null); 
        }
    }

    
    //PAINT SCREEN
    //active rendering to the screen
    private void paintScreen()
    {
        Graphics g;
        
        try 
        {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null))
            {
                g.drawImage(dbImage, 0, 0, null);
            }
            
            java.awt.Toolkit.getDefaultToolkit().sync();
            
            g.dispose();
        }
        
        catch (Exception e)
        {
            gameMaster.reportArea.append("Graphics context error:" + e);
        }
        
        
    }
    
    //GAME OVER MSG
    private void gameOverMsg(Graphics g)
    {
        //g.drawString(msg,x,y);
    }
    
    
    //*** KEYBOARD INPUT ***             
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyChar())
        {
            
            default:
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {   
        
        switch (e.getKeyChar())
        {
            
            default:
                break;
               
        }       

    }
   
    //*** MOUSE INPUT ***
    
    //MOUSE WHEEL MOVED
    //Zooms in and out by adjusting the hexSize variable to proscribed values
    //NOTE: the inclusion of the same hexMap.setHexSize in each case SEEMS redundant and dumb, 
    //but it prevents the user from unnecessarily updating the image when she attempt to zoom in further
    //than max zoom (which casues flicker)
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
                
        //Scroll DOWN, zoom IN
        if (e.getWheelRotation() >= 0)
        {           
            switch (hexSide)
            {
                case 20:
                    hexSide = 28;
                    hexMap.setHexSize(hexSide);
                    break;
                case 28:
                    hexSide = 34;
                    hexMap.setHexSize(hexSide);
                    break;
                case 34:
                    hexSide = 36;
                    hexMap.setHexSize(hexSide);
                    break;
                case 36:
                    hexSide = 44;
                    hexMap.setHexSize(hexSide);
                    break;
                case 44:
                    hexSide = 52;
                    hexMap.setHexSize(hexSide);
                    break;
                case 52:
                    hexSide = 64;
                    hexMap.setHexSize(hexSide);
                    break;
                case 64:
                    hexSide = 80;
                    hexMap.setHexSize(hexSide);
                    break;    
                default:
                    break;
            }
            
            //hexMap.setHexSize(hexSide);
            //hexMap.setupMap();
        }
        
        //Scroll UP, zoom OUT
        else
        {
            switch (hexSide)
            {

                case 28:
                    hexSide = 20;
                    hexMap.setHexSize(hexSide);
                    break;
                case 34:
                    hexSide = 28;
                    hexMap.setHexSize(hexSide);
                    break;
                case 36:
                    hexSide = 34;
                    hexMap.setHexSize(hexSide);
                    break;
                case 44:
                    hexSide = 36;
                    hexMap.setHexSize(hexSide);
                    break;
                case 52:
                    hexSide = 44;
                    hexMap.setHexSize(hexSide);
                    break;
                case 64:
                    hexSide = 52;
                    hexMap.setHexSize(hexSide);
                    break;
                case 80:
                    hexSide = 64;
                    hexMap.setHexSize(hexSide);
                    break;
                default:
                    break;
            }
            
            
            
            //hexMap.setupMap();
        }
    }
    
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }
   
   
    //MOUSE CLICKED
    //Invoked when the mouse button comes BACK UP
    @Override
    public void mouseClicked(MouseEvent e)
    {   
        //*LEFT* CLICK
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            
              
        }//LEFT 
        
        
        //*RIGHT* CLICK
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            
        }//if RIGHT BUTTON
    }  
   
    @Override
    public void mousePressed(MouseEvent e)
    {
         if (e.getButton() == MouseEvent.BUTTON1)
        {
            java.awt.Polygon candidate = hexMap.getPolygon(e.getX()+currentWindowX, e.getY()+currentWindowY);
            
            //Here, we determine the context of the click.
            //Possibilities: 
            /*  (MOVE PHASE): player selects the desired unit as a pre-cursor to movement.
                (MOVE PHASE): player clicks on a hex within movement range to move it there. (CAN UNDO)
                (MOVE PHASE): player clicks anywhere other than an unoccupied hex within movement range to cancel the move.
            */
            if (candidate != null)
            {
                Hex thisHex = hexMap.getHexFromPoly(candidate);

                if (thisHex != null)
                {

                    switch (gameMaster.getGamePhase())
                    {
                        //MOVEMENT PHASE (PLAYER ONE)
                        //During movement, ONLY ONE HEX MAY BE SELECTED AT A TIME.                        
                        case 11:
                        case 21:
                            //DESELECT
                            //re-clicking same unit to de-select it
                            if (hexMap.selectedHexes.contains(thisHex))
                            {
                                hexMap.deselectAllSelectedHexes();
                                hexMap.adjacentHexes.clear();
                                gameMaster.updateUnitReadouts(null);
                                hexMap.updateMapImage();
                            }
                        
                            else
                            {
                                //MOVE
                                //Clicking a hex within movement range to move it there IF it hasn't already moved.
                                if ((hexMap.adjacentHexes.contains(thisHex)) && (hexMap.selectedHexes.peek().getUnit().hasMoved == false))
                                {
                                        //A selected hex may not necessarily belong to the current player. Check for ownership
                                        if (gameMaster.currentPlayer.units.contains(hexMap.selectedHexes.peek().getUnit()))
                                        {  
                                            //GameEvent(String tp, Unit agentt, Hex source, Hex destination, int phase, String msg, int id)
                                            MoveEvent moveEvent = new MoveEvent("MOVE", hexMap.selectedHexes.peek().getUnit(), hexMap.selectedHexes.peek(), thisHex, gameMaster.getGamePhase(),"", true);

                                            //Submit the move event to the gameMaster...
                                            if (gameMaster.move(moveEvent))
                                            {
                                                hexMap.updateMapImage();
                                            }

                                            else
                                            {
                                                hexMap.deselectAllSelectedHexes();
                                                hexMap.adjacentHexes.clear();
                                                hexMap.updateMapImage();
                                            }
                                            
                                            gameMaster.updateUnitReadouts(null);
                                        }
                                        else
                                        {
                                            //gameMaster.reportArea.append("nacho cheese");
                                            hexMap.deselectAllSelectedHexes();
                                            hexMap.adjacentHexes.clear();
                                            hexMap.updateMapImage();
                                        }    
  
                                }
                                
                                //SELECT UNIT
                                //Previously unselected; player wishes to select
                                //No selected hexes, thisHex is populated, hasn't moved
                                else if ((hexMap.selectedHexes.isEmpty()) && (thisHex.occupyingUnit.hasMoved == false) && (thisHex.occupyingUnit.isDisabled() == false))
                                {
                                    hexMap.select(thisHex);
                                    
                                   //Add the surrounding hexes to adjacenHexes to display movement
                                    if (thisHex.isOccupied())
                                    {
                                        if (thisHex.occupyingUnit.unitType.equals("OGRE"))
                                        {
                                            Ogre tempOgre = (Ogre)thisHex.occupyingUnit;
                                            hexMap.adjacentHexes.addAll(hexMap.getHexesWithinRange(thisHex,tempOgre.getCurrentMovement()));
                                        }
                                        else    
                                            hexMap.adjacentHexes.addAll(hexMap.getHexesWithinRange(thisHex,thisHex.getUnit().movement));
                                    }    
                                    
                                    //Display stats and weapons in the on-screen list
                                    gameMaster.updateUnitReadouts(thisHex.getUnit());
                                        
                                    
                                    hexMap.updateMapImage();
                                }
                                
                                //DESELECT
                                //User clicked on an invalid hex-- PUNISH THEM
                                else
                                {
                                    hexMap.deselectAllSelectedHexes();
                                    hexMap.adjacentHexes.clear();
                                    gameMaster.updateUnitReadouts(null);
                                    hexMap.updateMapImage();
                                }
                            }
                            break;
                        
                        //*** SHOOTING PHASE
                        //Multiple units may be selected. A single enemy unit may be selected.
                        //In the event that the Ogre is selected as a DEFENDER, only single-unit attacks may be made
                        //against the TRACKS; otherwise, all units may fire.
                        //In the event that the Ogre is the attacker, only a single target may be selected. 
                            
                        //The key here is CONTEXT. Either player may have an ogre at their disposal.
                        //At this stage, we'll just round up everthing the player has selected and send it all
                        //to the gameMaster    
                        case 12:
                        case 22:
                           
                            //A click upon an occupied hex...
                            if (thisHex.occupyingUnit != null)
                            {
                                //FRIENDLY UNIT CLICKED
                                if (gameMaster.currentPlayer.units.contains(thisHex.occupyingUnit))
                                {
                                    //PREVIOUSLY SELECTED FRIENDLY
                                    if (hexMap.selectedHexes.contains(thisHex))
                                    {
                                        //Action: deselect this hex...
                                        hexMap.deselect(thisHex);
                                        
                                        //....Remove any Ogre weapons
                                        if (thisHex.occupyingUnit.unitType.equals("OGRE"))
                                        {
                                            Ogre thisOgre = (Ogre)thisHex.occupyingUnit;
                                            gameMaster.selectedOgreWeapons.removeAll(thisOgre.getWeapons());
                                            gameMaster.currentOgre = null;
                                            gameMaster.updateUnitReadouts(null);
                                        }
                                        
                                        //If this removes the last attacking friendly unit, deselect the targetted unit
                                        if ((gameMaster.currentTarget != null) && (gameMaster.hexMap.selectedHexes.size() >= 1))
                                        {
                                            gameMaster.hexMap.deselect(gameMaster.hexMap.getHexFromCoords(gameMaster.currentTarget.yLocation, gameMaster.currentTarget.xLocation));
                                            gameMaster.updateCurrentTarget(null);
                                        }
                                        
                                        //...adjust firing pattern
                                        hexMap.computeOverlappingHexes(gameMaster.currentPlayer);
                                        hexMap.updateMapImage();
                                        
                                    }
                                    
                                    //PREVIOUSLY UN-SELECTED FRIENDLY UNIT
                                    else
                                    {
                                        //If this unt is NOT disabled NOR already fired, select it 
                                        if (thisHex.occupyingUnit.disabled == false)
                                        {    
   
                                            if (thisHex.occupyingUnit.unitType.equals("OGRE") == false)
                                            {
                                                if (thisHex.occupyingUnit.unitWeapon.dischargedThisRound == false)
                                                {
                                                    hexMap.select(thisHex);
                                                    hexMap.computeOverlappingHexes(gameMaster.currentPlayer);

                                                    //Selecting multiple friendly units may remove a prior target from the combined attack radius.
                                                    //Check:
                                                    if (gameMaster.currentTarget != null)
                                                    {
                                                        if (!gameMaster.hexMap.adjacentHexes.contains(gameMaster.hexMap.getHexFromCoords(gameMaster.currentTarget.yLocation, gameMaster.currentTarget.xLocation)))
                                                        {
                                                            gameMaster.hexMap.deselect(gameMaster.hexMap.getHexFromCoords(gameMaster.currentTarget.yLocation, gameMaster.currentTarget.xLocation));
                                                            gameMaster.updateCurrentTarget(null);
                                                        }
                                                    }

                                                    hexMap.updateMapImage();
                                                }
                                            }
                                            
                                            //If OGRE, populate the weapon list
                                            else
                                            {
                                                hexMap.select(thisHex);
                                                Ogre thisOgre = (Ogre)thisHex.occupyingUnit;
                                                gameMaster.updateOgreWeaponSelectionList(thisOgre);
                                            }
                                        }
                                    }
                                    
                                }
                                
                                //ENEMY UNIT CLICKED
                                //Only one enemy at a time may be selected during the combat phase!
                                else
                                {
                                    //PREVIOUSLY SELECTED ENEMY
                                    if (hexMap.selectedHexes.contains(thisHex))
                                    {
                                        //Action: remove selected hex, set currrentTarget and wepaons to null
                                        hexMap.deselect(thisHex);
                                        //gameMaster.currentTarget = null;
                                        
                                        //Action: remove targettedWeapon 
                                        if (thisHex.occupyingUnit.unitType.equals("OGRE"))
                                        {
                                            gameMaster.targettedOgreWeapon = null;
                                            gameMaster.currentOgre = null;
                                            gameMaster.updateUnitReadouts(null);
                                        }
                                        
                                        gameMaster.updateCurrentTarget(null);
                                    }
                                    
                                    //PREVIOUSLY UN-SELECTED ENEMY
                                    else
                                    {
                                        //Was there a prior target? 
                                        if (gameMaster.currentTarget != null)
                                        {
                                            //Yes. Action: deselect prior target and reassign it to the one just clicked
                                            
                                            //TEST
                                            Hex tmpHex = hexMap.getHexFromCoords(gameMaster.currentTarget.yLocation, gameMaster.currentTarget.xLocation);
                                            if (tmpHex == null)
                                                gameMaster.reportArea.append("nothing there, boss");
                                            
                                            //All targets MUST be in the attack radius (adjacentHexes) to be legitimate targets
                                            if (hexMap.adjacentHexes.contains(thisHex))
                                            {    
                                                hexMap.deselect(hexMap.getHexFromCoords(gameMaster.currentTarget.yLocation, gameMaster.currentTarget.xLocation));
                                                hexMap.select(thisHex);
                                                gameMaster.updateCurrentTarget(thisHex.occupyingUnit);
                                            }
                                        }
                                        
                                        //No prior target. 
                                        //Target MUST be in adjacentHexes attack radius to be considered a legitimate target
                                        else
                                        {
                                            if (hexMap.adjacentHexes.contains(thisHex))
                                            {    
                                                hexMap.select(thisHex);
                                                gameMaster.updateCurrentTarget(thisHex.occupyingUnit);
                                            }
                                        }
                                    }
                                }
                                
                                
                            }
                        
                            //An UNOCCUPIED hex has been clicked: DO NOTHING
                            else
                            {
                                
                            }
                            
                            
                            //Should the ATTACK button be lit?
                            //MIN REQUIREMENTS: a currentTarget; one unit from the current players units
                            //Since target may only be selected from within attack radius (adjacentHexes) which,
                            //in the case of an Ogre, means a weapon has been selected.
                            if ((gameMaster.currentTarget != null) && (gameMaster.hexMap.selectedHexes.size() >= 2))
                            {
                                gameMaster.attackButton.setEnabled(true);
                            }
                            
                            else
                                gameMaster.attackButton.setEnabled(false);
                            
                            break;
                            
                        default:
                            break;
                       
                    }
   
                }
            }
              
        }//mouse
        
        //Right click
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            scrolling = true;
            //hexMap.showCoordinates = true;

            java.awt.PointerInfo pInfo = java.awt.MouseInfo.getPointerInfo();
            scrollingX = pInfo.getLocation().x;
            scrollingY = pInfo.getLocation().y;
        }
        
    }
    
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            scrolling = false;
            //hexMap.showCoordinates = false;
        }
    }
    
    
    
}
