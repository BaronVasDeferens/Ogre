/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import static ogre.MyGamesFrame.gameMaster;

/**
 *
 * @author Skot
 */
public class GameFrame extends javax.swing.JFrame {

    
    static OgreGame ogreGame;
    /**
     * Creates new form GameFrame
     */
    public GameFrame() {
        initComponents();
        ogreGame = new OgreGame(ogrePanel1);
        ogreGame.attachComponents(this, WeaponSystemsList, selectedUnitLabel, unitStatLabel, phaseLabel, 
                upperCurrentTargetLabel, currentTargetLabel, attackButton, reportArea, ratioLabel, undoButton, advancePhaseButton);
        
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        upperCurrentTargetLabel = new java.awt.Label();
        undoButton = new javax.swing.JButton();
        advancePhaseButton = new javax.swing.JButton();
        ogrePanel1 = new ogre.OgrePanel();
        WeaponSystemsList = new java.awt.List();
        selectedUnitLabel = new java.awt.Label();
        unitStatLabel = new java.awt.Label();
        phaseLabel = new java.awt.Label();
        attackButton = new javax.swing.JButton();
        currentTargetLabel = new java.awt.Label();
        outputArea = new javax.swing.JScrollPane();
        reportArea = new javax.swing.JTextArea();
        ratioLabel = new java.awt.Label();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        LogInMenuItem = new javax.swing.JMenuItem();
        registerMenuItem = new javax.swing.JMenuItem();
        ViewMyGamesMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        optionHexNumbers = new javax.swing.JMenuItem();
        createNewGameMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                GameFrame.this.windowClosing(evt);
            }
        });

        upperCurrentTargetLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        upperCurrentTargetLabel.setText("Current target:");

        undoButton.setText("UNDO");
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        advancePhaseButton.setText("NEXT PHASE");
        advancePhaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancePhaseButtonActionPerformed(evt);
            }
        });

        ogrePanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout ogrePanel1Layout = new javax.swing.GroupLayout(ogrePanel1);
        ogrePanel1.setLayout(ogrePanel1Layout);
        ogrePanel1Layout.setHorizontalGroup(
            ogrePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 796, Short.MAX_VALUE)
        );
        ogrePanel1Layout.setVerticalGroup(
            ogrePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 596, Short.MAX_VALUE)
        );

        WeaponSystemsList.setEnabled(false);
        WeaponSystemsList.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        WeaponSystemsList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                WeaponSystemsListItemStateChanged(evt);
            }
        });

        selectedUnitLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        selectedUnitLabel.setText("No Unit Selected");

        unitStatLabel.setText("null");

        phaseLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        phaseLabel.setText("no game loaded");

        attackButton.setText("ATTACK");
        attackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attackButtonClicked(evt);
            }
        });

        currentTargetLabel.setMaximumSize(new java.awt.Dimension(38, 20));
        currentTargetLabel.setText("currentTarget");

        reportArea.setEditable(false);
        reportArea.setColumns(20);
        reportArea.setLineWrap(true);
        reportArea.setRows(5);
        reportArea.setRequestFocusEnabled(false);
        outputArea.setViewportView(reportArea);

        ratioLabel.setAlignment(java.awt.Label.RIGHT);
        ratioLabel.setText("ratioLabel");

        jMenu1.setText("File");

        LogInMenuItem.setText("Log In");
        LogInMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogInMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(LogInMenuItem);

        registerMenuItem.setText("Register New User");
        registerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(registerMenuItem);

        ViewMyGamesMenuItem.setText("View My Games");
        ViewMyGamesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewMyGamesMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(ViewMyGamesMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Options");

        optionHexNumbers.setText("Show hex coordinates");
        optionHexNumbers.setSelected(true);
        optionHexNumbers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionHexNumbersActionPerformed(evt);
            }
        });
        jMenu2.add(optionHexNumbers);

        createNewGameMenuItem.setText("Create New Game");
        createNewGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewGameMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(createNewGameMenuItem);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(aboutMenuItem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(ratioLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(attackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(WeaponSystemsList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(phaseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(outputArea)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(undoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(advancePhaseButton))
                    .addComponent(currentTargetLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upperCurrentTargetLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(selectedUnitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(unitStatLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ogrePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(phaseLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedUnitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(unitStatLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(WeaponSystemsList, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upperCurrentTargetLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentTargetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ratioLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(attackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputArea, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(advancePhaseButton)
                            .addComponent(undoButton)))
                    .addComponent(ogrePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        // TODO add your handling code here:
        ogreGame.undo();
    }//GEN-LAST:event_undoButtonActionPerformed

    private void advancePhaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancePhaseButtonActionPerformed
        // TODO add your handling code here:
        ogreGame.advanceGamePhase();
    }//GEN-LAST:event_advancePhaseButtonActionPerformed

    private void LogInMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogInMenuItemActionPerformed
        // TODO add your handling code here:
        ogreGame.login();
    }//GEN-LAST:event_LogInMenuItemActionPerformed

    private void WeaponSystemsListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_WeaponSystemsListItemStateChanged

        //Determine which "mode" we're in (list must be populated and in COMBAT PHASE)
        //If multi-select is on, we're USING weapons
        //If multi-select is off, we're TARGETTING weapons
        
        //We are going to assume that the only way to access an Ogre is by having it be the last
        //unit in hexMap.selectedHexes
        if (ogreGame.hexMap.selectedHexes.isEmpty() == true)
        {
            WeaponSystemsList.setEnabled(false);
        }
        
        //POPULATED, COMBAT TIME
        else if ((WeaponSystemsList.getItemCount() > 0) && ((ogreGame.gamePhase == 12) || (ogreGame.gamePhase == 22)))
        {
            WeaponSystemsList.setEnabled(true);

            //CurrentPLayer's OGRE: Using the weapons to FIRE
            if (WeaponSystemsList.isMultipleMode() == true)
            {
                //There is an activly selected OGRE...
                if (ogreGame.currentOgre != null)
                { 
                    
                    int indexes[] = WeaponSystemsList.getSelectedIndexes();
                    
                    ogreGame.selectedOgreWeapons.clear();
                    
                    for (int i = 0; i < indexes.length; i++)
                    {
                        //Add it if it isn't there; else remove it
                        if (!ogreGame.selectedOgreWeapons.contains(ogreGame.currentOgre.getWeaponByID(indexes[i])))
                        {
                            ogreGame.selectedOgreWeapons.add(ogreGame.currentOgre.getWeaponByID(indexes[i]));
                        }
                       
                    }
                    
                    ogreGame.hexMap.computeOverlappingHexes(ogreGame.currentPlayer, ogreGame);
                    
                    //Changing weapons means that a prior target is no longer viable
                    //TODO: make this work right
                    if (ogreGame.currentTarget != null)
                    {    
                        Hex tempHex = ogreGame.ogrePanel.hexMapRenderer.getHexFromCoords(ogreGame.currentTarget.yLocation, ogreGame.currentTarget.xLocation);
                        if (ogreGame.hexMap.adjacentHexes.contains(tempHex) == false)
                        {
                            ogreGame.hexMap.deselect(ogreGame.ogrePanel.hexMapRenderer.getHexFromCoords(ogreGame.currentTarget.yLocation,ogreGame.currentTarget.xLocation));
                            ogreGame.updateCurrentTarget(null);
                        }
                    }
                    
                    ogreGame.ogrePanel.hexMapRenderer.updateMapImage();
                }
            }
            //currentPlayer is TARGETTING an enemy Ogre's weapon
            else
            {
                if (ogreGame.currentOgre != null)
                {
                    int index = WeaponSystemsList.getSelectedIndex();
                    ogreGame.targettedOgreWeapon = ogreGame.currentOgre.getWeaponByID(index);
                    ogreGame.updateCurrentTarget(ogreGame.currentOgre);
                }

            }
        }
        
        ogreGame.updateRatioLabel();
    }//GEN-LAST:event_WeaponSystemsListItemStateChanged

    private void attackButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attackButtonClicked
        //Oh noes! The attack button has been clicked! Something is going to happen now! BUT WHAT??!
        //Submit the selected units, etc, for inspection to the OgreGame attack function
        ogreGame.attack();
    }//GEN-LAST:event_attackButtonClicked

    private void optionHexNumbersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionHexNumbersActionPerformed
        // TODO add your handling code here:
        if (ogreGame.hexMap != null)
        {
            ogreGame.ogrePanel.hexMapRenderer.showCoordinates = !(ogreGame.ogrePanel.hexMapRenderer.showCoordinates);
            ogreGame.ogrePanel.hexMapRenderer.updateMapImage();
        }

    }//GEN-LAST:event_optionHexNumbersActionPerformed

    private void registerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerMenuItemActionPerformed
        // TODO add your handling code here:
        ogreGame.register();
    }//GEN-LAST:event_registerMenuItemActionPerformed

    private void createNewGameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewGameMenuItemActionPerformed
        // TODO add your handling code here:
        ogreGame.createNewGame();
    }//GEN-LAST:event_createNewGameMenuItemActionPerformed

    private void windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosing
        // If the user attempt to close the window while a game is in progress,
        //issue a warning and upload the gameState
        if (ogreGame.currentGameState != null)
        {
            Object[] options = { "QUIT", "CANCEL" };
            
            JOptionPane pane = new JOptionPane();
            
//            pane.showOptionDialog(this, "Quitting now will end your current turn!", "WARNING",
//            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
//            null, options, options[1]);
//            
//            Object selectedValue = pane.getValue();
            
            Object selectedValue = pane.showOptionDialog(this, "Quitting now will end your current turn!", "WARNING",
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[1]);
            
            //while (selectedValue == "uninitializedValue") {}
            
            if (selectedValue.equals(0))
            {
                //Set to phase 13 and advance the game state
                //which will perform the necessary state changes
                ogreGame.gamePhase = 13;
                ogreGame.advanceGamePhase();
                
                //Wait until the sequence has completed before dismissing the window

                    if (ogreGame.currentGameState == null)
                    {
                        this.setVisible(false);
//                        try
//                        {
//                            Thread.sleep(10);
//                        }
//                        catch (InterruptedException e)
//                        {
//                            
//                        }
                    }    
                
                
            }
            
            else
            {
                //cancel the window close operation....somehow
                JOptionPane.showMessageDialog(this, "Nothing happened...",
            "AW SHIT", JOptionPane.WARNING_MESSAGE);
                
            }
        }
    }//GEN-LAST:event_windowClosing

    private void ViewMyGamesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewMyGamesMenuItemActionPerformed
        //Check #1: is somebody logged in?
        if (ogreGame.activePlayerCredentials == null)
        {
            JOptionPane.showMessageDialog(this, "You are not logged in!",
            "LOG IN FIRST", JOptionPane.WARNING_MESSAGE);
        }
        
        else
        {
            //ogreGame.login();
            ogreGame.displayMyGames();
        }
    }//GEN-LAST:event_ViewMyGamesMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        //Spawn a pop-up with info
        JOptionPane.showMessageDialog(this, ("OGRE v" + ogreGame.version + "\n info at web.pdx.edu/~scowest/ogre"),
            "OGRE VERSION", JOptionPane.WARNING_MESSAGE);
        
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameFrame().setVisible(true);
    
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem LogInMenuItem;
    private javax.swing.JMenuItem ViewMyGamesMenuItem;
    public java.awt.List WeaponSystemsList;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton advancePhaseButton;
    public static javax.swing.JButton attackButton;
    private javax.swing.JMenuItem createNewGameMenuItem;
    public java.awt.Label currentTargetLabel;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    public static ogre.OgrePanel ogrePanel1;
    private javax.swing.JMenuItem optionHexNumbers;
    public static javax.swing.JScrollPane outputArea;
    private java.awt.Label phaseLabel;
    private java.awt.Label ratioLabel;
    private javax.swing.JMenuItem registerMenuItem;
    private javax.swing.JTextArea reportArea;
    private java.awt.Label selectedUnitLabel;
    private javax.swing.JButton undoButton;
    private java.awt.Label unitStatLabel;
    public java.awt.Label upperCurrentTargetLabel;
    // End of variables declaration//GEN-END:variables
}
