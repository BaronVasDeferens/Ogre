/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;

import java.util.Hashtable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
/**
 *
 * @author skot
 */
public class UnitImageLoader 
{
    Hashtable imageTable;
    
    
    UnitImageLoader()
    {
        imageTable = new Hashtable();
        imageTable.clear();
        
        //Begin loading all unit images into the hashtable, keyed to their names
        
        String name;
        
        name = "GEV.png";
        imageTable.put(loadImage(name),name);
        
        name = "GEV_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "command_post.png";
        imageTable.put(loadImage(name),name);
        
        name = "command_post_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "heavy_tank.png";
        imageTable.put(loadImage(name),name);
        
        name = "heavy_tank_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "howitzer.png";
        imageTable.put(loadImage(name),name);
        
        name = "howitzer_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_1.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_1_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_2.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_2_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_3.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_2_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_3.png";
        imageTable.put(loadImage(name),name);
        
        name = "infantry_3_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "missile_tank.png";
        imageTable.put(loadImage(name),name);
        
        name = "missile_tank_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "mobile_command_post.png";
        imageTable.put(loadImage(name),name);
        
        name = "mobile_command_post_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "ogre_mk3.png";
        imageTable.put(loadImage(name),name);
        
        name = "ogre_mk3_b.png";
        imageTable.put(loadImage(name),name);
        
        name = "ogre_mk5.png";
        imageTable.put(loadImage(name),name);
        
        name = "ogre_mk5_b.png";
        imageTable.put(loadImage(name),name);
            
    }
    
    private BufferedImage loadImage(String imageName)
    {
        if (imageName == null)
            return (null);
        
        else if ((!imageName.matches("")))
        {
            InputStream fin = null;
            BufferedImage img = null;
            
            fin = getClass().getResourceAsStream("images/" + imageName);
            
            try 
            {
                img = ImageIO.read(fin);
                fin.close();
            }
            catch (IOException e) 
            {
                System.out.println(imageName + ": no file found");
            }
            
            return (img);   
        }
        
        else
            return (null);
    }
    
    public BufferedImage getImage(String imageName)
    {
       return null;
    }
    
}
