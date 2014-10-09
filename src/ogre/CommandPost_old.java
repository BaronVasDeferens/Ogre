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
public class CommandPost_old extends ogre.Unit
{
    public CommandPost_old(int id)
    {
        super();
        
        unitName = "Command Post";
        unitType = UnitType.CommandPost;
        
        movement = 0;
        defense = 1;
        
        image = "command_post.png";
        imageAlternate = "command_post_b.png";
        currentImage = image;
        
        unitWeapon = new Weapon(0,0,true,"none",0);
    }
    
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
            case "X":
                //destroyed
                report = report.concat(" is DESTROYED!");
                isAlive = false;
                break;
            default:
                break;
        }
        
        return (report);
    }
}
