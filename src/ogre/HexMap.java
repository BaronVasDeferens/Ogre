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
    
    OgreGame gameMaster;
    
    Hex hexArray[][];
    
    LinkedList<Hex> hexList;
    LinkedList<Polygon> polyList;
    LinkedList<Hex> selectedHexes;
    LinkedList<Hex> adjacentHexes;
    
    //BufferedImage mapImage;
    //BufferedImage offScreenDraw;
    
    //UnitImageLoader unitImages; 
    
    int beginDrawingFromX, beginDrawingFromY;//, hexagonSize;
    //int minimumMapWidth, minimumMapHeight;
    
    public boolean showCoordinates = false;
    
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

        //hexagonSize = 64;
        //minimumMapWidth = 800;
        //minimumMapHeight = 600;
        
        //setHexSize(hexsize);
        
        //TODO:
        //This doesn't belong here! Move it after testing
        makeCrater(hexArray[2][5]);
        makeCrater(hexArray[1][7]);
        
        addRidge(hexArray[3][4],6,hexArray[2][3],3);
        addRidge(hexArray[3][4],1,hexArray[2][4],4);
        
        //unitImages = new UnitImageLoader(); 
        
    }
    
    //SET GAME MASTER
    //give this object awareness of its owner
    public void setMaster(OgreGame msr)
    {
        gameMaster = msr;
    }
    
    /*
    //SET HEXAGON SIZE
    public void setHexSize(int newSize)
    {
        hexagonSize = newSize;
        //setupMap();
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
    */
    
    
    //MAKE CRATER
    public void makeCrater(Hex target)
    {
        if (target != null)
        {
            target.isCrater = true;
        }
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
    
    //CREATE MAP
    //Returns a blank BufferedImage sized according to the size of the hexes to be drawn upon it.
    //If smaller than the viewing window (minX, minY), a BufferedImage of minWidth x minLength is returned instead.
    //Drawing offset coordinates are also adjusted to 2*hexagonSize from 0,0
    /*
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
      
        return (newImage);
        
    }
    
    //CREATE MAP
    //Creates an image of a map by drawing a hex field on it
    //OriginX/Y are the coordinates from which drawing shall begin (thr origin)
    public void setupMap()
    {
        if (polyList != null)
            polyList.clear();
 
        LinkedList<Hex> ridgeList = new LinkedList();
        ridgeList.clear();
 
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
                        //if occupying unit is disabled, "keep it gray"
                        if ((hexArray[i][j].getUnit().isDisabled()) && ((hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }


                        //Rescale and offset
                        BufferedImage unitImage = unitImages.getImage(hexArray[i][j].getUnit().currentImage);

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
                
                
                //Collect any ridges (drawn LAST)
                if (hexArray[i][j].ridges != null)
                {
                    ridgeList.add(hexArray[i][j]);
                } 
                
               
               //Draw coordinates
                if (showCoordinates)
                {
                    newMapGraphics.setColor(Color.BLUE);
                    newMapGraphics.drawString("[" + (j) + "," + (i) + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
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
        
        //Finally, draw ridges
        if (ridgeList.isEmpty() == false)
        {
            Hex rHex;
            Polygon rPoly;
            Ridge rRidge;
            int a,b,c,d;    
            
            Iterator hexIter = ridgeList.iterator();
            Iterator ridgeIter;
             
            while (hexIter.hasNext())
            {
                rHex = (Hex)hexIter.next();
                
                ridgeIter = rHex.ridges.iterator();
                
                while (ridgeIter.hasNext())
                {
                    rRidge = (Ridge)ridgeIter.next();
                    rPoly = rRidge.hexA.getPolygon();
                    
                    newMapGraphics.setColor(Color.BLACK);

                    //if the face is 1 or 4, then the "thick lines" need
                    //to be adjusted vertically
                    if ((rRidge.faceA == 1) || (rRidge.faceA == 4))
                    {
                        a = rPoly.xpoints[rRidge.faceA -1];
                        b = rPoly.ypoints[rRidge.faceA -1];
                        c = rPoly.xpoints[rRidge.faceA];
                        d = rPoly.ypoints[rRidge.faceA]; 
                        
                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a,b+1,c,d+1);
                        newMapGraphics.drawLine(a,b+2,c,d+2);
                        newMapGraphics.drawLine(a,b-1,c,d-1);
                        newMapGraphics.drawLine(a,b-2,c,d-2);

                    }
                    
                    else if (rRidge.faceA == 6)
                    {
                        a = rPoly.xpoints[0];
                        b = rPoly.ypoints[0];
                        c = rPoly.xpoints[5];
                        d = rPoly.ypoints[5]; 
                        
                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a-1,b,c-1,d);
                        newMapGraphics.drawLine(a-2,b,c-2,d);
                        newMapGraphics.drawLine(a+1,b,c+1,d);
                        newMapGraphics.drawLine(a+2,b,c+2,d);
                    }
                    
                    else
                    {
                        a = rPoly.xpoints[rRidge.faceA -1];
                        b = rPoly.ypoints[rRidge.faceA -1];
                        c = rPoly.xpoints[rRidge.faceA];
                        d = rPoly.ypoints[rRidge.faceA];

                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a-1,b,c-1,d);
                        newMapGraphics.drawLine(a-2,b,c-2,d);
                        newMapGraphics.drawLine(a+1,b,c+1,d);
                        newMapGraphics.drawLine(a+2,b,c+2,d);
                    }
                }
            }
            
            ridgeList.clear();
        }
           
        newMapGraphics.dispose();
        mapImage = offScreenDraw;
    }
    
    //GET UPDATED MAP IMAGE
    //Updates the map image ONLY. Does not reconfigure polygon or hex lists
    public void updateMapImage()
    {                  
        LinkedList<Hex> ridgeList = new LinkedList();
        ridgeList.clear();
       
        offScreenDraw = createNewImage();
        
        Graphics newMapGraphics = offScreenDraw.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,offScreenDraw.getWidth(), offScreenDraw.getHeight());

        int x = beginDrawingFromX;
        int y = beginDrawingFromY;
        
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
                        //color gray to disabled
                        if ((hexArray[i][j].getUnit().isDisabled()) && ((hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }

                        //Rescale and offset
                        BufferedImage unitImage = unitImages.getImage(hexArray[i][j].getUnit().currentImage);

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
             
                
                //Collect any ridge faces (drawn last)
                if (hexArray[i][j].ridges != null)
                {
                    ridgeList.add(hexArray[i][j]);
                }
                
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
        
        //Finally, draw ridges
        if (ridgeList.isEmpty() == false)
        {
            Hex rHex;
            Polygon rPoly;
            Ridge rRidge;
            int a,b,c,d;
            
            Iterator hexIter = ridgeList.iterator();
            Iterator ridgeIter;
             
            while (hexIter.hasNext())
            {
                rHex = (Hex)hexIter.next();
                
                ridgeIter = rHex.ridges.iterator();
                
                while (ridgeIter.hasNext())
                {
                    rRidge = (Ridge)ridgeIter.next();
                    rPoly = rRidge.hexA.getPolygon();
                    
                    newMapGraphics.setColor(Color.BLACK);

                    //if the face is 1 or 4, then the "thick lines" need
                    //to be adjusted vertically
                    if ((rRidge.faceA == 1) || (rRidge.faceA == 4))
                    {
                        a = rPoly.xpoints[rRidge.faceA -1];
                        b = rPoly.ypoints[rRidge.faceA -1];
                        c = rPoly.xpoints[rRidge.faceA];
                        d = rPoly.ypoints[rRidge.faceA]; 
                        
                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a,b+1,c,d+1);
                        newMapGraphics.drawLine(a,b+2,c,d+2);
                        newMapGraphics.drawLine(a,b-1,c,d-1);
                        newMapGraphics.drawLine(a,b-2,c,d-2);

                    }
                    
                    else if (rRidge.faceA == 6)
                    {
                        a = rPoly.xpoints[0];
                        b = rPoly.ypoints[0];
                        c = rPoly.xpoints[5];
                        d = rPoly.ypoints[5]; 
                        
                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a-1,b,c-1,d);
                        newMapGraphics.drawLine(a-2,b,c-2,d);
                        newMapGraphics.drawLine(a+1,b,c+1,d);
                        newMapGraphics.drawLine(a+2,b,c+2,d);
                    }
                    
                    else
                    {
                        a = rPoly.xpoints[rRidge.faceA -1];
                        b = rPoly.ypoints[rRidge.faceA -1];
                        c = rPoly.xpoints[rRidge.faceA];
                        d = rPoly.ypoints[rRidge.faceA];

                        newMapGraphics.drawLine(a,b,c,d);
                        newMapGraphics.drawLine(a-1,b,c-1,d);
                        newMapGraphics.drawLine(a-2,b,c-2,d);
                        newMapGraphics.drawLine(a+1,b,c+1,d);
                        newMapGraphics.drawLine(a+2,b,c+2,d);
                    }
                }
            }
            
            ridgeList.clear();
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
    
    */
    
    
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
            hexArray[col][row].setOccupyingUnit(toAdd);
            target.occupyingUnit.xLocation = target.getCol();
            target.occupyingUnit.yLocation = target.getRow();
            return true;
        }
        
        else
            return false;
        
    }
    
    
    public void select(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.add(thisOne);
            thisOne.select();
            //updateMapImage();
        }

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
        
//        
//        LinkedList<Hex> returnList = new LinkedList();
//        returnList.clear();
//        Iterator iter = adjHexes.iterator();
//        Hex thisHex;
//        
//        while (iter.hasNext())
//        {
//            thisHex = (Hex)iter.next();
//            returnList.add(thisHex);
//        }
        
        return (convertToHexLinkedList(adjHexes));
    }
    
    
    //GET HEXES WITHIN RANGE
    //Retruns a list of hexes in a "shell" radius within range.
    //If the ignoreCrater flag is set to false, then craters are considered obstructions
    //Likewise, if ignoreRidges is set to false, hexes which share a ridge will be obstructions
    //Relies upon the recursive version of the function of the same name (below)
    public LinkedList<Hex> getHexesWithinRange(Hex fromHere, int distance, boolean ignoreCrater, boolean ignoreRidges)
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
            //RIDGES and CRATERS should be processes/weeded out
            else
            {
                
                //NOTE: strangely, casting the Sets to LinkedLists does not affect their "one instance per list" behavior.
                //(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, LinkedList<Hex> returnList)
                returnList.addAll(getHexesWithinRange(fromHere, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges));
                ignoreThese.clear();
                
                //NOTE: the ignoreLIst is cleared at the end of each "ring's" examination;
                //a hex which is not accessible from one hex (shares a ridge) might be accessible from another.
                //If those "ignored" hexes are allowed to persists from one concentric exam to another,
                //they will appaear as totally inaccessible-- not good.
                
                for (int i = 0; i < distance-1; i++)
                {
                     iter = returnList.iterator();
                     
                     while (iter.hasNext())
                     {
                         thisHex = (Hex)iter.next();
                         tempAdjacents.addAll(getHexesWithinRange(thisHex, convertToHexLinkedList(doneThese), convertToHexLinkedList(ignoreThese), ignoreRidges));
                     }
                     
                     returnList.addAll(tempAdjacents);
                     tempAdjacents.clear();
                     ignoreThese.clear();
                }
                
                
            }
        }
        
        return (convertToHexLinkedList(returnList));
    }
    
    //GET HEXES WITHIN RANGE
    //My thinking was this:
    //A "center" hex and its 6 neighbors are examined. If there is a ridge between them (and ridges are not ignored),
    //then that hex is inaccessibe from the center hex and is "skipped" for a round of examinations.
    //That hex may be accessible from another neighbor hex, however, and if it can be found in the list of accessible
    //neighbors then it is removed from the skip list at the end.
    public LinkedList<Hex> getHexesWithinRange(Hex center, LinkedList<Hex> doneThese, LinkedList<Hex> ignoreThese, boolean ignoreRidges)
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
                    
                    if ((ignoreRidges == false) && ((center.sharesRidgeWithThisHex(thisHex)) && (!doneThese.contains(thisHex))))
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
                adjacentHexes.addAll(getHexesWithinRange(thisHex, thisHex.occupyingUnit.unitWeapon.range,true,true));
           }
       }
       
       while (iter.hasNext())
       {
           thisHex =(Hex)iter.next();
           //...and get the "intersection" between the first unit's zone and the rest of the frinedly units' zones
           if ((currentPlayer.units.contains(thisHex.occupyingUnit)) && (thisHex.occupyingUnit.unitWeapon != null))
           {
                adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisHex.occupyingUnit.unitWeapon.range, true, true));
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
                    Ogre thisOgre = (Ogre)thisHex.occupyingUnit;
                    
                    if ((weaponIter.hasNext()) && (adjacentHexes.size() <= 1))
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        adjacentHexes.addAll(getHexesWithinRange(thisHex, thisWeapon.range, true,true));
                    }
                    
                    while (weaponIter.hasNext())
                    {
                        thisWeapon = (Weapon)weaponIter.next();
                        
                        if (thisOgre.getWeapons().contains(thisWeapon))
                        {
                            adjacentHexes.retainAll(getHexesWithinRange(thisHex,thisWeapon.range, true,true));
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
    
}

