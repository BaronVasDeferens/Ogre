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
    
    BufferedImage mapImage;
    
    int beginDrawingFromX, beginDrawingFromY, hexagonSize;
    int minimumMapWidth, minimumMapHeight;
    
    //Constructor
    public HexMap(int rws, int cls)
    {
        rows = rws;
        cols = cls;
        
        hexList = new LinkedList();
        hexList.clear();
        
        polyList = new LinkedList();
        polyList.clear();
        
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
        
        beginDrawingFromX = 2 * hexagonSize;
        beginDrawingFromY = 2 * hexagonSize;
        

        //The formula for the image size is:
        //(hexSize * 1.5 * cols + ( 4 * hexSize)) by (hexSize * 1.5 * rows + ( 4 * hexSize))
        int sizeX = (int)((hexagonSize * 1.5 * rows) + (4 * hexagonSize));
        int sizeY = (int)((hexagonSize * 1.5 * cols) + (4 * hexagonSize));
        
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

        //Draw hex field
        for (int i = 1; i < rows+1; i++)
        {
           for (int j = 1; j < cols+1; j++)
           {
               java.awt.Polygon p = new java.awt.Polygon();
               p.reset();
               
               p.addPoint(x +(hexagonSize/2), y);
               p.addPoint(x+(hexagonSize/2) + hexagonSize, y);
               p.addPoint(x + 2*hexagonSize, (int)(.8660* hexagonSize + y));
               p.addPoint(x+(hexagonSize/2) + hexagonSize, (int)(.8660 * 2 * hexagonSize + y));
               p.addPoint(x+(hexagonSize/2),(int)(.8660*2*hexagonSize + y));
               p.addPoint(x,y+(int)(.8660 * hexagonSize));
               
               
               //Paint RED on selected hexes
               if (hexArray[i-1][j-1].isSelected())
               {
                   newMapGraphics.setColor(Color.RED);
                   newMapGraphics.fillPolygon(p);
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.drawPolygon(p);
                   
               }
               
               else
               {
                    newMapGraphics.setColor(Color.BLACK);
                    newMapGraphics.drawPolygon(p);
               }
               
               //Draw Units (if any)
               if (hexArray[i-1][j-1].isOccupied())
               {
                   //Rescale and offset
                   BufferedImage unitImage = hexArray[i-1][j-1].getUnit().getImage();
                   
                   int Xoffset, Yoffset, imageSize;
                   
                   Xoffset = (int)((2 * hexagonSize)/5.464);
                   Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                   imageSize = (int)(2 * (1.732 * Xoffset));
                   
                   newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, null);
                   
                   
               }
               
//               newMapGraphics.setColor(Color.BLACK);
//               newMapGraphics.drawPolygon(p);
               
               //associate a hex with this polygon
               associatePolygonWithHex(i-1,j-1,p);
               polyList.add(p);
               
               y = y + (int)(2 * .8660 * hexagonSize);
           }
        
            x = x + (hexagonSize/2) + hexagonSize;
            
            if ((i%2) > 0)
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
        int y = beginDrawingFromY;

        //Draw hex field
        for (int i = 1; i < rows+1; i++)
        {
           for (int j = 1; j < cols+1; j++)
           {
               java.awt.Polygon p = new java.awt.Polygon();
               p.reset();
               
               p.addPoint(x +(hexagonSize/2), y);
               p.addPoint(x+(hexagonSize/2) + hexagonSize, y);
               p.addPoint(x + 2*hexagonSize, (int)(.8660* hexagonSize + y));
               p.addPoint(x+(hexagonSize/2) + hexagonSize, (int)(.8660 * 2 * hexagonSize + y));
               p.addPoint(x+(hexagonSize/2),(int)(.8660*2*hexagonSize + y));
               p.addPoint(x,y+(int)(.8660 * hexagonSize));
               
               //Paint RED on selected hexes
               if (hexArray[i-1][j-1].isSelected())
               {
                   newMapGraphics.setColor(Color.RED);
                   newMapGraphics.fillPolygon(p);
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.drawPolygon(p);
                   
               }
               
               else
               {
                    newMapGraphics.setColor(Color.BLACK);
                    newMapGraphics.drawPolygon(p);
               }

               //Draw Units (if any)
               if (hexArray[i-1][j-1].isOccupied())
               {
                   //Rescale and offset
                   BufferedImage unitImage = hexArray[i-1][j-1].getUnit().getImage();
                   
                   int Xoffset, Yoffset, imageSize;
                   
                   Xoffset = (int)((2 * hexagonSize)/5.464);
                   Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                   imageSize = (int)(2 * (1.732 * Xoffset));
                   
                   newMapGraphics.drawImage(unitImage, x+Xoffset, y+Yoffset, imageSize, imageSize, null);
               }
               
               y = y + (int)(2 * .8660 * hexagonSize);
           }
        
            x = x + (hexagonSize/2) + hexagonSize;
            
            if ((i%2) > 0)
               y = beginDrawingFromY + (int)(.8660 * hexagonSize); 
            else
                y = beginDrawingFromY;
        }
        
    }
    
    //ASSOCIATE POLYGON WITH HEX
    //
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
    public void addUnit(Hex target, Unit toAdd)
    {
        target.setOccupingUnit(toAdd);
    }
    
}
