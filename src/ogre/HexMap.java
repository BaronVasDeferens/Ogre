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
        

        
    }
    
    //CREATE MAP
    //Creates an image of a map by drawing a hex field on it 
    public void setupMap(int width, int height, int originX, int originY, int hexSize)
    {
        hexagonSize = hexSize;     
        beginDrawingFromX = originX;
        beginDrawingFromY = originY;
        
        mapImage = new BufferedImage(width, height, BufferedImage.OPAQUE);        
        Graphics newMapGraphics = mapImage.getGraphics();
        
        //Clear background
        newMapGraphics.setColor(java.awt.Color.WHITE);
        newMapGraphics.fillRect(0,0,width,height);

        
        int x = originX;
        int y = originY;

        //Draw hex field
        for (int i = 1; i < rows+1; i++)
        {
           for (int j = 1; j < cols+1; j++)
           {
               java.awt.Polygon p = new java.awt.Polygon();
               p.reset();
               
               p.addPoint(x +(hexSize/2), y);
               p.addPoint(x+(hexSize/2) + hexSize, y);
               p.addPoint(x + 2*hexSize, (int)(.8660* hexSize + y));
               p.addPoint(x+(hexSize/2) + hexSize, (int)(.8660 * 2 * hexSize + y));
               p.addPoint(x+(hexSize/2),(int)(.8660*2*hexSize + y));
               p.addPoint(x,y+(int)(.8660 * hexSize));
               
               newMapGraphics.setColor(Color.BLACK);
               newMapGraphics.drawPolygon(p);
               
               //associate a hex with this polygon
               associatePolygonWithHex(i-1,j-1,p);
               polyList.add(p);
               
               y = y + (int)(2 * .8660 * hexSize);
           }
        
            x = x + (hexSize/2) + hexSize;
            
            if ((i%2) > 0)
               y = originY + (int)(.8660 * hexSize); 
            else
                y = originY;
            
        }    
                       
    }
    
    //GET UPDATED MAP IMAGE
    public void updateMapImage()
    {
        mapImage = new BufferedImage(mapImage.getWidth(), mapImage.getHeight(), BufferedImage.OPAQUE);
        
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
               
               if (hexArray[i-1][j-1].isSelected())
               {
                   newMapGraphics.setColor(Color.RED);
                   newMapGraphics.fillPolygon(p);                
               }
               
               else
               {
                    newMapGraphics.setColor(Color.BLACK);
                    newMapGraphics.drawPolygon(p);
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
    
    public BufferedImage getImage()
    {
        return (mapImage);
    }
}
