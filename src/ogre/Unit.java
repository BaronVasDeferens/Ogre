/*
UNIT CLASS
The basic entity class.

*/

package ogre;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 *
 * @author Skot
 */
public class Unit implements Serializable 
{
    String unitName;        //Different from type?
    String unitType;        //eg tank, infantry, GEV, etc
    int unitID;             //unique ID          

    Player controllingPlayer;
    
    //Awareness of board position
    int xLocation;
    int yLocation;
    
    //States
    boolean hasMoved;
    boolean disabled;
    int disabledTurns = 0;
    boolean isAlive;
    
    //Basic unit stats
    int defense;
    Weapon unitWeapon;
    
    //Movement
    int movement;          //covers the unit's movement allowance.
    int movementPostShooting;   //for GEVs
    
    BufferedImage image;
    
    //Default constructor
    public Unit(int id)
    {
        unitID = id;
        
        isAlive = true;
        
        hasMoved = false;
        disabled = false;
        
        unitWeapon = null;
        movementPostShooting = 0;
    }
    
    //SET LOCATION (X,Y)
    public void setLocation(int x, int y)
    {
        xLocation = x;
        yLocation = y;
    }
    
    //SET LOCATION (HEX)
    
    //LOAD IMAGE
    protected BufferedImage loadImage(String fileName)
    {
        if (fileName == null)
            return (null);
        
        else if ((!fileName.matches("none")))
        {
            InputStream fin = null;
            BufferedImage img = null;
            
            fin = getClass().getResourceAsStream("images/" + fileName);
            
            try 
            {
                img = ImageIO.read(fin);
                fin.close();
            }
            catch (IOException e) 
            {
                System.out.println(fileName + ": no file found");
            }
            
            return (img);   
        }
        
        else
            return (null);
    }
    
    //TAKE DAMAGE
    //Self-manages based on the result of the damage taken
    public void takeDamage(String result)
    {
        switch (result)
        {
            case "NE":
                //no result
                break;
            case "D":
                //disabled
                if (disabled == true)
                    isAlive = false;
                else
                {
                    disabled = true;
                    disabledTurns = 2;
                }    
                break;
            case "X":
                //destroyed
                isAlive = false;
                break;
            default:
                break;
        }
    }
   
    //DISCHARGE WEAPON
    //Assuming it is not disabled, already discharged, destroyed, or whatever, 
    //this function sets the "discharged" flag to true and returns its STRENGTH value.
    public int dischargeWeapon()
    {
        if (unitWeapon != null)
        {
            if ((unitWeapon.disabled == false) && (unitWeapon.dischargedThisRound == false))
            {
                return(unitWeapon.discharge());
            }
        }
        
        return (0);
    }
    
    //ENABLE
    //Enables a unit
    public void enable()
    {
        disabled = false;
    }
    
    //DISABLE
    //Who an ever know the mysteries of this weird function?!?!
    public void disable()
    {
        disabled = true;
    }
    
    //DISABLE
    //Set the number of turns before this unit come back online
    public void disable(int turns)
    {
        disabledTurns = turns;
    }
    
    //SUBTRACT TURN FROM DISBALE
    public void processEndOfTurn()
    {
        hasMoved = false;
        
        disabledTurns--;
        
        if (disabledTurns <= 0)
        {
            disabled = false;
            disabledTurns = 0;
        }
        
    }
    
    public boolean isDisabled()
    {
        if ((disabledTurns > 0) || (disabled == true))
            return true;
        else
            return (false);
    }   
    
    
    public BufferedImage getImage()
    {
        return image;
    }
    
}

