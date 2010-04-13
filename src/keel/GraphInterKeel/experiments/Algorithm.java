package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public final class Algorithm extends Node {

    //Julian - Now the 'par' vector is in nodo
    //so it is inherited by both Test and Algorithm classes
    //public Vector par;
    // public Algoritmo(String nombre, String path, int subtipo, Point position, GraphPanel p) {
    //  super(nombre, path, position, p.grafo.getId());
    public Algorithm() {
        super();
    }

    public Algorithm(ExternalObjectDescription dsc, Point position, GraphPanel p) {
        super(dsc, position, p.mainGraph.getId());

        /*System.out.println("Building ALGORITHM with " +
        " name = " + dsc.enumerateNames() +
        " path = " + dsc.getPath() +
        " subtype = " + dsc.getSubtype() +
        " jarname= " + dsc.getJarName()
        );*/

        actInputOutput(dsc, p);

        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_Algorithm;
        //this.subtipo = subtipo;
        if (dsc.getSubtype() == type_Preprocess) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocess.gif"));
        } else if (dsc.getSubtype() == type_Postprocess) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocess.gif"));
        } else if (dsc.getSubtype() == type_Method) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_node.gif"));
        }
        pd = p;

        // search pattern file
        par = new Vector();
        for (int i = 0; i < Layer.numLayers; i++) {
            par.addElement(new Parameters(dsc.getPath(i) + dsc.getName(i) + ".xml", false));
        }
    }

    // public Algoritmo(String nombre, String path, int subtipo, Point position, GraphPanel p,
    //                 Parametros parameters, int id) {
    //  super(nombre, path, position, id);
    public Algorithm(ExternalObjectDescription dsc, Point position, GraphPanel p,
            Vector vparameters, int id) {
        super(dsc, position, id);

        actInputOutput(dsc, p);

        type = type_Algorithm;
        // this.subtipo = subtipo;
        if (dsc.getSubtype() == type_Preprocess) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/preprocess.gif"));
        } else if (dsc.getSubtype() == type_Postprocess) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/postprocess.gif"));
        } else if (dsc.getSubtype() == type_Method) {
            image = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/method_node.gif"));
        }
        pd = p;
        par = new Vector(vparameters.size());
        for (int i = 0; i < vparameters.size(); i++) {
            par.addElement(new Parameters((Parameters) vparameters.elementAt(i)));
        // par = new Parametros(parameters);
        }
    }

    /**
     * Check the constraints defined by the node described by 'dsc' contained
     * in the graph
     * @param dsc The object describing the node
     * @param p the graph panel which contains the node
     */
    public void actInputOutput(ExternalObjectDescription dsc, GraphPanel p) {

        for (int i = 0; i < p.parent.listAlgor.length; i++) {

            //System.out.println (" algor: "+p.padre.listAlgor[i].name + " - " +  dsc.nombre[0] );
            if (p.parent.listAlgor[i].name.equalsIgnoreCase(dsc.name[0])) {
                //System.out.println (" \n\nALGORISME TROBAT");
                //System.out.println ("  > Continuous: "+p.padre.listAlgor[i].m_bInputContinuous );
                //System.out.println ("  > Integer: "+p.padre.listAlgor[i].m_bInputInteger );
                //System.out.println ("  > NOminal: "+p.padre.listAlgor[i].m_bInputNominal );
                //System.out.println ("  > Missing: "+p.padre.listAlgor[i].m_bInputMissing );


                m_bInputContinuous = p.parent.listAlgor[i].m_bInputContinuous;
                m_bInputInteger = p.parent.listAlgor[i].m_bInputInteger;
                m_bInputNominal = p.parent.listAlgor[i].m_bInputNominal;
                m_bInputMissing = p.parent.listAlgor[i].m_bInputMissing;
                m_bInputImprecise = p.parent.listAlgor[i].m_bInputImprecise;
                m_bInputMultiClass = p.parent.listAlgor[i].m_bInputMultiClass;
                m_bInputMultiOutput = p.parent.listAlgor[i].m_bInputMultiOutput;

                m_bOutputContinuous = p.parent.listAlgor[i].m_bOutputContinuous;
                m_bOutputInteger = p.parent.listAlgor[i].m_bOutputInteger;
                m_bOutputNominal = p.parent.listAlgor[i].m_bOutputNominal;
                m_bOutputMissing = p.parent.listAlgor[i].m_bOutputMissing;
                m_bOutputImprecise = p.parent.listAlgor[i].m_bOutputImprecise;
                m_bOutputMultiClass = p.parent.listAlgor[i].m_bOutputMultiClass;
                m_bOutputMultiOutput = p.parent.listAlgor[i].m_bOutputMultiOutput;

                break;
            }

        }
    }//end copyInputOutput

    Parameters getActivePair() {
        return (Parameters) par.elementAt(Layer.layerActivo);
    }

    /**
     * Shows the parameter dialog
     */
    public void showDialog() {
        dialog = new ParametersDialog(pd.parent, "Algorithm Parameters", true, par, dsc);

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

    /**
     * Draws this component
     */
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
}
