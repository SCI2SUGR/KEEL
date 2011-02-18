/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/
 
/**
 * <p>Title: Keel</p>
 * <p>Description: Modules screen</p>
 *
 * @author Isaac Triguero Velázquez
 * @version 0.0
 */
package keel.GraphInterKeel.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent.*;
import javax.swing.*;
import keel.GraphInterKeel.experiments.*;
import java.awt.Rectangle;
import keel.GraphInterKeel.statistical.StatisticalF;

public class FrameModules extends JFrame {

    JPanel contentPane;
    JLabel fondo = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel labelSalir = new JLabel();
    JLabel labelLQD = new JLabel();
    JLabel labelLQD2 = new JLabel();
    JLabel labelImbalance = new JLabel();
    JLabel labelNonParametric = new JLabel();
    JLabel labelMil = new JLabel();
    JLabel labelBack = new JLabel();
    JLabel lqd = new JLabel();
    JLabel imbalance = new JLabel();
    JLabel nonParametric = new JLabel();
    JLabel mil = new JLabel();
    JLabel back = new JLabel();
    JButton exit = new JButton();
    public String raiz = "../Datasets/";
    JLabel keel = new JLabel();
    JLabel logotipo = new JLabel();
    JLabel logotipoSoft = new JLabel();
    JLabel barraExit = new JLabel();
    JLabel accionExit = new JLabel();
    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/

    //0->Button Experiments is pressed, 1->Button Teaching is pressed
    public static int buttonPressed = 0;

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/
    /**
     * Builder
     */
    public FrameModules() {
        try {
            initializeModulesFrame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Parent frame
     */
    protected keel.GraphInterKeel.menu.Frame parent = null;

    /**
     * <p>
     * Sets parent
     * </p>
     * @param parent Frame parent
     */
    public void setParent(keel.GraphInterKeel.menu.Frame parent) {
        this.parent = parent;
    }

    /**
     * Initialize frame
     *
     * @throws java.lang.Exception
     */
    private void initializeModulesFrame() throws Exception {
        // frame initialization
        this.setFont(new java.awt.Font("Arial", 0, 11));
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                this.getClass().getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
        this.setSize(new Dimension(640, 480));
        this.setTitle("Keel");
        this.setResizable(false);

        // Create panel
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(null);

        // panel background
        fondo.setText("");
        fondo.setBounds(new Rectangle(0, 0, 640, 480));
        fondo.setFont(new java.awt.Font("Arial", 0, 11));
        fondo.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/fondo.gif")));


        exit.setText("Exit KEEL");
        exit.setBounds(new Rectangle(294, 327, 129, 36));
        exit.setFont(new java.awt.Font("Arial", 0, 11));

        // labels associated to buttons
        labelSalir.setFont(new java.awt.Font("Arial", 1, 18));
        labelSalir.setForeground(Color.white);
        labelSalir.setText("Exit KEEL");
        labelSalir.setBounds(new Rectangle(40, 412, 595, 27));
        labelSalir.setVisible(false);

        labelBack.setFont(new java.awt.Font("Arial", 1, 18));
        labelBack.setForeground(Color.white);
        labelBack.setText("Back to Keel Tool Menu");
        labelBack.setBounds(new Rectangle(40, 412, 595, 27));
        labelBack.setVisible(false);

        labelLQD.setFont(new java.awt.Font("Arial", 1, 18));
        labelLQD.setForeground(Color.white);
        labelLQD.setText("Experiments Design with Low Quality Data");
        labelLQD.setBounds(new Rectangle(40, 412, 465, 27));
        labelLQD.setVisible(false);

        labelImbalance.setFont(new java.awt.Font("Arial", 1, 18));
        labelImbalance.setForeground(Color.white);
        labelImbalance.setText("Experiments with Imbalance Data Sets");
        labelImbalance.setBounds(new Rectangle(40, 412, 465, 27));
        labelImbalance.setVisible(false);


        labelNonParametric.setFont(new java.awt.Font("Arial", 1, 18));
        labelNonParametric.setForeground(Color.white);
        labelNonParametric.setText("Non-Parametric Statistical Analysis");
        labelNonParametric.setBounds(new Rectangle(40, 412, 465, 27));
        labelNonParametric.setVisible(false);


        labelMil.setFont(new java.awt.Font("Arial", 1, 17));
        labelMil.setForeground(Color.white);
        labelMil.setText("Experiments with Multiple Instance Learning");
        labelMil.setBounds(new Rectangle(40, 412, 465, 27));
        labelMil.setVisible(false);

        contentPane.setFont(new java.awt.Font("Arial", 0, 11));

        keel.setText("");
        keel.setBounds(new Rectangle(160, 13, 336, 33));
        keel.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mod.png")));
        logotipo.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/logotipo.png")));
        logotipo.setBounds(new Rectangle(550, 13, 65, 43));
        logotipo.addMouseListener(new FrameModules_logotipo_mouseAdapter(this));

        logotipoSoft.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/Software.png")));
        logotipoSoft.setBounds(new Rectangle(30, 13, 75, 43));
        logotipoSoft.addMouseListener(new FrameModules_logotipoSoft_mouseAdapter(this));

        barraExit.setBounds(new Rectangle(0, 401, 751, 50));
        barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit.png")));
        accionExit.setText("jLabel2");
        accionExit.setBounds(new Rectangle(536, 409, 94, 38));
        accionExit.addMouseListener(new FrameModules_accionExit_mouseAdapter(this));

        back.setBounds(new Rectangle(406, 409, 110, 38));
        back.addMouseListener(new FrameModules_back_mouseAdapter(this));

        lqd.setBounds(new Rectangle(50, 140, 244, 40));
        lqd.addMouseListener(new Frame_lqd_mouseAdapter(this));

        imbalance.setBounds(new Rectangle(50, 200, 264, 38));
        imbalance.addMouseListener(new Frame_imbalance_mouseAdapter(this));

        nonParametric.setBounds(new Rectangle(50, 260, 400, 42));
        nonParametric.addMouseListener(new Frame_nonParametric_mouseAdapter(this));

        mil.setBounds(new Rectangle(50, 320, 400, 42));
        mil.addMouseListener(new Frame_mil_mouseAdapter(this));

        lqd.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/lqd.png")));
        imbalance.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/imbalance.png")));
        nonParametric.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/nonParametric.png")));
        mil.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mil.png")));
        back.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/back.png")));

        contentPane.add(back, null);
        contentPane.add(labelBack, null);

        contentPane.add(labelSalir, null);
        contentPane.add(labelLQD2, null);
        contentPane.add(labelLQD, null);
        contentPane.add(labelImbalance, null);
        contentPane.add(labelNonParametric, null);
        contentPane.add(labelMil, null);
        //contentPane.add(lqd);
        contentPane.add(mil);
        contentPane.add(imbalance);
        contentPane.add(nonParametric);
        contentPane.add(keel);
        contentPane.add(logotipo);
        contentPane.add(logotipoSoft);
        contentPane.add(barraExit);
        contentPane.add(fondo, null);
        contentPane.add(accionExit);
    }

    @Override
    /**
     *  Closing the application
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    // Exit button
    void exit_mouseEntered(MouseEvent e) {
        labelSalir.setVisible(true);
    }

    void exit_mouseExited(MouseEvent e) {
        labelSalir.setVisible(false);
        exit.setBounds(new Rectangle(15, 340, 129, 36));
    }

    void exit_mousePressed(MouseEvent e) {
        exit.setBounds(new Rectangle(16, 341, 129, 36));
    }

    void experimental_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.HAND_CURSOR);

    }

    void experimental_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.DEFAULT_CURSOR);

    }

    void exit_mouseReleased(MouseEvent e) {
        exit.setBounds(new Rectangle(15, 340, 129, 36));
        System.exit(0);
    }

    /**
     * Enter in exit button
     *
     * @param e Event
     */
    public void accionExit_mouseEntered(MouseEvent e) {
        barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit2.png")));
        labelSalir.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from exit button
     *
     * @param e Event
     */
    public void accionExit_mouseExited(MouseEvent e) {
        barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit.png")));
        labelSalir.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Releasing exit button
     *
     * @param e Event
     */
    public void accionExit_mouseReleased(MouseEvent e) {
        System.exit(0);
    }

    /**
     * Enter in back button
     *
     * @param e Event
     */
    public void back_mouseEntered(MouseEvent e) {
        back.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/back2.png")));
        labelBack.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from back button
     *
     * @param e Event
     */
    public void back_mouseExited(MouseEvent e) {
        back.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/back.png")));
        labelBack.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Releasing back button
     *
     * @param e Event
     */
    public void back_mouseReleased(MouseEvent e) {
        this.parent.setVisible(true);
        this.setVisible(false);
    }

    /**
     * Enter in LQD button
     *
     * @param e Event
     */
    public void lqd_mouseEntered(MouseEvent e) {
        lqd.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/lqd2.png")));
        labelLQD.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from lqd button
     *
     * @param e Event
     */
    public void lqd_mouseExited(MouseEvent e) {
        lqd.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/lqd.png")));
        labelLQD.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Enter in imbalanced button
     *
     * @param e Event
     */
    public void imbalance_mouseEntered(MouseEvent e) {
        imbalance.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/imbalance2.png")));
        labelImbalance.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from imbalanced button
     *
     * @param e Event
     */
    public void imbalance_mouseExited(MouseEvent e) {
        imbalance.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/imbalance.png")));
        labelImbalance.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Enter in statistical button
     *
     * @param e Event
     */
    public void nonParametric_mouseEntered(MouseEvent e) {
        nonParametric.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/nonParametric2.png")));
        labelNonParametric.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from statistical button
     *
     * @param e Event
     */
    public void nonParametric_mouseExited(MouseEvent e) {
        nonParametric.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/nonParametric.png")));
        labelNonParametric.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Enter in MIL button
     *
     * @param e Event
     */
    public void mil_mouseEntered(MouseEvent e) {
        mil.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mil2.png")));
        labelMil.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exit from MIL button
     *
     * @param e Event
     */
    public void mil_mouseExited(MouseEvent e) {
        mil.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mil.png")));
        labelMil.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Entering in LQD module
     *
     * @param e Event
     */
    public void lqd_mouseReleased(MouseEvent e) {

        buttonPressed = 0;

        Experiments frame = new Experiments(parent, Experiments.LQD);

        //LUEGO SERA LA MIA
        // frame.objType = Experiments.INVESTIGATIONLQD;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);
    }

    /**
     * Entering in MIL module
     *
     * @param e Event
     */
    public void mil_mouseReleased(MouseEvent e) {

        buttonPressed = 0;
        Experiments frame = new Experiments(parent, Experiments.MULTIINSTANCE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);

        frame.activateUpperMenu_principals();
    }

    /**
     * Entering in Imbalanced module
     *
     * @param e Event
     */
    public void imbalance_mouseReleased(MouseEvent e) {

        buttonPressed = 0;
        Experiments frame = new Experiments(parent, Experiments.IMBALANCED);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);

        frame.activateUpperMenu_principals();

    }

    /**
     * Entering in Statistical module
     *
     * @param e Event
     */
    public void nonParametric_mouseReleased(MouseEvent e) {

        StatisticalF frame = new StatisticalF();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        frame.setParent(this);
        this.setVisible(false);
        frame.setVisible(true);
    }

    /**
     * Entering in logo
     *
     * @param e Event
     */
    public void logotipo_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.HAND_CURSOR);
    }

    /**
     * Exiting from logo
     *
     * @param e Event
     */
    public void logotipo_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    /**
     * Clicking in KEEL logo
     *
     * @param e Event
     */
    public void logotipo_mousePressed(MouseEvent e) {
        BrowserControl.displayURL("http://www.keel.es");
    }

    /**
     * Clicking in software logo
     *
     * @param e Event
     */
    public void logotipoSoft_mousePressed(MouseEvent e) {
        BrowserControl.displayURL("http://sci2s.ugr.es/");
    }
}

/**
 * Default adapter for LQD module
 */
class Frame_lqd_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    Frame_lqd_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.lqd_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.lqd_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.lqd_mouseReleased(e);
    }
}

/**
 * Default adapter for imbalanced module
 */
class Frame_imbalance_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    Frame_imbalance_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.imbalance_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.imbalance_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.imbalance_mouseReleased(e);
    }
}

/**
 * Default adapter for mil module
 */
class Frame_mil_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    Frame_mil_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.mil_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.mil_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.mil_mouseReleased(e);
    }
}

/**
 * Default adapter for statistical module
 */
class Frame_nonParametric_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    Frame_nonParametric_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.nonParametric_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.nonParametric_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.nonParametric_mouseReleased(e);
    }
}

/**
 * Default adapter for KEEL logo
 */
class FrameModules_logotipo_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    FrameModules_logotipo_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.logotipo_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.logotipo_mouseExited(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        adaptee.logotipo_mousePressed(e);
    }
}

/**
 * Default adapter for logo button
 */
class FrameModules_logotipoSoft_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    FrameModules_logotipoSoft_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.logotipo_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.logotipo_mouseExited(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        adaptee.logotipoSoft_mousePressed(e);
    }
}

/**
 * Default adapter for exit button
 */
class FrameModules_accionExit_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    FrameModules_accionExit_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.accionExit_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.accionExit_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.accionExit_mouseReleased(e);
    }
}

/**
 * Default adapter for back button
 */
class FrameModules_back_mouseAdapter extends MouseAdapter {

    private FrameModules adaptee;

    FrameModules_back_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        adaptee.back_mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        adaptee.back_mouseExited(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        adaptee.back_mouseReleased(e);
    }
}
