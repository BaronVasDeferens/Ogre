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
public class HeavyTank extends ogre.Unit
{
    public HeavyTank(int id)
    {
        super(id);
        unitName = "Heavy Tank";
        unitType = UnitType.HeavyTank;
        movement = 3;
        defense = 3;
        
        unitWeapon = new Weapon(4,2, false, "Tank", 0);
        
        image = loadImage("heavy_tank.png");
    }
}
