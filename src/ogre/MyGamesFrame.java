/*
MY GAMES FRAME

This Frame is concerned with displaying the user's games, whether they be in progress
or waiting for the other player to take action.

Upon instantiation, MyGamesFrame accepts a LoginObject which contains a LinkedLIst of all the games the user is 
currently engaged in. These games will fall into two distinct categories:
--  games which are "ready" for the player to continue playing (the "closed" flag is true,
    and the currentPlayer field matches this player)
--  games which are not ready (isClosed is false, currentPLayer doesn't match this player).

Games which are "ready" will be displayed in the reday games list and those which
are pending end up in the pending box (cannot be selected for anything and are really
just for display)
 */
package ogre;

import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import static ogre.CreateNewGameFrame.credentials;

/**
 *
 * @author skot
 */
public class MyGamesFrame extends javax.swing.JFrame {

    static LoginObject activePlayerCredentials = null;
    static OgreGame gameMaster;
    static GameState [] readyGameArray;
    
    static HashSet<GameState> readyGameStates;
    static HashSet<GameState> pendingGameStates;
    
    /**
     * Creates new form MyGamesFrame
     */
    public MyGamesFrame(OgreGame master, LoginObject userCreds) 
    {
        initComponents();
        
        this.setTitle("My Games");
        
        gameMaster = master;
        activePlayerCredentials = userCreds;
        
        readyGameStates = new HashSet();
        readyGameStates.clear();
        
        pendingGameStates = new HashSet();
        pendingGameStates.clear();
        
        
        gamesList.removeAll();
        gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingGamesJList.removeAll();
        pendingGamesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        DefaultListModel readyListModel = new DefaultListModel();
        DefaultListModel pendingListModel = new DefaultListModel();
        
        Iterator iter = userCreds.gameStateList.iterator();
        GameState thisState = null;
        
        int readyIndex = 0;
        int pendingIndex = 0;
        
        //Seperate the games into two groups: ready/not ready
        while (iter.hasNext())
        {
            thisState = (GameState)iter.next();
            //Add to ready list
            if ((thisState.isOpen == false) && (thisState.currentPlayer.name.equals(userCreds.player.name)))
            {
                readyGameStates.add(thisState);
            }
            
            //Add to pending list
            else
            {
                pendingGameStates.add(thisState);
                pendingListModel.add(pendingIndex, ("#" + thisState.idNumber + " / " + thisState.playerOne.name + " vs " + thisState.playerTwo.name + " / " + thisState.scenario.scenarioType.toString() + " / turn " + thisState.turnNumber));
            }    
            
        }
         
        //Create the ready array and populate the lists
        readyGameArray = new GameState[readyGameStates.size()];
        iter = readyGameStates.iterator();
        thisState = null;
        
        while (iter.hasNext())
        {
            thisState = (GameState)iter.next();
            readyGameArray[readyIndex] = thisState;
            readyIndex++;
        }
        
        for (int i = 0; i < readyGameArray.length; i++)
        {
            readyListModel.add(i, ("#" + readyGameArray[i].idNumber + " / " + readyGameArray[i].playerOne.name + " vs " + readyGameArray[i].playerTwo.name + " / " + readyGameArray[i].scenario.scenarioType.toString() + " / turn " + readyGameArray[i].turnNumber));
        }
        
        gamesList.setModel(readyListModel);
        gamesList.setSelectedIndex(0);
        
        pendingGamesJList.setModel(pendingListModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        gamesList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        playJButton = new javax.swing.JButton();
        surrenderJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pendingGamesJList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        gamesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        gamesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(gamesList);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel1.setText("Ready To Play");

        playJButton.setText("PLAY");
        playJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playJButtonActionPerformed(evt);
            }
        });

        surrenderJButton.setText("SURRENDER");

        cancelJButton.setText("CANCEL");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        pendingGamesJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        pendingGamesJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        pendingGamesJList.setEnabled(false);
        pendingGamesJList.setFocusable(false);
        jScrollPane2.setViewportView(pendingGamesJList);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel2.setText("Waiting For Other Player");

        refreshButton.setText("REFRESH");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(surrenderJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(playJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(cancelJButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(surrenderJButton)
                    .addComponent(refreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(playJButton)
                .addGap(26, 26, 26)
                .addComponent(cancelJButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_cancelJButtonActionPerformed

    private void playJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playJButtonActionPerformed
        // TODO add your handling code here:
        
        if (gamesList.getSelectedValue() == null)
        {
            JOptionPane.showMessageDialog(this, "No game selected. Start a new one!",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        }    

        //Perform a quick check to see whether or not another game is already in progress
        else if (gameMaster.currentGameState != null)
        {
            //Object[] options = { "OK", "CANCEL" };
            //JOptionPane.showConfirmDialog(this, "This will end your current game. Continue?", "END CURRENT GAME?", JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                    
            Object[] options = { "OK", "CANCEL" };
            JOptionPane pane = new JOptionPane();
            pane.showOptionDialog(this, "This will end your current game turn. Continue?", "Warning",
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[0]);
            
            Object selectedValue = pane.getValue();
            
            if (selectedValue.equals("OK"))
            {
                gameMaster.setCurrentGameState(readyGameArray[gamesList.getSelectedIndex()]);
                this.setVisible(false);
            }
        }
        
        else
        {
            gameMaster.setCurrentGameState(readyGameArray[gamesList.getSelectedIndex()]);
            this.setVisible(false);
        }
    }//GEN-LAST:event_playJButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // TODO add your handling code here:
        refreshAndPopulate();
    }//GEN-LAST:event_refreshButtonActionPerformed

    //REFRESH AND POPULATE
    //Performs the messy business of sorting the GameStates returned by the server into
    //two groups and placing them in the correct JList
    public void refreshAndPopulate()
    {
        if (gameMaster.activePlayerCredentials == null)
        {
            JOptionPane.showMessageDialog(this, "Aw, crap: activePlayerCredntials are null.",
            "UH OH", JOptionPane.WARNING_MESSAGE);
        }
        
        else if ((gameMaster.activePlayerCredentials.player.name == null) || (gameMaster.activePlayerCredentials.player.password == null))
        {
            //error msg
            JOptionPane.showMessageDialog(this, "Aw, crap: username/password are null.",
            "UH OH", JOptionPane.WARNING_MESSAGE);
        }        
        
        else
        {
            //Reach out and obtain a refreshed version of the loginObject
            LoginManager loginMan = new LoginManager(gameMaster.server, gameMaster.port, null, null);
            LoginObject refreshedCreds = loginMan.getLoginObject(activePlayerCredentials.player.name, gameMaster.activePlayerCredentials.player.password);
            
            if (refreshedCreds != null)
            {
                readyGameStates.clear();
                pendingGameStates.clear();

                gamesList.removeAll();
                gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                pendingGamesJList.removeAll();
                //pendingGamesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                DefaultListModel readyListModel = new DefaultListModel();
                DefaultListModel pendingListModel = new DefaultListModel();

                Iterator iter = refreshedCreds.gameStateList.iterator();
                GameState thisState = null;

                int readyIndex = 0;
                int pendingIndex = 0;

                //Seperate the games into two groups: ready/not ready
                while (iter.hasNext())
                {
                    thisState = (GameState)iter.next();

                    if ((thisState.isOpen == false) && (thisState.currentPlayer.name.equals(activePlayerCredentials.player.name)))
                    {
                        readyGameStates.add(thisState);
                    }

                    else
                    {
                        pendingGameStates.add(thisState);
                        pendingListModel.add(pendingIndex, ("#" + thisState.idNumber + " / " + thisState.playerOne.name + " vs " + thisState.playerTwo.name + " / " + thisState.scenario.scenarioType.toString() + " / turn " + thisState.turnNumber));
                    }    
                }

                //Create the ready array and populate the lists
                readyGameArray = new GameState[readyGameStates.size()];
                iter = readyGameStates.iterator();
                thisState = null;

                while (iter.hasNext())
                {
                    thisState = (GameState)iter.next();
                    readyGameArray[readyIndex] = thisState;
                    readyIndex++;
                }

                for (int i = 0; i < readyGameArray.length; i++)
                {
                    readyListModel.add(i, ("#" + readyGameArray[i].idNumber + " / " + readyGameArray[i].playerOne.name + " vs " + readyGameArray[i].playerTwo.name + " / " + readyGameArray[i].scenario.scenarioType.toString() + " / turn " + readyGameArray[i].turnNumber));
                }

                gamesList.setModel(readyListModel);
                gamesList.setSelectedIndex(0);

                pendingGamesJList.setModel(pendingListModel);
                
                gameMaster.activePlayerCredentials = refreshedCreds;
            }    
        }
    }
    
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
            java.util.logging.Logger.getLogger(MyGamesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyGamesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyGamesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyGamesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MyGamesFrame(gameMaster,activePlayerCredentials).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelJButton;
    private javax.swing.JList gamesList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList pendingGamesJList;
    private javax.swing.JButton playJButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton surrenderJButton;
    // End of variables declaration//GEN-END:variables
}
