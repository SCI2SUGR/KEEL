package keel.GraphInterKeel.menu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JComponent.*;
import javax.swing.*;
import keel.GraphInterKeel.experiments.Experiments;
import keel.GraphInterKeel.experiments.*;
import keel.GraphInterKeel.help.HelpContent;
import keel.GraphInterKeel.datacf.*;
import java.awt.Rectangle;
import keel.GraphInterKeel.statistical.StatisticalF;


/**
 * <p>Title: Keel</p>
 * <p>Description: Modules screen</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: Universidad de Granada</p>
 * @author Isaac Triguero Velázquez
 * @version 0.0
 */

public class FrameModules extends JFrame {
  JPanel contentPane;
  JLabel fondo = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel labelSalir = new JLabel();
  
  JLabel labelLQD= new JLabel();
   JLabel labelLQD2= new JLabel();
  JLabel labelImbalance = new JLabel();
  JLabel labelNonParametric = new JLabel();
  JLabel labelMil = new JLabel();
  JLabel labelBack = new JLabel();
  
  JLabel lqd = new JLabel();
  JLabel imbalance = new JLabel();
  JLabel nonParametric = new JLabel();
  JLabel mil = new JLabel();
  JLabel back = new JLabel();
  
  
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
    


    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/

    //0->Button Experiments is pressed, 1->Button Teaching is pressed
    public static int buttonPressed = 0;

    /***************************************************************
     ***************  EDUCATIONAL KEEL  ***************************
     **************************************************************/

  public FrameModules() {
    try {
      jbInit();
    }
    catch (Exception e) {
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
        

    
  private void jbInit() throws Exception {
    // frame initialization
    this.setFont(new java.awt.Font("Arial", 0, 11));
    this.setIconImage(Toolkit.getDefaultToolkit().getImage(
        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/logo/logo.gif")));
    this.setSize(new Dimension(640, 480));
    this.setTitle("Keel");
    this.setResizable(false);

    // Create panel
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(null);

    // panel background
    fondo.setText("");
    fondo.setBounds(new Rectangle(0, 0, 640, 480));
    fondo.setFont(new java.awt.Font("Arial", 0, 11));
    fondo.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/fondo.gif"))); 
    
    
    // menu buttons
   /*
    lqd.setText("Low Quality Data");
    lqd.setBounds(new Rectangle(252, 230, 129, 36));
    lqd.setFont(new java.awt.Font("Arial", 0, 11));
    lqd.addMouseListener(new Inicio_lqd_mouseAdapter(this));
            
    
    imbalance.setText("Imbalanced Learning");
    imbalance.setBounds(new Rectangle(200, 230, 129, 36));
    imbalance.setFont(new java.awt.Font("Arial", 0, 11));
    imbalance.addMouseListener(new Inicio_imbalance_mouseAdapter(this));
    
    nonParametric.setText("Non-Parametric Statistical Analysis");
    nonParametric.setBounds(new Rectangle(310, 148, 129, 36));
    nonParametric.setFont(new java.awt.Font("Arial", 0, 11));
    nonParametric.addMouseListener(new Inicio_nonParametric_mouseAdapter(this));
*/

    exit.setText("Exit KEEL");
    exit.setBounds(new Rectangle(294, 327, 129, 36));
    exit.setFont(new java.awt.Font("Arial", 0, 11));
    //exit.addMouseListener(new InicioModules_exit_mouseAdapter(this));

    
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
   
    contentPane.add(back,null);
    contentPane.add(labelBack,null);
        
    contentPane.add(labelSalir, null);
    contentPane.add(labelLQD2, null);
    contentPane.add(labelLQD, null);
    contentPane.add(labelImbalance, null);
    contentPane.add(labelNonParametric, null);
	contentPane.add(labelMil, null);
    contentPane.add(lqd);
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

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }


  

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
    
    
    public void back_mouseEntered(MouseEvent e) {
        back.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/back2.png")));
        labelBack.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void back_mouseExited(MouseEvent e) {
        back.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/back.png")));
        labelBack.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void back_mouseReleased(MouseEvent e) {
        this.parent.setVisible(true);
        this.setVisible(false);
    }
    

    public void lqd_mouseEntered(MouseEvent e) {
        lqd.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/lqd2.png")));
        labelLQD.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void lqd_mouseExited(MouseEvent e) {
        lqd.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/lqd.png")));
        labelLQD.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void imbalance_mouseEntered(MouseEvent e) {
        imbalance.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/imbalance2.png")));
        labelImbalance.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }

    public void imbalance_mouseExited(MouseEvent e) {
        imbalance.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/imbalance.png")));
        labelImbalance.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void nonParametric_mouseEntered(MouseEvent e) {
        nonParametric.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/nonParametric2.png")));
        labelNonParametric.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }
    
    public void nonParametric_mouseExited(MouseEvent e) {
        nonParametric.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/nonParametric.png")));
        labelNonParametric.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }
        
   
	public void mil_mouseEntered(MouseEvent e) {
       mil.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mil2.png")));
        labelMil.setVisible(true);
        this.setCursor(Cursor.HAND_CURSOR);
    }
    
    public void mil_mouseExited(MouseEvent e) {
        mil.setIcon(new ImageIcon(this.getClass().getResource("/keel/GraphInterKeel/resources/imag/menu/mil.png")));
        labelMil.setVisible(false);
        this.setCursor(Cursor.DEFAULT_CURSOR);
    }
    public void lqd_mouseReleased(MouseEvent e) {
        
    	buttonPressed = 0;
		
        Experiments frame = new Experiments(parent,Experiments.LQD);

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
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                           (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);
    }

	public void mil_mouseReleased(MouseEvent e) {

        buttonPressed = 0;
        Experiments frame = new Experiments(parent,Experiments.MULTIINSTANCE);
       // frame.objType = Experiments.IMBALANCED;
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
    
    public void imbalance_mouseReleased(MouseEvent e) {
		
		buttonPressed = 0;
        Experiments frame = new Experiments(parent,Experiments.IMBALANCED);
       // frame.objType = Experiments.IMBALANCED;
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


    public void nonParametric_mouseReleased(MouseEvent e) {
        StatisticalF frame= new StatisticalF();
        
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


class Frame_lqd_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    Frame_lqd_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.lqd_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.lqd_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.lqd_mouseReleased(e);
    }
}


class Frame_imbalance_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    Frame_imbalance_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.imbalance_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.imbalance_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.imbalance_mouseReleased(e);
    }
}

class Frame_mil_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    Frame_mil_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.mil_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.mil_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.mil_mouseReleased(e);
    }
}

class Frame_nonParametric_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    Frame_nonParametric_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.nonParametric_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.nonParametric_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.nonParametric_mouseReleased(e);
    }
}




class FrameModules_logotipo_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    FrameModules_logotipo_mouseAdapter(FrameModules adaptee) {
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


class FrameModules_logotipoSoft_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    FrameModules_logotipoSoft_mouseAdapter(FrameModules adaptee) {
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




class FrameModules_accionExit_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    FrameModules_accionExit_mouseAdapter(FrameModules adaptee) {
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

class FrameModules_back_mouseAdapter extends MouseAdapter {
    private FrameModules adaptee;
    FrameModules_back_mouseAdapter(FrameModules adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.back_mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        adaptee.back_mouseExited(e);
    }

    public void mouseReleased(MouseEvent e) {
        adaptee.back_mouseReleased(e);
    }
}


