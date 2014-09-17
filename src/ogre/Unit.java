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

enum UnitType
{
    Ogre, HeavyTank, MissileTank, CommandPost, MobileCommandPost, Infantry, GEV, Howitzer
}

public class Unit implements Serializable 
{
    
    
    String unitName;        //Different from type?
    UnitType unitType;        //eg tank, infantry, GEV, etc
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
    BufferedImage imageAlternate;
    BufferedImage currentImage;
    
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
    //Self-manages based on the result of the damage taken.
    //Returns a String with flavr text
    public String takeDamage(String result)
    {
        String report = unitName;
        
        switch (result)
        {
            case "NE":
                //no result
                report = report.concat(" is UNHARMED.");
                break;
            case "D":
                //disabled
                if (disabled == true)
                {
                    isAlive = false;
                    report = report.concat(" is DESTROYED!");
                }
                else
                {
                    report = report.concat(" is DISABLED.");
                    disabled = true;
                    disabledTurns = 3;
                }    
                break;
            case "X":
                //destroyed
                report = report.concat(" is DESTROYED!");
                isAlive = false;
                break;
            default:
                report = report.concat(" DERP");
                break;
        }
        
        return (report);
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
    
//    //DISABLE
//    //Who an ever know the mysteries of this weird function?!?!
//    public void disable()
//    {
//        disabled = true;
//    }
    
    //DISABLE
    //Set the number of turns before this unit come back online
    public void disable(int turns)
    {
        disabledTurns = turns;
    }
    
    //PROCESS END OF TURN
    public void processEndOfTurn()
    {
        hasMoved = false;
        
        if (disabledTurns > 0)
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
    
    public void flipImage()
    {
        if (currentImage == image)
            currentImage = imageAlternate;
        else
            currentImage = image;
    }
    
    public BufferedImage getImage()
    {
        return currentImage;
    }
    
}

