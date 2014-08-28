/*
HEXMAP
Keeps track of a hex map. Contains and manages several sizes/aspects of the same 
map (large, meium, small, fpr example).
 */

package ogre;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.*;

/**
 *
 * @author Skot
 */
public class HexMap 
{
    int rows, cols;
    
    OgreGame gameMaster;
    
    Hex hexArray[][];
    
    LinkedList<Hex> hexList;
    LinkedList<Polygon> polyList;
    LinkedList<Hex> selectedHexes;
    LinkedList<Hex> adjacentHexes;
    
    BufferedImage mapImage;
    BufferedImage offScreenDraw;
    
    int beginDrawingFromX, beginDrawingFromY, hexagonSize;
    int minimumMapWidth, minimumMapHeight;
    
    boolean showCoordinates = true;
    
    //Constructor
    public HexMap(int rws, int cls, int hexsize)
    {
        rows = rws;
        cols = cls;
        
        hexList = new LinkedList();
        hexList.clear();
        
        polyList = new LinkedList();
        polyList.clear();
        
        selectedHexes = new LinkedList();
        selectedHexes.clear();
        
        adjacentHexes = new LinkedList();
        adjacentHexes.clear();
        
        hexArray = new Hex[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                hexArray[i][j] = new Hex(i,j,false);
                hexList.add(hexArray[i][j]);
            }
        }

        hexagonSize = 64;
        minimumMapWidth = 800;
        minimumMapHeight = 600;
        
        setHexSize(hexsize);
        
    }
    
    //SET GAME MASTER
    //give this object awareness of its owner
    public void setMaster(OgreGame msr)
    {
        gameMaster = msr;
    }
    
    //SET HEXAGON SIZE
    public void setHexSize(int newSize)
    {
        hexagonSize = newSize;
        setupMap();
    }
    
    public int getHexSize()
    {
        return (hexagonSize);
    }
    
    //SET MINIMUM MAP SIZE
    //ba-derp
    public void setMinimumMapSize(int minWidth, int minHeight)
    {
        minimumMapWidth = minWidth;
        minimumMapHeight = minHeight;
    }
    
    //CREATE MAP
    //Returns a blank BufferedImage sized according to the size of the hexes to be drawn upon it.
    //If smaller than the viewing window (minX, minY), a BufferedImage of minWidth x minLength is returned instead.
    //Drawing offset coordinates are also adjusted to 2*hexagonSize from 0,0
    private BufferedImage createNewImage()
    {
        
        beginDrawingFromX = 4 * hexagonSize;
        beginDrawingFromY = 4 * hexagonSize;
        

        //The formula for the image size is:
        //(hexSize * 1.5 * cols + ( 4 * hexSize)) by (hexSize * 1.5 * rows + ( 4 * hexSize))
        int sizeX = (int)((hexagonSize * 2 * cols) + (4 * hexagonSize));
        int sizeY = (int)((hexagonSize * 2 * rows) + (4 * hexagonSize));
        
        if (sizeX < minimumMapWidth)
            sizeX = minimumMapWidth;
        if (sizeY < minimumMapHeight)
            sizeY = minimumMapHeight;
        
        BufferedImage newImage = new BufferedImage(sizeX,sizeY,BufferedImage.OPAQUE);
        
        //System.out.println("Created new Image: " + sizeX + " x " + sizeY);
        
        return (newImage);
        
    }
    
    //CREATE MAP
    //Creates an image of a map by drawing a hex field on it
    //OriginX/Y are the coordinates from which drawing shall begin (thr origin)
    public void setupMap()
    {
        if (polyList != null)
            polyList.clear();
        
 
        offScreenDraw = createNewImage();
        Graphics newMapGraphics = offScreenDraw.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,offScreenDraw.getWidth(),offScreenDraw.getHeight());

        
        int x = beginDrawingFromX;
        int y = beginDrawingFromY;

        for (int i = 0; i < rows; i++)
        {
           
           for (int j = 0; j < cols; j++)
           {           
               if ((j%2) != 0)
                    y = beginDrawingFromY + (int)(.8660 * hexagonSize); 
               else
                    y = beginDrawingFromY;
               
               
               java.awt.Polygon p = new java.awt.Polygon();
               p.reset();
               
               p.addPoint(x +(hexagonSize/2), y);
               p.addPoint(x+(hexagonSize/2) + hexagonSize, y);
               p.addPoint(x + 2*hexagonSize, (int)(.8660* hexagonSize + y));
               p.addPoint(x+(hexagonSize/2) + hexagonSize, (int)(.8660 * 2 * hexagonSize + y));
               p.addPoint(x+(hexagonSize/2),(int)(.8660*2*hexagonSize + y));
               p.addPoint(x,y+(int)(.8660 * hexagonSize));
               
               //Draw solid black crater
               if (hexArray[i][j].isCrater())
               {
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.fillPolygon(p);
               }
               
               else
               {
                   newMapGraphics.setColor(Color.WHITE);
                   
                   //Adjacent colorization 
                   if (adjacentHexes.contains(hexArray[i][j]))
                     {
                         newMapGraphics.setColor(Color.PINK);
                         newMapGraphics.fillPolygon(p);
                         newMapGraphics.setColor(Color.BLACK);
                         newMapGraphics.drawPolygon(p);
                         newMapGraphics.setColor(Color.PINK);
                     }

                    //Paint RED on selected hexes
                    if (hexArray[i][j].isSelected())
                    {
                        newMapGraphics.setColor(Color.RED);
                        newMapGraphics.fillPolygon(p);
                    }

                    //Draw Units (if any)
                    if (hexArray[i][j].isOccupied())
                    {
                        //if occupying unit is disabled, "keept it gray"
                        if ((hexArray[i][j].getUnit().isDisabled()) && ((hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }


                        //Rescale and offset
                        BufferedImage unitImage = hexArray[i][j].getUnit().getImage();

                        int Xoffset, Yoffset, imageSize;

                        Xoffset = (int)((2 * hexagonSize)/5.464);
                        Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                        imageSize = (int)(2 * (1.732 * Xoffset));
                        
                        newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, newMapGraphics.getColor(),null);
                        //newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, null); 
                    }
               
               }
               
               //Draw basic polygon
                newMapGraphics.setColor(Color.BLACK);
                newMapGraphics.drawPolygon(p);
               
               //Draw coordinates
                if (showCoordinates)
                {
                    newMapGraphics.setColor(Color.BLUE);
                    newMapGraphics.drawString("[" + (i) + "," + (j) + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
                }

               //associate a hex with this polygon
               associatePolygonWithHex(i,j,p);
               polyList.add(p);
               
               //scoot the pencil over
               x = x + (hexagonSize/2) + hexagonSize;   
               
           }// for j (columns)
        
           //Reset for the next row
           beginDrawingFromY += (int)2 *(.8660 * hexagonSize);
           x = beginDrawingFromX;

           
            if ((i%2) != 0)
               y = beginDrawingFromY + (int)(.8660 * hexagonSize); 
            else
                y = beginDrawingFromY;
            
        }  
           
        newMapGraphics.dispose();
        mapImage = offScreenDraw;
    }
    
    //GET UPDATED MAP IMAGE
    //Updates the map image ONLY. Does not reconfigure polygon or hex lists
    public void updateMapImage()
    {                  

        //mapImage = new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.OPAQUE);
        offScreenDraw = createNewImage();
        
        Graphics newMapGraphics = offScreenDraw.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,offScreenDraw.getWidth(), offScreenDraw.getHeight());

        int x = beginDrawingFromX;
        //int y = beginDrawingFromY;
        int y = beginDrawingFromY;// + (int)(.8660 * hexagonSize); 
        
        //Draw hex field
        for (int i = 0; i < rows; i++)
        {
           
           for (int j = 0; j < cols; j++)
           {           
               if ((j%2) != 0)
                    y = beginDrawingFromY + (int)(.8660 * hexagonSize); 
               else
                    y = beginDrawingFromY;
               
               java.awt.Polygon p = new java.awt.Polygon();
               p.reset();
               
               p.addPoint(x +(hexagonSize/2), y);
               p.addPoint(x+(hexagonSize/2) + hexagonSize, y);
               p.addPoint(x + 2*hexagonSize, (int)(.8660* hexagonSize + y));
               p.addPoint(x+(hexagonSize/2) + hexagonSize, (int)(.8660 * 2 * hexagonSize + y));
               p.addPoint(x+(hexagonSize/2),(int)(.8660*2*hexagonSize + y));
               p.addPoint(x,y+(int)(.8660 * hexagonSize));
               
                //Draw solid black crater
               if (hexArray[i][j].isCrater())
               {
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.fillPolygon(p);
               }
               
               else
               {                
                    newMapGraphics.setColor(Color.WHITE);

                    //Adjacent hex colorization
                    if (adjacentHexes.contains(hexArray[i][j]))
                    {
                         newMapGraphics.setColor(Color.PINK);
                         newMapGraphics.fillPolygon(p);
                         newMapGraphics.setColor(Color.BLACK);
                         newMapGraphics.drawPolygon(p);
                         newMapGraphics.setColor(Color.PINK);
                     }

                    //Paint RED on selected hexes
                    if (hexArray[i][j].isSelected())
                    {
                        newMapGraphics.setColor(Color.RED);
                        newMapGraphics.fillPolygon(p);
                        //newMapGraphics.setColor(Color.BLACK);
                        //newMapGraphics.drawPolygon(p);

                    }

                    //Draw Units (if any)
                    if (hexArray[i][j].isOccupied())
                    {
                        //color gray to disbaled
                        if ((hexArray[i][j].getUnit().isDisabled()) && ((hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }

                        //Rescale and offset
                        BufferedImage unitImage = hexArray[i][j].getUnit().getImage();

                        int Xoffset, Yoffset, imageSize;

                        Xoffset = (int)((2 * hexagonSize)/5.464);
                        Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                        imageSize = (int)(2 * (1.732 * Xoffset));
                        
                        newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, newMapGraphics.getColor(),null);
                        //newMapGraphics.drawImage(unitImage, x+Xoffset, y+Yoffset, imageSize, imageSize, null);
                    } 
                }

                //Draw basic polygon 
                newMapGraphics.setColor(Color.BLACK);
                newMapGraphics.drawPolygon(p);
             
               //Coordinates
                if (showCoordinates)
                {
                    newMapGraphics.setColor(Color.BLUE);
                    newMapGraphics.drawString("[" + hexArray[i][j].getCol() + "," + hexArray[i][j].getRow() + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
                }
                
                //Move the pencil over
                x = x + (hexagonSize/2) + hexagonSize;

           }
         
           beginDrawingFromY += (int)2 * (.8660 * hexagonSize);
           
           x = beginDrawingFromX;
           y = y + (int)(2 * .8660 * hexagonSize); 
            
            if ((i%2) != 0)
               y = beginDrawingFromY + (int)(.8660 * hexagonSize); 
            else
                y = beginDrawingFromY;
        }
        
        newMapGraphics.dispose();
        mapImage = offScreenDraw;
    }
    
    //ASSOCIATE POLYGON WITH HEX
    private void associatePolygonWithHex(int rw, int cl, Polygon poly)
    {
        hexArray[rw][cl].setPolygon(poly);
    }
    

    public Polygon getPolygon(int pointX, int pointY)
    {
        //bounds checking
        Iterator polys = polyList.iterator();
        Polygon currentPoly;
        
        while (polys.hasNext())
        {
            currentPoly = (Polygon)polys.next();
            
            if (currentPoly.contains(pointX, pointY))
            {
                return currentPoly;
            }
        }
        
        return null;
    }
    
    public Hex getHexFromPoly(Polygon thisOne)
    {
        Iterator hexes = hexList.iterator();
        Hex current;
        
        while (hexes.hasNext())
        {
            current = (Hex)hexes.next();
            
            if (current.getPolygon() == thisOne)
            {
                return (current);
            }
        }
        
        return null;
    }
    
    public Hex getHexFromCoords(int rw, int cl)
    {
        if ((rw <= rows) && (rw >= 0) && (cl <= cols) && (cl >= 0))
        {
            return hexArray[rw][cl];
        }
        
        else
            return null;
    }
    
    
    public BufferedImage getImage()
    {
        return (mapImage);
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
            updateMapImage();
            return (true);
        }
        else
            return (false);
    }
    
    
    
    public void select(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.add(thisOne);
            thisOne.select();
            updateMapImage();
        }

    }
    
    public void deselect(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.remove(thisOne);
            thisOne.deselect();           
            updateMapImage();
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
    public LinkedList<Hex> getAdjacentHexes(int row, int col)
    {
        LinkedList<Hex> adjHexes = new LinkedList();
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
        
        return (adjHexes);
    }
    
    
    //GET HEXES WITHIN RANGE
    //Retruns a list of hexes in a "shell" radius within range.
    public LinkedList<Hex> getHexesWithinRange(Hex fromHere, int distance)
    {
        LinkedList<Hex> tempAdjacents = new LinkedList();
        LinkedList<Hex> returnList = new LinkedList();
        //prevents the same hex being called multiple THOUSANDS of times.
        LinkedList<Hex> doneThese = new LinkedList(); 
        
        tempAdjacents.clear();
        returnList.clear();
        
        if ((fromHere != null) && (distance > 0))
        {
            //Add the first "shell" of 6 immediate neighbors
            tempAdjacents.addAll(getAdjacentHexes(fromHere));
            returnList.addAll(tempAdjacents);
            doneThese.add(fromHere);
            
            Iterator iter = tempAdjacents.iterator();
            Hex thisHex;
            
            for (int i = 0; i < distance-1; i++)
            {
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

                iter = tempAdjacents.iterator();
            }
        }
        
        return (returnList);
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
    public void computeOverlappingHexes(Player currentPlayer)
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
                adjacentHexes.addAll(getHexesWithinRange(thisHex, thisHex.occupyingUnit.unitWeapon.range));
           }
       }
       
       while (iter.hasNext())
       {
           thisHex =(Hex)iter.next();
           //...and get the "intersection" between the first unit's zone and the rest of the frinedly units' zones
           if ((currentPlayer.units.contains(thisHex.occupyingUnit)) && (thisHex.occupyingUnit.unitWeapon != null))
           {
                adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisHex.occupyingUnit.unitWeapon.range));
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

                if (thisHex.occupyingUnit.unitType.equals("OGRE"))
                {
                    Ogre thisOgre = (Ogre)thisHex.occupyingUnit;
                    
                    if ((weaponIter.hasNext()) && (adjacentHexes.size() <= 1))
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        adjacentHexes.addAll(getHexesWithinRange(thisHex, thisWeapon.range));
                    }
                    
                    while (weaponIter.hasNext())
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        
                        if (thisOgre.getWeapons().contains(thisWeapon))
                        {
                            adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisWeapon.range));
                        }
                    }
                }
            }
       }
        
            updateMapImage();
    }
    
}

