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
public class GEV extends ogre.Unit
{
    public GEV(int id)
    {
        super(id);
        
        unitName = "G.E.V.";
        unitType = "GEV";
        
        movement = 4;
        movementPostShooting = 3;
        
        defense = 2;
        
        //Weapon(int atk, int rng, boolean infOnly, String name, int id)
        unitWeapon = new Weapon(2, 2, false, "GEV", 0);
        image = loadImage("GEV.png");
    }
}
