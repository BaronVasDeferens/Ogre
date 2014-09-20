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
        TestObject [] objectArray = new TestObject[10];
        
        int val = 1;
        
        for (TestObject x: objectArray)
        {
            x = new TestObject(val);
            val++;
        }
        
        for (TestObject x: objectArray)
        {
            System.out.println(x.data);
        }

    }
}

class TestObject
{
    public int data;
    
    TestObject()
    {
        data = 0;
    }
    
    TestObject(int value)
    {
        data = value;
    }
}
