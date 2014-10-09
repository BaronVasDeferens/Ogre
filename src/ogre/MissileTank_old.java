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
public class MissileTank_old extends ogre.Unit
{
    public MissileTank_old(int id)
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
