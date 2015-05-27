/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import javax.swing.*;
import java.util.*;
/**
 *
 * @author skot
 */
public class CreateNewGameFrame extends javax.swing.JFrame {

    
    static LoginObject credentials;
    static NewGameManager myManager;
    static boolean opponentGoesFirst = false;
    static Player opponent;
    static ScenarioDescription scenarioDescription;
    /**
     * Creates new form CreateNewGameFrame
     */
    public CreateNewGameFrame(NewGameManager myMgr, LoginObject loginCreds) {
        
        myManager = myMgr;
        credentials = loginCreds;
        
        opponentGoesFirst = false;
        opponent = null;
        
        initComponents();
        
        this.setTitle("Start A New Game");
        
        //Setup the opponentList
        OpponentList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
       //Create an array to populate the list
        DefaultListModel listModel = new DefaultListModel();
        
        Player [] playerArray = new Player[credentials.registeredPlayers.size() + 1];
        
        Iterator iter = credentials.registeredPlayers.iterator();
        Player plyr;
        int index = 0;
        
        while (iter.hasNext())
        {
            plyr = (Player)iter.next();
            
            playerArray[index] = plyr;
            listModel.add(index, plyr.name);
            index++;
        }
        
        listModel.add(index, new PlayerAI("Ogre AI").name);
        
              
        OpponentList.setModel(listModel);
        OpponentList.setSelectedIndex(0);
        
        //Setup scenario selection
        scenarioJComboBox.removeAllItems();
        scenarioJComboBox.addItem(ScenarioType.Test);
//        scenarioJComboBox.addItem(ScenarioType.MkV);
//        scenarioJComboBox.addItem(ScenarioType.Test);
//        scenarioJComboBox.addItem(ScenarioType.Custom);
        
        scenarioDescription = new ScenarioDescription();
        scenarioDescriptionJTextArea.setText(scenarioDescription.description((ScenarioType)scenarioJComboBox.getSelectedItem()));
        
        OKButton.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        scenarioJComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        OKButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        OpponentList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        scenarioDescriptionJTextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel1.setText("Choose An Opponent");

        scenarioJComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        scenarioJComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scenarioJComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel2.setText("Choose A Scenario");

        OKButton.setText("OK");
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        OpponentList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        OpponentList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                OpponentListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(OpponentList);

        scenarioDescriptionJTextArea.setEditable(false);
        scenarioDescriptionJTextArea.setColumns(20);
        scenarioDescriptionJTextArea.setLineWrap(true);
        scenarioDescriptionJTextArea.setRows(5);
        jScrollPane2.setViewportView(scenarioDescriptionJTextArea);

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel3.setText("Scenario Description");

        jCheckBox1.setText("Opponent Goes Second");
        jCheckBox1.setActionCommand("Opponent Goes First");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        jLabel4.setText("Options");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(scenarioJComboBox, 0, 251, Short.MAX_VALUE)
                                    .addComponent(jLabel2)
                                    .addComponent(jScrollPane2))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox1)
                                    .addComponent(jLabel4))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(378, 378, 378)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(OKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scenarioJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1))
                        .addGap(104, 104, 104)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(OKButton)
                            .addComponent(cancelButton)))
                    .addComponent(jScrollPane1))
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        // TODO add your handling code here:
        
        //TODO: add preflight checks here
        
        //obtain a reference to the chosen opponent
        String opponentName =(String)OpponentList.getSelectedValue();
        
        //TODO: allow user to select the scenario
        myManager.createNewGame(opponentName, ScenarioType.MkIII);
            
    }//GEN-LAST:event_OKButtonActionPerformed

    private void OpponentListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_OpponentListValueChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_OpponentListValueChanged

    private void scenarioJComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scenarioJComboBoxActionPerformed
        // TODO add your handling code here:
        
        //Change the game description based on user input
        
    }//GEN-LAST:event_scenarioJComboBoxActionPerformed

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
            java.util.logging.Logger.getLogger(CreateNewGameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CreateNewGameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CreateNewGameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CreateNewGameFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CreateNewGameFrame(myManager,credentials).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton OKButton;
    public javax.swing.JList OpponentList;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTextArea scenarioDescriptionJTextArea;
    public javax.swing.JComboBox scenarioJComboBox;
    // End of variables declaration//GEN-END:variables
}
