/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

/**
 *
 * @author Skot
 */
public class Infantry extends ogre.Unit
{
    /*
    public Infantry(int id)
    {
        super(id);
        
        unitName = "Infantry";
        unitType = "INFANTRY";
        
        movement = 2;
        defense = 3;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(3, 1, false, "anti-tank", 0);
        image = loadImage("infantry_3.png");
    }
    */
    
    public Infantry(int id, int def)
    {
        super(id);
        unitName = "Infantry";
        unitType = "INFANTRY";
        
        movement = 2;
        defense = def;
        unitWeapon = new Weapon(def, 1, false, "anti-tank", 0);
        
        image = loadImage("infantry_" + def + ".png");
    }
    
    //TAKE DAMAGE
    //Self-manages based on the result of the damage taken
    @Override
    public void takeDamage(String result)
    {
        switch (result)
        {
            case "NE":
                //no result
                break;
            case "D":
                //*** Strength reduced by one; if already 1, then death
                if (unitWeapon.strength > 1)
                {
                    unitWeapon.strength --;
                    defense--;
                    
                    //Load a new image to reflect the new strength
                    switch (unitWeapon.strength)
                    {
                        case 2:
                            image = loadImage("infantry_2.png");
                            break;
                        case 1:
                            image = loadImage("infantry_1.png");
                            break;
                        default:
                            break;
                    }
                }
                else
                    isAlive = false;
                break;
            case "X":
                //destroyed
                isAlive = false;
                break;
            default:
                break;
        }
    }
}
