/*
April 8th, 2015: The Future
Performing some clean-up in preparation for integration of new features, such as player-vs-AI
 */
package ogre;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author skot
 */
public class Ogre {
    
    
    
    public static void main(String ... args)
    {
     /*   
        SwingUtilities.invokeLater(new Runnable() {
                   public void run() {
                       launch();
                   }
               });
             */
        
        launch();
    }
    
    private static void launch()
    {
        // Create the "brain" of the game...
        OgreGame gameBrain = new OgreGame();
        GameFrame ogreFrame = new GameFrame();
        
        ogreFrame.connectToGameBrain(gameBrain);
        
        ogreFrame.setPreferredSize(new Dimension(1148, 645));
        ogreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        ogreFrame.getContentPane().add(new JLabel("??"), java.awt.BorderLayout.CENTER);
        
        ogreFrame.pack();
        ogreFrame.setVisible(true);
        
        while (true)
        {
            
        }
        
    }
    
}
