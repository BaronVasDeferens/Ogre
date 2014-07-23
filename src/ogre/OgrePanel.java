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
    private final int SLEEP_INTERVAL = 10;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    //private static final int BLOCK_SIZE = 30; //in pixels
    
    //for the hex map
    HexMap hexMap;
    public int hexSide = 64;
    public final int HEX_ROWS = 15;
    public final int HEX_COLS = 21;

    public final int VIEW_WINDOW_WIDTH = 800;
    public final int VIEW_WINDOW_HEIGHT = 600;
    
    
    //DRAWING STUFF
    private Thread animator;
    private volatile boolean running = false;    
    private volatile boolean gameOver = false;
    
    private Graphics dbg;
    private Image dbImage = null;
    
    java.util.Random rando;

    java.awt.Polygon selectedHex;
    
    //User-interaction flags
    boolean scrolling = false;
    int scrollingX, scrollingY;     //stores prior position of mouse to compare
    int currentWindowX, currentWindowY;   //stores current position of the upper corner of the view window
    float zoomFactor = 1.0F;
    
    LinkedList<Unit> allUnits = null;
    LinkedList<Unit> selectedUnits = null;
    //LinkedList<Hex> adjacentHexes = null;
    
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
        
        hexMap = new HexMap(HEX_ROWS,HEX_COLS, hexSide);
        hexMap.setMinimumMapSize(VIEW_WINDOW_WIDTH,VIEW_WINDOW_HEIGHT);
        hexMap.setupMap();
     
        currentWindowX = 0;
        currentWindowY = 0;
        
        //Initialize lists
        allUnits = new LinkedList();
        allUnits.clear();
        selectedUnits = new LinkedList();
        selectedUnits.clear();
        //adjacentHexes = new LinkedList();
        //adjacentHexes.clear();
        
        HeavyTank tank1 = new HeavyTank(1);
        allUnits.add(tank1);
        
        Howitzer how = new Howitzer(2);
        allUnits.add(how);
        
        GEV gev = new GEV(3);
        allUnits.add(gev);
        
        Infantry troop1 = new Infantry(4);
        allUnits.add(troop1);
        
        MissileTank msl1 = new MissileTank(5);
        allUnits.add(msl1);
        
        Ogre ogremk3 = new Ogre(3);
        allUnits.add(ogremk3);
        
        CommandPost cmdPost = new CommandPost(7);
        allUnits.add(cmdPost);
        
        
        hexMap.addUnit(hexMap.getHexFromCoords(0,0), tank1);
        hexMap.addUnit(hexMap.getHexFromCoords(1,1), how);
        hexMap.addUnit(hexMap.getHexFromCoords(2,2), gev);
        hexMap.addUnit(hexMap.getHexFromCoords(3,3), troop1);
        hexMap.addUnit(hexMap.getHexFromCoords(4,4), msl1);
        hexMap.addUnit(hexMap.getHexFromCoords(5,5), ogremk3);
        hexMap.addUnit(hexMap.getHexFromCoords(6,6), cmdPost);
        
        hexMap.updateMapImage();
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

    
    //*** ADD NOTIFY
    //A mystery
    @Override
    public void addNotify()
    {
        //Creates the peer, starts the game
        super.addNotify();
        startGame();
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
            System.out.println("ERROR: dbImage is null");
            return;
        }
        
        else
        {
            dbg = dbImage.getGraphics();
        }
        
        Graphics bigMapGraphics = hexMap.getImage().getGraphics();
        BufferedImage temp = hexMap.getImage();

        //make sure the new image is big enough
        if (((currentWindowX + 800) <= temp.getWidth()) && ((currentWindowY + 600) <= temp.getHeight()))
            temp = temp.getSubimage(currentWindowX, currentWindowY, 800, 600);
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
            System.out.println("Graphics context error:" + e);
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
    
    @Override
    public void mouseClicked(MouseEvent e)
    {   
        //*LEFT* CLICK
//        if (e.getButton() == MouseEvent.BUTTON1)
//        {
//            java.awt.Polygon candidate = hexMap.getPolygon(e.getX()+currentWindowX, e.getY()+currentWindowY);
//            if (candidate != null)
//            {
//                Hex thisHex = hexMap.getHexFromPoly(candidate);
//                
//                if (thisHex != null)
//                {
//                    if (thisHex.isSelected() == false)
//                    {
//                        thisHex.select();
//                        hexMap.updateMapImage();
//                    }
//                    else
//                    {
//                        thisHex.deselect();
//                        hexMap.updateMapImage();
//                    }    
//                }
//            }
//              
//        }//mouse
        
        
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
            if (candidate != null)
            {
                Hex thisHex = hexMap.getHexFromPoly(candidate);
                
                if (thisHex != null)
                {
                    if (thisHex.isSelected() == false)
                    {
                        hexMap.select(thisHex);
                        hexMap.updateMapImage();
                    }
                    else
                    {
                        hexMap.deselect(thisHex);
                        hexMap.updateMapImage();
                    }    
                }
            }
              
        }//mouse
        
        
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            scrolling = true;

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
        }
    }
    
    
    
}
