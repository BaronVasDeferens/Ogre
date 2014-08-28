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
        int movement;
        int maxMove = 3;
        int maxTreads = 45;
        int treadsPerRow = 15;
        int remainingTreads = maxTreads;
        int lostTreads = maxTreads - remainingTreads;
        
        for (int i = maxTreads; i >= 0; i--)
        {
            lostTreads = maxTreads - remainingTreads;
            movement = maxMove - (lostTreads/treadsPerRow);
            System.out.println("(" + remainingTreads + ") " + "LOST: " + (lostTreads) + "  MOVE: " + movement);
            
            remainingTreads--;

        }
    }
}
