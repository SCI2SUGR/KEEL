/*@author Modified Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010 (University of Oviedo)*/
package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;


public final class Test extends Node {

    public Test() {
        super();
    }

    public Test(ExternalObjectDescription dsc, Point position, GraphPanel p) {
        super(dsc, position, p.mainGraph.getId());
        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_Test;
        // subtipo = tipo_Test;
        if (dsc.getSubtype() == type_Test) {
            image = Toolkit.getDefaultToolkit().getImage(this.getClass().
                    getResource("/keel/GraphInterKeel/resources/ico/experiments/test.gif"));
        } else if (dsc.getSubtype() == type_Visor) {
            image = Toolkit.getDefaultToolkit().getImage(this.getClass().
                    getResource("/keel/GraphInterKeel/resources/ico/experiments/visor.gif"));
        }
        pd = p;
        par = new Vector();
        for (int i = 0; i < Layer.numLayers; i++) {
            par.addElement(new Parameters(dsc.getPath(i) + dsc.getName(i) + ".xml", true));
        }
    }

    public Test(ExternalObjectDescription dsc, Point position, GraphPanel p,
            Vector vparameters, int id) {
        super(dsc, position, id);
        type = type_Test;
        // subtipo = tipo_Test;
        if (dsc.getSubtype() == type_Test) {
            image = Toolkit.getDefaultToolkit().getImage(this.getClass().
                    getResource("/keel/GraphInterKeel/resources/ico/experiments/test.gif"));
        } else if (dsc.getSubtype() == type_Visor) {
            image = Toolkit.getDefaultToolkit().getImage(this.getClass().
                    getResource("/keel/GraphInterKeel/resources/ico/experiments/visor.gif"));
        }
        pd = p;
        par = new Vector();
        for (int i = 0; i < vparameters.size(); i++) {
            par.addElement(new Parameters((Parameters) vparameters.elementAt(i)));
        }

    // par = new Parametros(parameters);
    }

    public Parameters getActivePair() {
        return (Parameters) par.elementAt(Layer.layerActivo);
    }
public void contain(String title, int show,Node n,Experiments exp) {
    }
    public void showDialog() {
//    dialogo = new ParametrosDialog(pd.padre, "Test Parameters", true, (Parametros)par.elementAt(Layer.layerActivo));

        dialog = new ParametersDialog(pd.parent, "Test Parameters", true, par, dsc);

        // Center dialog
        dialog.setSize(400, 580);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialog.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void draw(Graphics2D g2, boolean select) {
        Point pinit = new Point(centre.x - 25, centre.y - 25);
        Point pfin = new Point(centre.x + 25, centre.y + 25);
        figure = new RoundRectangle2D.Float(pinit.x, pinit.y,
                Math.abs(pfin.x - pinit.x),
                Math.abs(pfin.y - pinit.y), 20, 20);

        g2.setColor(Color.black);
        if (select) {
            Stroke s = g2.getStroke();
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
            g2.draw(figure);
            g2.setStroke(s);
        } else {
            g2.draw(figure);

        }
        g2.drawImage(image, centre.x - 25, centre.y - 25, 50, 50, pd);

        g2.setFont(new Font("Courier", Font.BOLD + Font.ITALIC, 12));
        FontMetrics metrics = g2.getFontMetrics();
        int width = metrics.stringWidth(dsc.getName());
        int height = metrics.getHeight();
        g2.drawString(dsc.getName(), centre.x - width / 2, centre.y + 40);
    }

    boolean chkNumEntradas(int nEntradas) {
        // Check that input numbers is correct
        // test class implements test that needs 2 inputs

        // For ANOVA or tables with various comparisons
        // we must inherit a new type that redefines this function

        // Changed LSR 07/02/05
        // return nEntradas==2;
        int ne = ((Parameters) (par.elementAt(0))).getNumInputs();
        if (ne == 0) {
            return true;
        }
        return nEntradas == ne;

    }
}
