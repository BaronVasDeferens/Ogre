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
        unitType = UnitType.Infantry;
        
        movement = 2;
        defense = def;
        unitWeapon = new Weapon(def, 1, false, "anti-tank", 0);
        
        image = loadImage("infantry_" + def + ".png");
        imageAlternate = loadImage("infantry_" + def + "_b.png");
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

                    //Load a new image to reflect the new strength
                    switch (defense)
                    {
                        case 2:
                            image = loadImage("infantry_2.png");
                            imageAlternate = loadImage("infantry_2_b.png");
                            report = report.concat(" TAKES A CASUALTY.");
                            break;
                        case 1:
                            image = loadImage("infantry_1.png");
                            imageAlternate = loadImage("infantry_1_b.png");
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
