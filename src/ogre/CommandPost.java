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
public class CommandPost extends ogre.Unit
{
    public CommandPost(int id)
    {
        super(id);
        
        unitName = "Command Post";
        unitType = "CP";
        
        movement = 0;
        defense = 1;
        
        image = loadImage("command_post.png");
        
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
                report = report.concat(" DERP");
                break;
        }
        
        return (report);
    }
}
