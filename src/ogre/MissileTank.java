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
public class MissileTank extends ogre.Unit
{
    public MissileTank(int id)
    {
        super(id);
        
        unitName = "Missile Tank";
        unitType = "MSLTANK";
        
        movement = 2;
        defense = 2;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(3, 4, false, "missile", 0);
        image = loadImage("missile_tank.png");
    }
}
