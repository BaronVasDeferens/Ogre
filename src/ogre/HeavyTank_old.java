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
public class HeavyTank_old extends Unit
{
    public HeavyTank_old()
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
