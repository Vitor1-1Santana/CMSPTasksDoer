/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer;

import java.awt.Frame;

/**
 *
 * @author vitor
 */
public class LoginWarning extends javax.swing.JDialog{
    
    OptionsDialog optionsDialog;

    /**
     * Creates new form LoginWarning
     */
    public LoginWarning(java.awt.Frame parent, boolean modal){
        super(parent, modal);
                try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Windows".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex){
            java.util.logging.Logger.getLogger(LoginWarning.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        initComponents();
        progressInfo.setText("");
        optionsDialog = new OptionsDialog((Frame) getParent(), true);
    }
    
    public void setNameAndNick(String name, String nick){
        warningUserText.setText(warningUserText.getText().replace("[USERNAME]", name));
        this.setSize(warningUserText.getX()+warningUserText.getPreferredSize().width, this.getHeight());
        nickName.setText(nick);
        tasksNumber.setText(""+CMSPTasksDoer.cmspCommunicator.getTasks().size());
        progressBar.setSize(146, 14);
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warningUserText = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        progressInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        nickName = new javax.swing.JLabel();
        tasksNumber = new javax.swing.JLabel();
        doTasks = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        optionsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Informação de conta");
        setBounds(0,0,0,warningUserText.getPreferredSize().width);

        warningUserText.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        warningUserText.setText("Bem vindo(a) [USERNAME]!");

        progressBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        progressBar.setMaximumSize(new java.awt.Dimension(146, 14));
        progressBar.setMinimumSize(new java.awt.Dimension(146, 14));

        progressInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressInfo.setText("progressInfo");

        jLabel3.setText("Nick:");

        jLabel4.setText("Quantidade restante:");

        nickName.setText("SeidyNadaNao");

        tasksNumber.setText("Muitas");

        doTasks.setText("Realizar atividades");

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        optionsButton.setText("Opções");
        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(doTasks)
                        .addGap(59, 59, 59)
                        .addComponent(optionsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(warningUserText)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tasksNumber))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nickName)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningUserText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nickName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tasksNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(progressInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(doTasks)
                    .addComponent(jButton2)
                    .addComponent(optionsButton))
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void optionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsButtonActionPerformed
        
        optionsDialog.setVisible(true);
    }//GEN-LAST:event_optionsButtonActionPerformed


//        java.awt.EventQueue.invokeLater(new Runnable(){
//            public void run(){
//                LoginWarning dialog = new LoginWarning(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter(){
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e){
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton doTasks;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel nickName;
    private javax.swing.JButton optionsButton;
    public javax.swing.JProgressBar progressBar;
    public javax.swing.JLabel progressInfo;
    private javax.swing.JLabel tasksNumber;
    private javax.swing.JLabel warningUserText;
    // End of variables declaration//GEN-END:variables
}
