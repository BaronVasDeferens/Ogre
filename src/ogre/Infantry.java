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
    
    //TAKE DAMAGE
    //Self-manages based on the result of the damage taken
    @Override
    public void takeDamage(char result)
    {
        switch (result)
        {
            case 'n':
                //no result
                break;
            case 'd':
                //*** Strength reduced by one; if already 1, then death
                if (unitWeapon.attack > 1)
                {
                    unitWeapon.attack --;
                    defense--;
                    
                    //Load a new image to reflect the new strength
                    switch (unitWeapon.attack)
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
            case 'x':
                //destroyed
                isAlive = false;
                break;
            default:
                break;
        }
    }
}
