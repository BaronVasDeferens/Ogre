/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.awt.Polygon;
import java.util.LinkedList;

/**
 *
 * @author Skot
 */
public class Hex 
{

    private int row, col;
    private Polygon myPoly;
    public Unit occupyingUnit;
    boolean isCrater;
    private boolean isSelected;
    
    public LinkedList<Ridge> ridges;
    
    public Hex(int rws, int cls, boolean crater)
    {
        row = rws;
        col = cls;
                
        occupyingUnit = null;
        myPoly = null;
        
        isCrater = crater;
        isSelected = false;
        
        ridges = null;
    }
    
    public void setPolygon(Polygon poly)
    {
        myPoly = poly;
    }
    
    public Polygon getPolygon()
    {
        return myPoly;
    }
    
    public void setOccupyingUnit(Unit unit)
    {
        occupyingUnit = unit;
    }
    
    //IS OCCUPIED (occupe!)
    //Null/populated check, just prettier syntax
    public boolean isOccupied()
    {
        if (occupyingUnit == null)
            return (false);
        else
            return (true);
    }
    
    public Unit getUnit()
    {
        return (occupyingUnit);
    }
    
    public boolean isCrater()
    {
        return (isCrater);
    }
    
    public void select()
    {
        isSelected = true;
    }
    
    public void deselect()
    {
        isSelected = false;
    }
    
    public boolean isSelected()
    {
        return (isSelected);
    }
    
    public int getRow()
    {
        return (row);
    }
    
    public int getCol()
    {
        return (col);
    }
    
    public void addRidge(Hex h1, int face1, Hex h2, int face2)
    {
        if (ridges == null)
        {
            ridges = new LinkedList();
            ridges.clear();
        }
        
        ridges.add(new Ridge (h1,face1,h2,face2));
    }
    
    public LinkedList<Ridge>  getRidges()
    {
        return ridges;
    }
}

class Ridge
{
    public Hex hexA, hexB;
    int faceA, faceB;
    
    Ridge(Hex hexOne, int faceOne, Hex hexTwo, int faceTwo)
    {
        hexA = hexOne;
        faceA = faceOne;
        hexB = hexTwo;
        faceB = faceTwo;
    }
}
