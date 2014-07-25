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

/**
 *
 * @author Skot
 */
public class HexMap 
{
    int rows, cols;
    
    Hex hexArray[][];
    
    LinkedList<Hex> hexList;
    LinkedList<Polygon> polyList;
    LinkedList<Hex> selectedHexes;
    LinkedList<Hex> adjacentHexes;
    
    BufferedImage mapImage;
    
    int beginDrawingFromX, beginDrawingFromY, hexagonSize;
    int minimumMapWidth, minimumMapHeight;
    
    boolean showCoordinates = false;
    
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
    
    //SET HEXAGON SIZE
    public void setHexSize(int newSize)
    {
        hexagonSize = newSize;
        setupMap();
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
        
        System.out.println("Created new Image: " + sizeX + " x " + sizeY);
        
        return (newImage);
        
    }
    
    //CREATE MAP
    //Creates an image of a map by drawing a hex field on it
    //OriginX/Y are the coordinates from which drawing shall begin (thr origin)
    public void setupMap()
    {
        if (polyList != null)
            polyList.clear();
        
 
        mapImage = createNewImage();
        Graphics newMapGraphics = mapImage.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,mapImage.getWidth(),mapImage.getHeight());

        
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
                    //if (hexArray[i-1][j-1].isOccupied())
                    {
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
                               
    }
    
    //GET UPDATED MAP IMAGE
    //Updates the map image ONLY. Does not reconfigure polygon or hex lists
    public void updateMapImage()
    {                  

        //mapImage = new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.OPAQUE);
        mapImage = createNewImage();
        
        Graphics newMapGraphics = mapImage.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,mapImage.getWidth(), mapImage.getHeight());

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
                        //Rescale and offset
                        BufferedImage unitImage = hexArray[i][j].getUnit().getImage();
;
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
                    newMapGraphics.drawString("[" + hexArray[i][j].getRow() + "," + hexArray[i][j].getCol() + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
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
    public void addUnit(Hex target, Unit toAdd)
    {
        if ((target.isCrater() == false) && (target.isOccupied() == false))
            target.setOccupingUnit(toAdd);
    }
    
    
    
    public void select(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.add(thisOne);
            thisOne.select();
            
            adjacentHexes.addAll(getHexesWithinRange(thisOne,thisOne.getUnit().movement));
            updateMapImage();
        }

    }
    
    public void deselect(Hex thisOne)
    {
        if (thisOne.isOccupied())
        {
            selectedHexes.remove(thisOne);
            thisOne.deselect();
            //adjacentHexes.removeAll(getAdjacentHexes(thisOne));
            adjacentHexes.clear();

            Iterator iter = selectedHexes.iterator();
            Hex current;

            while (iter.hasNext())
            {
                current = (Hex)iter.next();
                adjacentHexes.addAll(getHexesWithinRange(current,current.getUnit().movement));
            }
            
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
    
    public LinkedList<Hex> getHexesWithinRange(Hex fromHere, int distance)
    {
        LinkedList<Hex> tempAdjacents = new LinkedList();
        LinkedList<Hex> returnList = new LinkedList();
        tempAdjacents.clear();
        returnList.clear();
        
        if ((fromHere != null) && (distance > 0))
        {
            tempAdjacents.addAll(getAdjacentHexes(fromHere));
            
            Iterator iter = tempAdjacents.iterator();
            Hex thisHex;
            
            for (int i = 1; i < distance; i++)
            {
                while (iter.hasNext())
                {
                    thisHex = (Hex)iter.next();
                    returnList.addAll(getAdjacentHexes(thisHex));
                }
                
                tempAdjacents.addAll(returnList);
                iter = tempAdjacents.iterator();
            }
        }
        
        return (returnList);
    }
}
