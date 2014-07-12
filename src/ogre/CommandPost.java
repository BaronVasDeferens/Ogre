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
        defense = 0;
        
        image = loadImage("command_post.png");
    }
}
