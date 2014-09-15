/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.awt.Polygon;

/**
 *
 * @author Skot
 */
public class mathOut {
    
    public static void main(String ... args)
    {
//        float result;
//        
//        for (int i = 1; i < 7; i++)
//        {          
//            System.out.println(i + "," + (i+3)%6);
//        }
        
        Polygon p = new Polygon();
        p.reset();
        p.addPoint(0,0);
        p.addPoint(0,15);
        p.addPoint(15,15);
        p.addPoint(15,0);
        
        System.out.println(p.npoints);
        System.out.println(p.xpoints[1] + "," + p.ypoints[1]);

    }
}
