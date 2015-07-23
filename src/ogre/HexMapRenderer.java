/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.*;
import java.util.*;
import java.awt.*;


/**
 *
 * @author skot
 */
public class HexMapRenderer 
{
    HexMap hexMap;
    OgreGame gm;
    GameState gameState;
    
    BufferedImage mapImage;
    BufferedImage offScreenDraw;
    
    UnitImageLoader unitImages; 
    
    int beginDrawingFromX, beginDrawingFromY, hexagonSize;
    int minimumMapWidth, minimumMapHeight;
    
    public boolean showCoordinates;
    
    
    
    HexMapRenderer(OgreGame gm, HexMap hxMap)
    {
        hexMap = hxMap;
        this.gm = gm;
        
        unitImages = new UnitImageLoader();
        mapImage = null;
        offScreenDraw = null;
    }

    
    
    //SET MINIMUM MAP SIZE
    public void setMinimumMapSize(int minWidth, int minHeight)
    {
        minimumMapWidth = minWidth;
        minimumMapHeight = minHeight;
    }
    
    //SET HEXAGON SIZE
    public void setHexSize(int newSize)
    {
        hexagonSize = newSize;
    }
    
    public int getHexSize()
    {
        return (hexagonSize);
    }

    //CREATE NEW IMAGE
    //Returns a blank BufferedImage sized according to the size of the hexes to be drawn upon it.
    //If smaller than the viewing window (minX, minY), a BufferedImage of minWidth x minLength is returned instead.
    //Drawing offset coordinates are also adjusted to 2*hexagonSize from 0,0
    private BufferedImage createNewImage()
    {
        int rows = hexMap.rows;
        int cols = hexMap.cols;
        
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
    
    
    //SETUP MAP
    //Creates an image of a map by drawing a hex field on it
    //OriginX/Y are the coordinates from which drawing shall begin (the origin)
    public void setupMap()
    {
        
        int rows = hexMap.rows;
        int cols = hexMap.cols;
        
        if (hexMap.polyList != null)
            hexMap.polyList.clear();
 
        LinkedList<Hex> ridgeList = new LinkedList();
        ridgeList.clear();
 
        offScreenDraw = createNewImage();
        //Graphics newMapGraphics = offScreenDraw.getGraphics();
        
        Graphics g = offScreenDraw.getGraphics();
        Graphics2D newMapGraphics = (Graphics2D)g;
        newMapGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
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
               if (hexMap.hexArray[i][j].isCrater())
               {
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.fillPolygon(p);
               }
               
               else
               {
                   newMapGraphics.setColor(Color.WHITE);
                   
                   //Adjacent colorization 
                   if (hexMap.adjacentHexes.contains(hexMap.hexArray[i][j]))
                     {
                         newMapGraphics.setColor(Color.PINK);
                         newMapGraphics.fillPolygon(p);
                         newMapGraphics.setColor(Color.BLACK);
                         newMapGraphics.drawPolygon(p);
                         newMapGraphics.setColor(Color.PINK);
                     }

                    //Paint RED on selected hexes
                    if (hexMap.hexArray[i][j].isSelected())
                    {
                        newMapGraphics.setColor(Color.RED);
                        newMapGraphics.fillPolygon(p);
                    }

                    //Draw Units (if any)
                    if (hexMap.hexArray[i][j].isOccupied())
                    {
                        //if occupying unit is disabled, "keep it gray"
                        if ((hexMap.hexArray[i][j].getUnit().isDisabled()) && ((hexMap.hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }


                        //Rescale and offset
                        BufferedImage unitImage = unitImages.getImage(hexMap.hexArray[i][j].getUnit().currentImage);

                        int Xoffset, Yoffset, imageSize;

                        Xoffset = (int)((2 * hexagonSize)/5.464);
                        Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                        imageSize = (int)(2 * (1.732 * Xoffset));
                        
                        //newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, newMapGraphics.getColor(),null);
                        
                        // DRAW THE GREEN SQUARE
                        // During the current player's turn, a small indicator should be used to show whether or not a unit
                        // is eligable to move/fire during that phase.
                        if (gm != null) {
                            
                        
                            gameState = gm.currentGameState;
                            
                            Unit currentUnit = hexMap.hexArray[i][j].occupyingUnit;
                            
                            if ((gameState.currentPlayer.units.contains(currentUnit)) && (currentUnit.disabled == false)) {
                             
                                switch (gm.phaseType) {
                                    // Units which have not yet moved and are not zero-movement units are highlighted witha green square
                                    case MOVE:
                                        if ((currentUnit.hasMoved == false) && (currentUnit.movement > 0)) {
                                            newMapGraphics.setColor(Color.GREEN);
                                            newMapGraphics.fillRect(x+Xoffset+(int)(imageSize*.75), y+Yoffset, imageSize/4, imageSize/4);
                                        }
                                        break;
                                    // Non-Ogre units which have not yet discharged their weapons are highlighted with a red square
                                    case SHOOT:
                                        if ((currentUnit instanceof OgreUnit == false) && (currentUnit.unitWeapon.dischargedThisRound == false)) {
                                            newMapGraphics.setColor(Color.RED);
                                            newMapGraphics.fillRect(x + Xoffset + (int)(imageSize*.25), y+Yoffset + (int)(imageSize*.75), imageSize/4, imageSize/4);
                                        }
                                        break;
                                    // GEVs are again highlighted with a green square
                                    case SECONDMOVE:
                                        if ((currentUnit instanceof GEV) && (currentUnit.hasMoved == false)) {
                                            newMapGraphics.setColor(Color.GREEN);
                                            newMapGraphics.fillRect(x+Xoffset+(int)(imageSize*.75), y+Yoffset, imageSize/4, imageSize/4);
                                        }
                                    break;
                                }
                            }
                        }
                        
                        newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, null,null);
                    }
               
               }
               
               //Draw basic polygon
                newMapGraphics.setColor(Color.BLACK);
                newMapGraphics.drawPolygon(p);
                
                
                //Collect any ridges (drawn LAST)
                if (hexMap.hexArray[i][j].ridges != null)
                {
                    ridgeList.add(hexMap.hexArray[i][j]);
                } 
                
               
               //Draw coordinates
                if (showCoordinates)
                {

                    newMapGraphics.setColor(Color.GRAY);
                    //newMapGraphics.setColor(Color.BLUE);
                    newMapGraphics.drawString("[" + (j) + "," + (i) + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
                }

               //associate a hex with this polygon
               associatePolygonWithHex(i,j,p);
               hexMap.polyList.add(p);
               
               
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
        
        int rows = hexMap.rows;
        int cols = hexMap.cols;
        
        LinkedList<Hex> ridgeList = new LinkedList();
        ridgeList.clear();
       
        offScreenDraw = createNewImage();
        
        //Graphics newMapGraphics = offScreenDraw.getGraphics();
        Graphics g = offScreenDraw.getGraphics();
        Graphics2D newMapGraphics = (Graphics2D)g;
        newMapGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
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
               if (hexMap.hexArray[i][j].isCrater())
               {
                   newMapGraphics.setColor(Color.BLACK);
                   newMapGraphics.fillPolygon(p);
               }
               
               else
               {                
                    newMapGraphics.setColor(Color.WHITE);

                    //Adjacent hex colorization
                    if (hexMap.adjacentHexes.contains(hexMap.hexArray[i][j]))
                    {
                         newMapGraphics.setColor(Color.PINK);
                         newMapGraphics.fillPolygon(p);
                         newMapGraphics.setColor(Color.BLACK);
                         newMapGraphics.drawPolygon(p);
                         newMapGraphics.setColor(Color.PINK);
                     }

                    //Paint RED on selected hexes
                    if (hexMap.hexArray[i][j].isSelected())
                    {
                        newMapGraphics.setColor(Color.RED);
                        newMapGraphics.fillPolygon(p);
                    }
                    
                    // paint magenta on highlighted hexes
                    if (hexMap.highlightedHexes.contains(hexMap.hexArray[i][j])) {
                        newMapGraphics.setColor(Color.MAGENTA);
                        newMapGraphics.fillPolygon(p);
                    }

                    //Draw Units (if any)
                    if (hexMap.hexArray[i][j].isOccupied())
                    {
                        //Color the hex gray if the occupying unit is disabled
                        if ((hexMap.hexArray[i][j].getUnit().isDisabled()) && ((hexMap.hexArray[i][j].isSelected() == false)))
                        {
                            newMapGraphics.setColor(Color.GRAY);
                            newMapGraphics.fillPolygon(p); 
                        }

                        //Rescale and offset
                        BufferedImage unitImage = unitImages.getImage(hexMap.hexArray[i][j].getUnit().currentImage);

                        int Xoffset, Yoffset, imageSize;

                        Xoffset = (int)((2 * hexagonSize)/5.464);
                        Yoffset = (int)(.66 * hexagonSize - (2 * hexagonSize)/5.464);
                        imageSize = (int)(2 * (1.732 * Xoffset));
                        
                        //newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, newMapGraphics.getColor(),null);
                        
                        // DRAW THE COLORED STATUS SQUARES
                        // During the current player's turn, a small indicator should be used to show whether or not a unit
                        // is eligable to move during that phase.
                        if (gm != null) {
                            
                            gameState = gm.currentGameState;
                            Unit currentUnit = hexMap.hexArray[i][j].occupyingUnit;
                            
                            if ((gameState.currentPlayer.units.contains(currentUnit)) && (currentUnit.disabled == false)) {
                             
                                switch (gm.phaseType) {
                                    // Units which 
                                    case MOVE:
                                        if ((currentUnit.hasMoved == false) && (currentUnit.movement > 0)) {
                                            newMapGraphics.setColor(Color.GREEN);
                                            newMapGraphics.fillRect(x+Xoffset+(int)(imageSize*.75), y+Yoffset, imageSize/4, imageSize/4);
                                        }
                                        break;
                                    case SHOOT:
                                        if ((currentUnit instanceof OgreUnit == false) && (currentUnit.unitWeapon.dischargedThisRound == false)) {
                                            newMapGraphics.setColor(Color.RED);
                                            newMapGraphics.fillRect(x + Xoffset + (int)(imageSize*.25), y+Yoffset + (int)(imageSize*.75), imageSize/4, imageSize/4);
                                        }
                                        break;
                                    // GEVs are again highlighted with a green square
                                    case SECONDMOVE:
                                        if ((currentUnit instanceof GEV) && (currentUnit.hasMoved == false)) {
                                            newMapGraphics.setColor(Color.GREEN);
                                            newMapGraphics.fillRect(x+Xoffset+(int)(imageSize*.75), y+Yoffset, imageSize/4, imageSize/4);
                                        }
                                        break;
                                }
                            }
                        }
                        
                        //newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, newMapGraphics.getColor(),null);
                        //newMapGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        newMapGraphics.drawImage(unitImage,  x+Xoffset, y+Yoffset, imageSize, imageSize, null ,null);
                    } 
                }

                //Draw basic polygon 
                newMapGraphics.setColor(Color.BLACK);
                newMapGraphics.drawPolygon(p);
             
                
                //Collect any ridge faces (drawn last)
                if (hexMap.hexArray[i][j].ridges != null)
                {
                    ridgeList.add(hexMap.hexArray[i][j]);
                }
                
               //Coordinates
                if (showCoordinates)
                {
                    newMapGraphics.setColor(Color.GRAY);
                    //newMapGraphics.setColor(Color.BLUE);
                    newMapGraphics.drawString("[" + hexMap.hexArray[i][j].getCol() + "," + hexMap.hexArray[i][j].getRow() + "]", (x+(int)(hexagonSize/2)), y +(int)(hexagonSize/2));
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
        hexMap.hexArray[rw][cl].setPolygon(poly);
    }
    

    public Polygon getPolygon(int pointX, int pointY)
    {
        //bounds checking
        Iterator polys = hexMap.polyList.iterator();
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
        Iterator hexes = hexMap.hexList.iterator();
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
    
    //GET HEX FROM COORDS
    public Hex getHexFromCoords(int rw, int cl)
    {
        if ((rw <= hexMap.rows) && (rw >= 0) && (cl <= hexMap.cols) && (cl >= 0))
        {
            return hexMap.hexArray[rw][cl];
        }
        
        else
            return null;
    }
    
    //GET IMAGE
    //Returns the latest rendering of the image
    public BufferedImage getImage()
    {
        return (mapImage);
    }
    
 
}
