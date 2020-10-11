package form;

import reader.Reader;
import receipt.EDEKA;
import receipt.Generic;
import receipt.generic.Receipt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;


public class App extends JFrame {
    private static App window = new App();


    final private String RESOURCES = App.class.getResource("/").getPath();
    private Receipt RECEIPT;
    private String readText = "";


    public App() {
        initComponents();


        // Load perfect file Button
        btnLoadPerfectFile.addActionListener(e -> {
            Reader reader = new Reader();
            try {
                readText = reader.read(new File(RESOURCES + "perfect-read.txt"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            txtLoadedText.setText(readText);
        });

        //---------------------- Additional configuration
        txtPathLabel.setBackground(null);
        txtPathLabel.setText(txtPathLabel.getText() + RESOURCES);
        txtPathKeyReleased(null);
        cbbFiles.setSelectedItem("Test-Bon.jpg");
        lblLoading.setVisible(false);
    }

    public static void main(String[] args){
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    //------------- GUI Element Function
    // Set elements of drop-down-menu
    private void txtPathKeyReleased(KeyEvent e) {
/*
        if(e == null || e.getKeyCode() != KeyEvent.VK_ENTER)
            return;

        // Clear menu
        cbbFiles.removeAllItems();
*/
        // Directory path here
        File folder = new File(RESOURCES);
        File[] listOfFiles = folder.listFiles();

        //System.out.println("Files in " + App.class.getResource("/").getPath());
        assert listOfFiles != null;
        for (File currentFile : listOfFiles) {
            if (currentFile.isFile()) {
                cbbFiles.addItem(currentFile.getName());
                //System.out.println(files);
            }
        }
    }

    // Drop-Down for Files trigger on StateChange
    private void cbbFilesItemStateChanged(ItemEvent e) {
        // Just trigger on changed selection
        if(e.getStateChange() == ItemEvent.SELECTED)
            txtPath.setText(e.getItem().toString());
    }

    // Scan Text Button
    private void btnScanActionPerformed(ActionEvent e) {
        txtUnderstoodOutput.setText("");

        readText = txtLoadedText.getText();

        // Analyze Text Write Data To Object
        try{
            RECEIPT = new EDEKA(readText);
        }catch (Exception ex){
            txtUnderstoodOutput.setText(ex.getMessage());
            ex.printStackTrace();
        }

        if (RECEIPT instanceof EDEKA){
            //txtUnderstoodOutput.setText(AssignmentLogic.findAll(readText));
            EDEKA receipt = new EDEKA(readText);
            /*String text = "testing ground for Template:\n";
            text += receipt.getTemplate().findAddress() + "\n\n";
            text += receipt.getTemplate().findTaxExemption() + "\n";
            text += receipt.export();*/

            txtUnderstoodOutput.setText(receipt.export());
        } else if (RECEIPT instanceof Generic){
            txtUnderstoodOutput.setText("testing ground for Template:\n" + RECEIPT);
        }

        lblLoading.setVisible(false);
    }

    // Load selected File Button
    private void btnLoadFileActionPerformed(ActionEvent e) {
        final Reader reader = new Reader();

        File fileFromTF = new File(RESOURCES + txtPath.getText());
        String readFile = "";
        try {
            readFile = reader.read(fileFromTF);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        readText = readFile;
        txtLoadedText.setText(readFile);
    }

    // Test PDFBox with and without SortByPosition
    private void btnPDFBoxTestActionPerformed(ActionEvent e) {
        final Reader reader = new Reader();

        File fileFromTF = new File(RESOURCES + txtPath.getText());
        String withoutSortByPosition = "";
        String withSortByPosition = "";
        if(!reader.getFileType(fileFromTF).equals("pdf")){
            txtLoadedText.setText("File not PDF");
            return;
        }
        try {
            reader.setSortByPosition(true);
            withSortByPosition = reader.read(fileFromTF);
            reader.setSortByPosition(false);
            withoutSortByPosition = reader.read(fileFromTF);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        txtUnderstoodOutput.setText("Read PDF with SortByPosition = true: \n" + withSortByPosition);

        txtLoadedText.setText("Read PDF with SortByPosition = false: \n" + withoutSortByPosition);
    }

    // Save To File Button
    private void btnSaveToTXTActionPerformed(ActionEvent e) {
        if(RECEIPT == null){
            lblSaveOutput.setText("Receipt empty, nothing to save");
        }else{
            RECEIPT.saveToFile("out");
            lblSaveOutput.setText("Saved out.txt");
        }

    }




    //------------- Generated Initialisation Don't Touch
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Benjamin Herzberger
        pnlOutput = new JPanel();
        scrollPane1 = new JScrollPane();
        txtUnderstoodOutput = new JTextArea();
        scrollPane2 = new JScrollPane();
        txtLoadedText = new JTextArea();
        pnlButtons = new JPanel();
        lblLoading = new JLabel();
        cbbFiles = new JComboBox();
        lblFile = new JLabel();
        txtPath = new JTextField();
        btnLoadFile = new JButton();
        btnLoadPerfectFile = new JButton();
        btnScan = new JButton();
        txtPathLabel = new JTextPane();
        btnPDFBoxTest = new JButton();
        btnSaveToTXT = new JButton();
        lblSaveOutput = new JLabel();
        hSpacer1 = new JPanel(null);

        //======== this ========
        setTitle("Semantik Erkennung");
        var contentPane = getContentPane();

        //======== pnlOutput ========
        {
            pnlOutput.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing.
            border. EmptyBorder( 0, 0, 0, 0) , "JFor\u006dDesi\u0067ner \u0045valu\u0061tion", javax. swing. border. TitledBorder. CENTER
            , javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font
            .BOLD ,12 ), java. awt. Color. red) ,pnlOutput. getBorder( )) ); pnlOutput. addPropertyChangeListener (
            new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("bord\u0065r"
            .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );

            //======== scrollPane1 ========
            {

                //---- txtUnderstoodOutput ----
                txtUnderstoodOutput.setFont(new Font("Consolas", Font.PLAIN, 12));
                scrollPane1.setViewportView(txtUnderstoodOutput);
            }

            //======== scrollPane2 ========
            {

                //---- txtLoadedText ----
                txtLoadedText.setFont(new Font("Consolas", Font.PLAIN, 12));
                scrollPane2.setViewportView(txtLoadedText);
            }

            GroupLayout pnlOutputLayout = new GroupLayout(pnlOutput);
            pnlOutput.setLayout(pnlOutputLayout);
            pnlOutputLayout.setHorizontalGroup(
                pnlOutputLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, pnlOutputLayout.createSequentialGroup()
                        .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
            );
            pnlOutputLayout.setVerticalGroup(
                pnlOutputLayout.createParallelGroup()
                    .addGroup(pnlOutputLayout.createSequentialGroup()
                        .addGroup(pnlOutputLayout.createParallelGroup()
                            .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE)
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
                        .addContainerGap())
            );
        }

        //======== pnlButtons ========
        {

            //---- lblLoading ----
            lblLoading.setText("Loading...");

            //---- cbbFiles ----
            cbbFiles.addItemListener(e -> cbbFilesItemStateChanged(e));

            //---- lblFile ----
            lblFile.setText("Select file:");

            //---- txtPath ----
            txtPath.setText("Test-Bon.jpg");
            txtPath.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    txtPathKeyReleased(e);
                }
            });

            //---- btnLoadFile ----
            btnLoadFile.setText("Load File");
            btnLoadFile.addActionListener(e -> btnLoadFileActionPerformed(e));

            //---- btnLoadPerfectFile ----
            btnLoadPerfectFile.setText("Load Perfect File");

            //---- btnScan ----
            btnScan.setText("Scan Text (EDEKA)");
            btnScan.addActionListener(e -> btnScanActionPerformed(e));

            //---- txtPathLabel ----
            txtPathLabel.setEditable(false);
            txtPathLabel.setBorder(null);
            txtPathLabel.setText("File must be in: ");

            //---- btnPDFBoxTest ----
            btnPDFBoxTest.setText("PDFBox test position");
            btnPDFBoxTest.addActionListener(e -> btnPDFBoxTestActionPerformed(e));

            //---- btnSaveToTXT ----
            btnSaveToTXT.setText("Save as out.txt File");
            btnSaveToTXT.addActionListener(e -> btnSaveToTXTActionPerformed(e));

            GroupLayout pnlButtonsLayout = new GroupLayout(pnlButtons);
            pnlButtons.setLayout(pnlButtonsLayout);
            pnlButtonsLayout.setHorizontalGroup(
                pnlButtonsLayout.createParallelGroup()
                    .addComponent(txtPath, GroupLayout.Alignment.TRAILING)
                    .addComponent(cbbFiles, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlButtonsLayout.createParallelGroup()
                            .addComponent(btnLoadFile, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(btnLoadPerfectFile, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(lblLoading, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnScan, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(txtPathLabel, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addComponent(btnPDFBoxTest, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.TRAILING, pnlButtonsLayout.createSequentialGroup()
                                .addGroup(pnlButtonsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblSaveOutput, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                    .addComponent(lblFile, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                            .addComponent(btnSaveToTXT, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)))
            );
            pnlButtonsLayout.setVerticalGroup(
                pnlButtonsLayout.createParallelGroup()
                    .addGroup(pnlButtonsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtPathLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblFile)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbFiles, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btnLoadFile)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLoadPerfectFile)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblLoading)
                        .addGap(35, 35, 35)
                        .addComponent(btnScan)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSaveToTXT)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSaveOutput)
                        .addGap(41, 41, 41)
                        .addComponent(btnPDFBoxTest)
                        .addContainerGap(234, Short.MAX_VALUE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlButtons, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                    .addComponent(hSpacer1, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(pnlOutput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(pnlButtons, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addComponent(pnlOutput, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(hSpacer1, GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Benjamin Herzberger
    private JPanel pnlOutput;
    private JScrollPane scrollPane1;
    private JTextArea txtUnderstoodOutput;
    private JScrollPane scrollPane2;
    private JTextArea txtLoadedText;
    private JPanel pnlButtons;
    private JLabel lblLoading;
    private JComboBox cbbFiles;
    private JLabel lblFile;
    private JTextField txtPath;
    private JButton btnLoadFile;
    private JButton btnLoadPerfectFile;
    private JButton btnScan;
    private JTextPane txtPathLabel;
    private JButton btnPDFBoxTest;
    private JButton btnSaveToTXT;
    private JLabel lblSaveOutput;
    private JPanel hSpacer1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
