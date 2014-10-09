/*
UNIT CLASS
The basic entity class.

*/

package ogre;



import javax.sound.sampled.*;
import java.io.Serializable;

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
    UnitType unitType;       //eg tank, infantry, GEV, etc
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
    
    String image;
    String imageAlternate;
    String currentImage;
    
    //Default constructor
    public Unit()
    {   
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
        
}



class CommandPost extends ogre.Unit
{
    public CommandPost()
    {
        super();
        
        unitName = "Command Post";
        unitType = UnitType.CommandPost;
        
        movement = 0;
        defense = 1;
        
        image = "command_post.png";
        imageAlternate = "command_post_b.png";
        currentImage = image;
        
        unitWeapon = new Weapon(0,0,true,"none",0);
    }
    
    @Override
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
            case "X":
                //destroyed
                report = report.concat(" is DESTROYED!");
                isAlive = false;
                break;
            default:
                break;
        }
        
        return (report);
    }
}


class MobileCommandPost extends ogre.Unit
{
    public MobileCommandPost()
    {
        super();
        
        unitName = "Mobile Command Post";
        unitType = UnitType.MobileCommandPost;
        
        movement = 1;
        defense = 1;
        
        image = "mobile_command_post.png";
        imageAlternate = "mobile_command_post_b.png";
        currentImage = image;
        
        unitWeapon = new Weapon(0,0,true,"none",0);
    }
}

class GEV extends ogre.Unit
{
    public GEV()
    {
        super();
        
        unitName = "G.E.V.";
        unitType = UnitType.GEV;
        
        movement = 4;
        movementPostShooting = 3;
        
        defense = 2;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(2, 2, false, "GEV", 0);
        image = "GEV.png";
        imageAlternate = "GEV_b.png";
        currentImage = image;
    }
}



class HeavyTank extends Unit
{
    public HeavyTank()
    {
        super();
        unitName = "Heavy Tank";
        unitType = UnitType.HeavyTank;
        movement = 3;
        defense = 3;
        
        unitWeapon = new Weapon(4,2, false, "Tank", 0);
        
        image = "heavy_tank.png";
        imageAlternate = "heavy_tank_b.png";
        currentImage = image;
    }
}



class Howitzer extends ogre.Unit
{
    public Howitzer()
    {
        super();
        
        unitName = "Howitzer";
        unitType = UnitType.Howitzer;
        
        movement = 0;
        defense = 1;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(6, 8, false, "Howitzer", 0);
        image = "howitzer.png";
        imageAlternate = "howitzer_b.png";
        currentImage = image;
        
    }
}


class Infantry extends ogre.Unit
{
    
    public Infantry(int def)
    {
        super();
        unitName = "Infantry";
        unitType = UnitType.Infantry;
        
        movement = 2;
        defense = def;
        unitWeapon = new Weapon(def, 1, false, "anti-tank", 0);
        
        image = "infantry_" + def + ".png";
        imageAlternate = "infantry_" + def + "_b.png";
        currentImage = image;
    }
    
    //TAKE DAMAGE
    //Self-manages based on the result of the damage taken
    @Override
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
                //*** Strength reduced by one; if already 1, then death
                    unitWeapon.strength--;
                    defense--;

                    //Modify image name to reflect the new strength
                    switch (defense)
                    {
                        //The switch to UnitIMageLoader necessitated some changes here
                        case 2:
                            if (currentImage == image)
                            {
                                image = "infantry_2.png";
                                imageAlternate = "infantry_2_b.png";
                                currentImage = image;
                            }
                            else
                            {
                                image = "infantry_2.png";
                                imageAlternate = "infantry_2_b.png";
                                currentImage = imageAlternate;
                            }
                            
                            report = report.concat(" TAKES A CASUALTY.");
                            break;
                        case 1:
                            if (currentImage == image)
                            {
                                image = "infantry_1.png";
                                imageAlternate = "infantry_1_b.png";
                                currentImage = image;
                            }
                            else
                            {
                                image = "infantry_1.png";
                                imageAlternate = "infantry_1_b.png";
                                currentImage = imageAlternate;
                            }
                            report = report.concat(" TAKES A CASUALTY.");
                            break;
                        case 0:
                            report = report.concat(" is DESTROYED!");
                            isAlive = false;
                            break;
                        default:
                            break;
                    }

                break;
            case "X":
                //destroyed
                report = report.concat(" is DESTROYED!");
                isAlive = false;
                break;
            default:
                break;
        }
        
        return report;
    }
}


class MissileTank extends ogre.Unit
{
    public MissileTank()
    {
        super();
        
        unitName = "Missile Tank";
        unitType = UnitType.MissileTank;
        
        movement = 2;
        defense = 2;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(3, 4, false, "missile", 0);
        image = "missile_tank.png";
        imageAlternate = "missile_tank_b.png";
        currentImage = image;
    }
}
