package keel.GraphInterKeel.experiments;
/*
File:	Credits.java
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;

public class Credits extends JFrame implements ActionListener {

    protected JLabel titleLabel,  aboutLabel[];
    protected static int labelCount = 33;
    protected static int aboutWidth = 500;
    protected static int aboutHeight = 600;
    protected static int aboutTop = 200;
    protected static int aboutLeft = 350;
    protected Font titleFont,  bodyFont,  miniFont,  subtFont;
    protected ResourceBundle resbundle;

    /**
     * Default builder
     */
    public Credits() {
        super("");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/logo.gif")));
        this.setTitle("About ...");
        this.setResizable(false);
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);

        // Initialize useful fonts
        titleFont = new Font("Arial", Font.BOLD, 16);
        if (titleFont == null) {
            titleFont = new Font("Arial", Font.BOLD, 16);
        }
        bodyFont = new Font("Arial", Font.PLAIN, 12);
        if (bodyFont == null) {
            bodyFont = new Font("Arial", Font.PLAIN, 12);
        }
        miniFont = new Font("Arial", Font.PLAIN, 10);
        if (miniFont == null) {
            miniFont = new Font("Arial", Font.PLAIN, 10);
        }
        subtFont = new Font("Arial", Font.BOLD, 14);
        if (subtFont == null) {
            subtFont = new Font("Arial", Font.BOLD, 14);
        }
        this.setBackground(new Color(225, 225, 225));
        this.getContentPane().setLayout(new BorderLayout(15, 15));

        aboutLabel = new JLabel[labelCount];
        aboutLabel[0] = new JLabel("");
        aboutLabel[1] = new JLabel("KEEL: Knowledge Extraction based on Evolutionary Learning");
        aboutLabel[1].setFont(titleFont);
        aboutLabel[2] = new JLabel("Project code TIN2005-08386-C05-01. Version beta-1");
        aboutLabel[2].setFont(subtFont);
        aboutLabel[3] = new JLabel("http://sci2s.ugr.es/keel");
        aboutLabel[3].setFont(subtFont);

        aboutLabel[4] = new JLabel("");
        aboutLabel[5] = new JLabel("(C) Universities of Granada, C�rdoba, Ja�n, Ramon Llull and Oviedo ");
        aboutLabel[5].setFont(bodyFont);
        aboutLabel[6] = new JLabel("JDK " + System.getProperty("java.version"));
        aboutLabel[6].setFont(bodyFont);
        aboutLabel[7] = new JLabel(" ");
        aboutLabel[7].setFont(bodyFont);
        aboutLabel[8] = new JLabel("Granada Subproject:");
        aboutLabel[8].setFont(subtFont);
        aboutLabel[9] = new JLabel("Ph. D. Herrera, Francisco; " +
                "Mr. Alcal�, Jes�s; " +
                "Ph.D. Alcal�, Rafael; " +
                "Ph.D. Lozano, Manuel ");
        aboutLabel[9].setFont(miniFont);
        aboutLabel[10] = new JLabel("Ph.D. Casillas, Jorge; " +
                "Ph.D. S�nchez, Ana Mar�a; " +
                "Ph.D. Villar, Pedro; " +
                "Ph.D. Peregr�n, Antonio");
        aboutLabel[10].setFont(miniFont);

        aboutLabel[11] = new JLabel("Mr. M�rquez, Francisco A.; " +
                "Mr. Molina, Daniel; " +
                "Ph. D. Garc�a, Salvador; " +
                "Mr. Fern�ndez, Alberto");
        aboutLabel[11].setFont(miniFont);

        aboutLabel[12] = new JLabel("Ph.D. Mart�nez, Francisco J.; " +
                "Mr. D�ez, Javier; " +
                "Ph.D. P�rez, Elena; " +
                "Ph.D. Bull, Larry");
        aboutLabel[12].setFont(miniFont);

        aboutLabel[13] = new JLabel("Mr. Luengo, Juli�n; " +
                "Mr. Robles, Ignacio; " + "Mr Derrac, Joaquin");


        aboutLabel[13].setFont(miniFont);

        aboutLabel[14] = new JLabel("");
        aboutLabel[15] = new JLabel("C�rdoba Subproject:");
        aboutLabel[15].setFont(subtFont);

        aboutLabel[16] = new JLabel("Ph.D. Herv�s, C�sar; Ph.D. Carbonero, Mariano; Mr. Gonz�lez, Pedro");
        aboutLabel[16].setFont(miniFont);
        aboutLabel[17] = new JLabel("Ph.D. Mart�nez, Francisco; Ph.D. Mart�nez, Alfonso C.; Ph.D. Ort�z, Domingo; Ph.D. Torres, Mercedes;");
        aboutLabel[17].setFont(miniFont);
        aboutLabel[18] = new JLabel("Ph.D. Romero, Crist�bal; Ph.D. Ventura, Sebati�n; Mr. Fern�ndez, Juan C.; Mr. Delgado, Jos� A.");
        aboutLabel[18].setFont(miniFont);
        aboutLabel[19] = new JLabel("Mrs. Zafra, Amelia");
        aboutLabel[19].setFont(miniFont);
        aboutLabel[20] = new JLabel("");
        aboutLabel[20] = new JLabel("Ramon Llull Subproject:");
        aboutLabel[20].setFont(subtFont);
        aboutLabel[21] = new JLabel("Ph.D. Garrell, Josep M.; Ph.D. Bernado, Ester; Ph.D. Bacardit, Jaume; Mr. Camps, Joan");
        aboutLabel[21].setFont(miniFont);
        aboutLabel[22] = new JLabel("Mr. R�os, Joaquim; Mr. Barrabeig, Miguel A.; Mr. Orriols, Albert; Mr. Teixido, Francesc");
        aboutLabel[22].setFont(miniFont);
        aboutLabel[23] = new JLabel("Ph.D. Kam, Tin");
        aboutLabel[23].setFont(miniFont);
        aboutLabel[24] = new JLabel("");
        aboutLabel[25] = new JLabel("Ja�n Subproject:");
        aboutLabel[25].setFont(subtFont);
        aboutLabel[26] = new JLabel("Ph.D. del Jes�s, Mar�a Jos�; Mr. Aguilera, Jos� Joaqu�n; Mr. Berlanga, Fracisco J.; Ph.D. Cano, Jos� R.");
        aboutLabel[26].setFont(miniFont);
        aboutLabel[27] = new JLabel("Mr. Gonz�lez, Pedro; Ph.D. Mesonero, Mikel; Mrs. P�rez, Mar�a Dolores; Ph.D. Rivas, V�ctor");
        aboutLabel[27].setFont(miniFont);
        aboutLabel[28] = new JLabel("");
        aboutLabel[29] = new JLabel("Oviedo Subproject:");
        aboutLabel[29].setFont(subtFont);
        aboutLabel[30] = new JLabel("Ph.D. S�nchez, Luciano; Ph.D. Otero, Jos�; Ph.D. Villar, Jos� R.");
        aboutLabel[30].setFont(miniFont);
        aboutLabel[31] = new JLabel("Ph.D. de la Cal, Enrique; Mr. Otero, Adolfo; Mr. Junco, Lu�s; Mrs. Su�rez, M. del Rosario");
        aboutLabel[31].setFont(miniFont);
        aboutLabel[32] = new JLabel("Ph.D. Hoffmann, Frank");
        aboutLabel[32].setFont(miniFont);


        Panel textPanel2 = new Panel(new GridLayout(labelCount, 1));
        textPanel2.setBackground(new Color(225, 225, 225));
        for (int i = 0; i < labelCount; i++) {
            aboutLabel[i].setHorizontalAlignment(JLabel.CENTER);
            textPanel2.add(aboutLabel[i]);
        }

        this.getContentPane().add(textPanel2, BorderLayout.CENTER);
        this.pack();
        this.setLocation(aboutLeft, aboutTop);
        this.setSize(aboutWidth, aboutHeight);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SymWindow extends java.awt.event.WindowAdapter {

        public void windowClosing(java.awt.event.WindowEvent event) {
            setVisible(false);
        }
    }

    /**
     * Action performed
     * @param newEvent Handler
     */
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }

    private void jbInit() throws Exception {
        /*for (int i=0; i<aboutLabel.length; i++)
        {
        aboutLabel[i].setFont(new java.awt.Font("Arial", 0, 11));
        }*/

        this.getContentPane().setBackground(Color.white);
        this.setFont(new java.awt.Font("Arial", 0, 11));
    }
}