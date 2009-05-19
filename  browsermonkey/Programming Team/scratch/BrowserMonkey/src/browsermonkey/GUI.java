/*
 * GUI.java
 *
 * Created on 16-Apr-2009, 17:27:19
 */

package browsermonkey;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import browsermonkey.utility.BrowserMonkeyLogger;

/**
 *
 * @author Paul Calcraft
 */
public class GUI extends javax.swing.JFrame {

    /** Creates new form GUI */
    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}
        initComponents();
        documentPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                panelChanged();
            }
        });
        loadFile("welcome.html");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        top = new javax.swing.JPanel();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        goButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        statusBar = new javax.swing.JLabel();
        documentScrollPanel = new javax.swing.JScrollPane();
        documentPanel = new browsermonkey.render.DocumentPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrowserMonkey");

        addressLabel.setText("Address:");

        addressField.setText("welcome.html");
        addressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addressFieldActionPerformed(evt);
            }
        });

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topLayout = new javax.swing.GroupLayout(top);
        top.setLayout(topLayout);
        topLayout.setHorizontalGroup(
            topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressField, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(goButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseButton)
                .addContainerGap())
        );
        topLayout.setVerticalGroup(
            topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressLabel)
                    .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton)
                    .addComponent(goButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        statusBar.setText("Ready");
        statusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 5));

        documentScrollPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        documentScrollPanel.setViewportView(documentPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addComponent(documentScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addComponent(top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        loadFile(addressField.getText());
    }//GEN-LAST:event_goButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(addressField.getText()));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        String path = fc.getSelectedFile().getAbsolutePath();
        addressField.setText(path);
        loadFile(path);
    }//GEN-LAST:event_browseButtonActionPerformed

    private void addressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressFieldActionPerformed
        goButtonActionPerformed(null);
    }//GEN-LAST:event_addressFieldActionPerformed

    private void panelChanged() {
        this.addressField.setText(documentPanel.getAddress());
        addressField.setCaretPosition(addressField.getText().length());
    }

    private void loadFile(String path){
        try {
            documentPanel.load(path);
        } catch (FileNotFoundException ex) {
            BrowserMonkeyLogger.warning("File not found: "+path);
            loadFile("404.html");
        } catch (IOException ex) {
            BrowserMonkeyLogger.warning("File read error: "+path);
            // TODO: Make alternative error page for file read errors.
            loadFile("404.html");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton browseButton;
    private browsermonkey.render.DocumentPanel documentPanel;
    private javax.swing.JScrollPane documentScrollPanel;
    private javax.swing.JButton goButton;
    private javax.swing.JLabel statusBar;
    private javax.swing.JPanel top;
    // End of variables declaration//GEN-END:variables

}
