/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.awt.Polygon;

/**
 *
 * @author Skot
 */
public class Hex 
{

    private int row, col;
    private Polygon myPoly;
    int [] ridgeFaces;
    public Unit occupyingUnit;
    private boolean isCrater;
    private boolean isSelected;
    
    public Hex(int rws, int cls, boolean crater)
    {
        row = rws;
        col = cls;
        
        ridgeFaces = new int[6];
        //System.arraycopy(ridges, 0, ridgeFaces, 0, 6);
        
        occupyingUnit = null;
        myPoly = null;
        
        isCrater = crater;
        isSelected = false;
    }
    
    public void setPolygon(Polygon poly)
    {
        myPoly = poly;
    }
    
    public Polygon getPolygon()
    {
        return myPoly;
    }
    
    public boolean setOccupingUnit(Unit unit)
    {
        if (occupyingUnit == null)
        {
            occupyingUnit = unit;
            return (true);
        }
        
        else
            return (false);
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
}
