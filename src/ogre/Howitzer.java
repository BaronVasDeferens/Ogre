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
public class Howitzer extends ogre.Unit
{
    public Howitzer(int id)
    {
        super(id);
        
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
