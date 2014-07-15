/*
UNIT CLASS
The basic entity class.

*/

package ogre;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 *
 * @author Skot
 */
public class Unit 
{
    String unitName;        //Different from type?
    String unitType;        //eg tank, infantry, GEV, etc
    int unitID;             //unique ID          

    //Awareness of board position
    int xLocation;
    int yLocation;
    
    //States
    boolean hasMoved;
    boolean disabled;
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
    
    //SET LOCATION
    public void setLocation(int x, int y)
    {
        xLocation = x;
        yLocation = y;
    }
    
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
    public void takeDamage(char result)
    {
        switch (result)
        {
            case 'n':
                //no result
                break;
            case 'd':
                //disabled
                disabled = true;
                break;
            case 'x':
                //destroyed
                isAlive = false;
                break;
            default:
                break;
        }
    }
    
}

