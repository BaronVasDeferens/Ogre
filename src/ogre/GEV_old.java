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
public class GEV_old extends ogre.Unit
{
    public GEV_old(int id)
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
