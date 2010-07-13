/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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

package keel.GraphInterKeel.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent.*;
import javax.swing.*;
import keel.GraphInterKeel.experiments.*;
import keel.GraphInterKeel.help.HelpContent;
import keel.GraphInterKeel.datacf.*;
import java.awt.Rectangle;

/**
 * <p>Title: Keel</p>
 * <p>Description: Initial screen</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Universidad de Granada</p>
 * @author Jes?s Alcal? Fern?ndez
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of CÃ³rdoba) 7/07/2009
 * @author Modified by Ana Palacios Jimenez and Luciano Sanchez Ramos (Univerity of Oviedo)
 * @author Modified by Isaac Triguer VelÃ¡zquez (University of Granada) 1/6/2010
 * @version 0.0
 */

public class Frame extends JFrame {
  JPanel contentPane;
  JLabel fondo = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
    JLabel labelSalir = new JLabel();
  JLabel labelDatos = new JLabel();
  JLabel labelExperiment = new JLabel();
  JLabel labelJclec = new JLabel();
  JButton datos = new JButton();
  JButton experimentos = new JButton();
  
  //Expermiments with LQD
  JLabel labelExperimentLQD= new JLabel();
  JLabel experimental = new JLabel();

//  JLabel jclec = new JLabel();
  JButton exit = new JButton();

//  ImageIcon idatos = new ImageIcon(this.getClass().getResource("/imag/menu/datos.gif"));
//  ImageIcon idatos_select = new ImageIcon(this.getClass().getResource("/imag/menu/datos_select.gif"));
//  ImageIcon iexperiment = new ImageIcon(this.getClass().getResource("/imag/menu/experimentos.gif"));
//  ImageIcon iexperiment_select = new ImageIcon(this.getClass().getResource("/imag/menu/experimentos_select.gif"));
//  ImageIcon ijclec = new ImageIcon(this.getClass().getResource("/imag/menu/jclec.gif"));
//  ImageIcon ijclec_select = new ImageIcon(this.getClass().getResource("/imag/menu/jclec_select.gif"));
//  ImageIcon iexit = new ImageIcon(this.getClass().getResource("/imag/menu/exit.gif"));
//  ImageIcon iexit_select = new ImageIcon(this.getClass().getResource("/imag/menu/exit_select.gif"));
  public String raiz = "../Datasets/";
    JLabel keel = new JLabel();
    JLabel logotipo = new JLabel();
    JLabel logotipoSoft = new JLabel();
    JLabel barraExit = new JLabel();
    JLabel accionExit = new JLabel();
    JLabel dataManagement = new JLabel();
    JLabel experiments = new JLabel();
    JLabel help = new JLabel();
    JLabel teaching = new JLabel();
    JLabel labelTeaching = new JLabel();
    JLabel labelhelp = new JLabel();
    JLabel labelModules = new JLabel();
    //Expermiments with LQD
    JLabel experimentsLQD = new JLabel();

    JLabel modules = new JLabel();

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/

    //0->Button Experiments is pressed, 1->Button Teaching is pressed
    public static int buttonPressed = 0;

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/

    public Frame() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    // frame initialization
    this.setFont(new java.awt.Font("Arial", 0, 11));
    this.setIconImage(Toolkit.getDefaultToolkit().getImage(
        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
    this.setSize(new Dimension(640, 480));
    this.setTitle("Keel");
    this.setResizable(false);

    this.
    // Create panel
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(null);

    // panel background
    fondo.setText("");
    fondo.setBounds(new Rectangle(0, 0, 640, 480));
    fondo.setFont(new java.awt.Font("Arial", 0, 11));
    fondo.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/fondo.gif"))); 


  
    
    exit.setText("Exit KEEL");
    exit.setBounds(new Rectangle(294, 327, 129, 36));
    exit.setFont(new java.awt.Font("Arial", 0, 11));
    exit.addMouseListener(new Inicio_exit_mouseAdapter(this));
    
//    exit.setDebugGraphicsOptions(0);
 //   exit.setIcon(iexit);

  // LSR Button "Extra" temporarily commented out
 //   this.experimental.setText("Extra");
 //   experimental.setBounds(new Rectangle(440, 412, 70, 27));
 //   experimental.setFont(new java.awt.Font("Arial", 0, 16));
 //   experimental.setForeground(Color.GREEN);
 //    experimental.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/browser.gif")));
 //   experimental.addMouseListener(new Inicio_experimental_mouseAdapter(this));
    
    // labels associated to buttons
    labelSalir.setFont(new java.awt.Font("Arial", 1, 18));
    labelSalir.setForeground(Color.white);
    labelSalir.setText("Exit KEEL");
    labelSalir.setBounds(new Rectangle(40, 412, 595, 27));
    labelSalir.setVisible(false);

    labelDatos.setFont(new java.awt.Font("Arial", 1, 18));
    labelDatos.setForeground(Color.white);
    labelDatos.setText("Management of Datasets");
    labelDatos.setBounds(new Rectangle(40, 412, 465, 27));
    labelDatos.setVisible(false);

    labelModules.setFont(new java.awt.Font("Arial", 1, 18));
    labelModules.setForeground(Color.white);
    labelModules.setText("Modules");
    labelModules.setBounds(new Rectangle(40, 412, 465, 27));
    labelModules.setVisible(false);
    
    
    labelExperiment.setFont(new java.awt.Font("Arial", 1, 18));
    labelExperiment.setForeground(Color.white);
    labelExperiment.setText("Experiments Design with available models");
    labelExperiment.setBounds(new Rectangle(40, 412, 465, 27));
    labelExperiment.setVisible(false);
    
    labelExperimentLQD.setFont(new java.awt.Font("Arial", 1, 18));
    labelExperimentLQD.setForeground(Color.white);
    labelExperimentLQD.setText("Experiments Design with Low Quality Data");
    labelExperimentLQD.setBounds(new Rectangle(40, 412, 465, 27));
    labelExperimentLQD.setVisible(false);

    labelTeaching.setFont(new java.awt.Font("Arial", 1, 18));
    labelTeaching.setForeground(Color.white);
    labelTeaching.setText("Educational version of Experiments Design");
    labelTeaching.setBounds(new Rectangle(40, 412, 465, 27));
    labelTeaching.setVisible(false);

    labelhelp.setFont(new java.awt.Font("Arial", 1, 18));
    labelhelp.setForeground(Color.white);
    labelhelp.setText("KEEL Tool 2.0 Description");
    labelhelp.setBounds(new Rectangle(40, 412, 465, 27));
    labelhelp.setVisible(false);


/*    labelJclec.setFont(new java.awt.Font("Dialog", 1, 15));
    labelJclec.setForeground(Color.white);
    labelJclec.setText("Algorithms development with JCLEC library");
    labelJclec.setBounds(new Rectangle(190, 395, 465, 17));
    labelJclec.setVisible(false);*/

    contentPane.setFont(new java.awt.Font("Arial", 0, 11));
    labelJclec.setFont(new java.awt.Font("Arial", 0, 11));
    keel.setText("");
    keel.setBounds(new Rectangle(160, 13, 336, 33));
    keel.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/keel.png")));
    logotipo.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/logotipo.png")));
        logotipo.setBounds(new Rectangle(550, 13, 65, 43));
        logotipo.addMouseListener(new Frame_logotipo_mouseAdapter(this));
        
        logotipoSoft.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/Software.png")));
        logotipoSoft.setBounds(new Rectangle(30, 13, 75, 43));
        logotipoSoft.addMouseListener(new Frame_logotipoSoft_mouseAdapter(this));
        
        
        barraExit.setBounds(new Rectangle(0, 401, 751, 50));
    barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit.png")));
        accionExit.setText("jLabel2");
        accionExit.setBounds(new Rectangle(536, 409, 94, 38));
        accionExit.addMouseListener(new Frame_accionExit_mouseAdapter(this));
        dataManagement.setBounds(new Rectangle(50, 153, 232, 40));
        dataManagement.addMouseListener(new Frame_dataManagement_mouseAdapter(this));
        experiments.setBounds(new Rectangle(50, 216, 171, 38));
        experiments.addMouseListener(new Frame_experiments_mouseAdapter(this));
        
        modules.setBounds(new Rectangle(350, 153, 155, 38)); 
        modules.addMouseListener(new Frame_modules_mouseAdapter(this));
        
        experimentsLQD.setBounds(new Rectangle(350, 216, 250, 38)); 
        experimentsLQD.setVisible(false);
        experimentsLQD.addMouseListener(new Frame_experimentsLQD_mouseAdapter(this));
        teaching.setBounds(new Rectangle(48, 274, 175, 42));
        teaching.addMouseListener(new Frame_teaching_mouseAdapter(this));
        help.setBounds(new Rectangle(48, 332, 99, 39));
        help.addMouseListener(new Frame_help_mouseAdapter(this));
        experiments.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experiments.png")));
        experimentsLQD.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experimentsLQD.gif")));
    dataManagement.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/dataManagement.png")));
    teaching.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/educational.png")));
    help.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/help.png")));
    modules.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/modules.png")));
    
    

    contentPane.add(labelSalir, null);
    contentPane.add(labelDatos, null);
    contentPane.add(labelExperiment, null);
    contentPane.add(labelExperimentLQD, null);
    contentPane.add(experimental);
      contentPane.add(modules);
    contentPane.add(labelTeaching);
    contentPane.add(labelhelp);
    contentPane.add(labelModules,null);
//    contentPane.add(labelJclec, null);
        contentPane.add(keel);
    contentPane.add(logotipo);
     contentPane.add(logotipoSoft);
    contentPane.add(barraExit);
    contentPane.add(dataManagement);
    contentPane.add(experiments);
    contentPane.add(experimentsLQD);
    contentPane.add(teaching);
    contentPane.add(help);
  
    contentPane.add(fondo, null);
    contentPane.add(accionExit);
    }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  // data button
  void datos_mouseEntered(MouseEvent e) {
//    datos.setIcon(idatos_select);
  }

  void datos_mouseExited(MouseEvent e) {
//    datos.setIcon(idatos);
    labelDatos.setVisible(false);
    datos.setBounds(new Rectangle(15, 130, 129, 36));
  }

  void datos_mousePressed(MouseEvent e) {
    datos.setBounds(new Rectangle(16, 131, 129, 36));
  }

  void datos_mouseReleased(MouseEvent e) {
    datos.setBounds(new Rectangle(15, 130, 129, 36));
  }

  // experiment button
  void experimentos_mouseEntered(MouseEvent e) {
//    experimentos.setIcon(iexperiment_select);
  }

  void experimentos_mouseExited(MouseEvent e) {
//    experimentos.setIcon(iexperiment);
    labelExperiment.setVisible(false);
    experimentos.setBounds(new Rectangle(15, 200, 129, 36));
  }

  void experimentos_mousePressed(MouseEvent e) {
    experimentos.setBounds(new Rectangle(16, 201, 129, 36));
  }

  void experimentos_mouseReleased(MouseEvent e) {
    experimentos.setBounds(new Rectangle(15, 200, 129, 36));
  }
  
  
  
    // experiment button
  void modulesI_mouseEntered(MouseEvent e) {
//    experimentos.setIcon(iexperiment_select);
  }

  void modulesI_mouseExited(MouseEvent e) {
//    experimentos.setIcon(iexperiment);
    labelModules.setVisible(false);
    modules.setBounds(new Rectangle(350, 153, 129, 36));
  }

  void modulesI_mousePressed(MouseEvent e) {
    modules.setBounds(new Rectangle(351, 153, 129, 36));
  }

  void modulesI_mouseReleased(MouseEvent e) {
    modules.setBounds(new Rectangle(350, 153, 129, 36));
  }
  
  
  // Jclec button
/*  void jclec_mouseEntered(MouseEvent e) {
    jclec.setIcon(ijclec_select);
    labelJclec.setVisible(true);
  }

  void jclec_mouseExited(MouseEvent e) {
    jclec.setIcon(ijclec);
    labelJclec.setVisible(false);
    jclec.setBounds(new Rectangle(15, 270, 129, 36));
  }

  void jclec_mousePressed(MouseEvent e) {
    jclec.setBounds(new Rectangle(16, 271, 129, 36));
  }

  void jclec_mouseReleased(MouseEvent e) {
    jclec.setBounds(new Rectangle(15, 270, 129, 36));
  }*/

  // Exit button
  void exit_mouseEntered(MouseEvent e) {
//    exit.setIcon(iexit_select);
    labelSalir.setVisible(true);
  }

  void exit_mouseExited(MouseEvent e) {
  //  exit.setIcon(iexit);
    labelSalir.setVisible(false);
    exit.setBounds(new Rectangle(15, 340, 129, 36));
  }

  void exit_mousePressed(MouseEvent e) {
    exit.setBounds(new Rectangle(16, 341, 129, 36));
  }
  
  void experimental_mousePressed(MouseEvent e) {
        experimentsLQD.setVisible(true);
      
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

    public void accionExit_mouseEntered(MouseEvent e) {
        barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit2.png")));
        labelSalir.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void accionExit_mouseExited(MouseEvent e) {
        barraExit.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/exit.png")));
        labelSalir.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void accionExit_mouseReleased(MouseEvent e) {
        System.exit(0);
    }

    public void dataManagement_mouseEntered(MouseEvent e) {
        dataManagement.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/dataManagement2.png")));
        labelDatos.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void dataManagement_mouseExited(MouseEvent e) {
        dataManagement.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/dataManagement.png")));
        labelDatos.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void help_mouseEntered(MouseEvent e) {
        help.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/help2.png")));
        labelhelp.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void help_mouseExited(MouseEvent e) {
        help.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/help.png")));
        labelhelp.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void experiments_mouseEntered(MouseEvent e) {
        experiments.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experiments2.png")));
        labelExperiment.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }
    
    public void experiments_mouseExited(MouseEvent e) {
        experiments.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experiments.png")));
        labelExperiment.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }
    
    public void experimentsLQD_mouseEntered(MouseEvent e) {
        experimentsLQD.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experimentsLQD2.gif")));
        labelExperimentLQD.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }
         
    public void experimentsLQD_mouseExited(MouseEvent e) {
        experimentsLQD.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/experimentsLQD.gif")));
        labelExperimentLQD.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }
    
    public void modules_mouseEntered(MouseEvent e) {
        modules.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/modules2.png")));
        labelModules.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }
         
    public void modules_mouseExited(MouseEvent e) {
        modules.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/modules.png")));
        labelModules.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }
    

    public void teaching_mouseEntered(MouseEvent e) {
        teaching.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/educational2.png")));
        labelTeaching.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void teaching_mouseExited(MouseEvent e) {
        teaching.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/educational.png")));
        labelTeaching.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void dataManagement_mouseReleased(MouseEvent e) {
        /*Inicio frame = new Inicio(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
        this.setVisible(false);
        this.setEnabled(false);*/
        
    	DataCFFrame frame = new DataCFFrame();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);
        
    	frame.setParent(this);
    	this.setVisible(false);
    	frame.setVisible(true);
    }


    public void help_mouseReleased(MouseEvent e) {
        JFrame frame = new JFrame();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //frame.setPreferredSize(new Dimension(frameSize.height/2,frameSize.width/2));

        frame.setSize(screenSize.width/2, screenSize.height/2);
		frame.setPreferredSize(new java.awt.Dimension(364,305));
        frame.setLocation( (screenSize.width - frame.getSize().width),
                         (screenSize.height - frame.getSize().height) );
        HelpContent desc = new HelpContent();
        desc.muestraURL(this
    			.getClass().getResource("/help/help_intro.html"));
        frame.add(desc);
        frame.setVisible(true);
        //this.setVisible(false);
    }

    /***************************************************************
	***************  EDUCATIONAL KEEL  ****************************
	**************************************************************/
    public void experiments_mouseReleased(MouseEvent e) {
    	buttonPressed = 0;
        Experiments frame = new Experiments(this,Experiments.INVESTIGATION);
       // frame.objType = Experiments.INVESTIGATION;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);

        frame.activateUpperMenu_principals();

    }
    
    public void modules_mouseReleased(MouseEvent e) {
    	buttonPressed = 0;
         
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        FrameModules frame= new FrameModules();
        
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
        
        
      	frame.setParent(this);
    	this.setVisible(false);
    	frame.setVisible(true);
     
    }
        
    public void experimentsLQD_mouseReleased(MouseEvent e) {
    	buttonPressed = 0;
        experimentsLQD.setVisible(false);
        Experiments frame = new Experiments(this,Experiments.LQD); //LUEGO SERA LA MIA
       // frame.objType = Experiments.INVESTIGATIONLQD;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);

    }
    /***************************************************************
	***************  EDUCATIONAL KEEL  ****************************
	**************************************************************/

    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/
    public void teaching_mouseReleased(MouseEvent e)
    {

    	 buttonPressed = 1;
    	 Experiments frame = new Experiments(this,Experiments.TEACHING);
         //frame.objType = Experiments.TEACHING;
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         Dimension frameSize = frame.getSize();
         if (frameSize.height > screenSize.height) {
           frameSize.height = screenSize.height;
         }
         if (frameSize.width > screenSize.width) {
           frameSize.width = screenSize.width;
         }
         frame.setLocation( (screenSize.width - frameSize.width) / 2,
                            (screenSize.height - frameSize.height) / 2);
         this.setVisible(false);
    }
    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/

    public void logotipo_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void logotipo_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void logotipo_mousePressed(MouseEvent e) {
        BrowserControl.displayURL("http://www.keel.es");
    }
    
     public void logotipoSoft_mousePressed(MouseEvent e) {
        BrowserControl.displayURL("http://sci2s.ugr.es/");
    }

}


class Frame_dataManagement_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_dataManagement_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.dataManagement_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.dataManagement_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.dataManagement_mouseReleased(e);
    }
}


class Frame_help_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_help_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.help_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.help_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.help_mouseReleased(e);
    }
}


class Frame_experiments_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_experiments_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.experiments_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.experiments_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.experiments_mouseReleased(e);
    }
}


class Frame_modules_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_modules_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.modules_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.modules_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.modules_mouseReleased(e);
    }
}


class Frame_experimentsLQD_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_experimentsLQD_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.experimentsLQD_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.experimentsLQD_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.experimentsLQD_mouseReleased(e);
    }
}

class Frame_logotipo_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_logotipo_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.logotipo_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.logotipo_mouseExited(e);
    }

    public void mousePressed(MouseEvent e) {
        adaptee.logotipo_mousePressed(e);
    }

}

class Frame_logotipoSoft_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_logotipoSoft_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.logotipo_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.logotipo_mouseExited(e);
    }

    public void mousePressed(MouseEvent e) {
        adaptee.logotipoSoft_mousePressed(e);
    }

}


        

class Frame_teaching_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_teaching_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.teaching_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.teaching_mouseExited(e);
    }

    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/
    public void mouseReleased(MouseEvent e) {
        adaptee.teaching_mouseReleased(e);
    }
    /***************************************************************
     ***************  EDUCATIONAL KEEL *****************************
     **************************************************************/
}


class Frame_accionExit_mouseAdapter extends MouseAdapter {
    private Frame adaptee;
    Frame_accionExit_mouseAdapter(Frame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.accionExit_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.accionExit_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.accionExit_mouseReleased(e);
    }
}


class Inicio_exit_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_exit_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.exit_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.exit_mouseExited(e);
  }

  public void mousePressed(MouseEvent e) {
    adaptee.exit_mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.exit_mouseReleased(e);
  }
}

class Inicio_experimental_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_experimental_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mousePressed(MouseEvent e) {
    adaptee.experimental_mousePressed(e);
  }
   public void mouseEntered(MouseEvent e) {
    adaptee.experimental_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.experimental_mouseExited(e);
  }
 
}

class Inicio_modules_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_modules_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.modulesI_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.modulesI_mouseExited(e);
  }

 
  public void mousePressed(MouseEvent e) {
    adaptee.modulesI_mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.modulesI_mouseReleased(e);
  }
}


class Inicio_datos_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_datos_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.datos_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.datos_mouseExited(e);
  }

  public void mousePressed(MouseEvent e) {
    adaptee.datos_mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.datos_mouseReleased(e);
  }
}

class Inicio_experimentos_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_experimentos_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.experimentos_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.experimentos_mouseExited(e);
  }

  public void mousePressed(MouseEvent e) {
    adaptee.experimentos_mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.experimentos_mouseReleased(e);
  }
}


/*
class Inicio_jclec_mouseAdapter
    extends java.awt.event.MouseAdapter {
  Frame adaptee;

  Inicio_jclec_mouseAdapter(Frame adaptee) {
    this.adaptee = adaptee;
  }

  public void mouseEntered(MouseEvent e) {
    adaptee.jclec_mouseEntered(e);
  }

  public void mouseExited(MouseEvent e) {
    adaptee.jclec_mouseExited(e);
  }

  public void mousePressed(MouseEvent e) {
    adaptee.jclec_mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    adaptee.jclec_mouseReleased(e);
  }
}*/

