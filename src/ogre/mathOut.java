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
public class mathOut {
    
    public static void main(String ... args)
    {
        float ratio;
        
        for (int i = 1; i < 10; i++)
        {
            ratio = (float)5 / i;
            System.out.print("5/" + i + " : " + ratio);
            System.out.print("\t (int)" + (int)ratio);
            System.out.println();
        }
    }
}
