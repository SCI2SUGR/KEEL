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
 * <p>
 * @author Modified by Julian Luengo Martin (modifications 19/04/2009)
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @author Modified Ana Palacios Jimenez and Luciano Sanchez Ramos 23-4-2010( University of Oviedo)
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.event.*;
import keel.GraphInterKeel.menu.Frame;

public class GraphPanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Scrollable {

    Graph mainGraph;
    boolean elementSelected;
    int typeSelected;
    public Experiments parent;
    private boolean paintingLine = false;
    private boolean paintingBox = false;
    private Point last = new Point();
    private Point originP = new Point();
    private Point destinationP = new Point();
    private int maxUnitIncrement = 10;
    public boolean paintGrid = false;
    protected boolean multipleSelection = false;
    protected Vector selectedN = new Vector();
    private final int NODE = 0;
    static final int NODELQD = 10;
    static final int NODELQD_c = 11;
    static final int NODEC_LQD = 12;
    static final int NODEC = 13;
    private final int ARC = 1;
    static final int SELECTING = 2;
    static final int PAINT_ARC = 3;
    static final int PAINT_DATASET = 4;
    static final int PAINT_ALGORITHM = 5;
    static final int PAINT_JCLEC = 6;
    static final int PAINT_USER = 7;
    static final int PAINT_TEST = 8;
    static final int PAINT_MULTIPLEXOR = 9;
    public int iden_node = 0;
    // static int Secundary= 0;
    private BufferedImage texture;
    //Graphics2D g2;
    public int node_selected;
    public int arc_selected;
    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    private int caseG = 2;
    private Image backgroundImage;

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/

    /**
     * Builder
     * @param f Parent frame
     * @param g Graph
     */
    public GraphPanel(Experiments f, Graph g) {
        parent = f;
        mainGraph = g;
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.addKeyListener(this);
        elementSelected = false;
        try {
            texture = ImageIO.read(this.getClass().getResource(
                    "/keel/GraphInterKeel/resources/ico/experiments/cuadricula.gif"));
        } catch (Exception e) {
        }

    }

    /**
     * Draw graph
     * @param g Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        if (mainGraph != null) {
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ***************************
             **************************************************************/
            if (Frame.buttonPressed == 1) //Button Teaching pressed
            {
                backgroundImage = Toolkit.getDefaultToolkit().getImage(
                        this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/fondoPanelDibujo.jpg"));

                if (backgroundImage != null) //Button Experiments pressed
                {
                    g2.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ***************************
             **************************************************************/
            // Antialias
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (paintGrid) {
                // Draw grid
                try {
                    Rectangle r = new Rectangle(0, 0, 15, 15);
                    Paint p = g2.getPaint();
                    g2.setPaint(new TexturePaint(texture, r));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setPaint(p);
                } catch (Exception exc) {
                }
            }

            // draw elastic line
            if (paintingLine) {
                Stroke s = g2.getStroke();
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
                g2.setColor(Color.darkGray);
                g2.drawLine(originP.x, originP.y, destinationP.x, destinationP.y);
                g2.setStroke(s);
            }

            // draw elastic rectangle
            if (paintingBox) {
                Stroke s = g2.getStroke();
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
                g2.setColor(Color.darkGray);
                if (originP.x > destinationP.x && originP.y > destinationP.y) {
                    g2.drawRect(destinationP.x, destinationP.y, originP.x - destinationP.x,
                            originP.y - destinationP.y);
                } else if (originP.x > destinationP.x) {
                    g2.drawRect(destinationP.x, originP.y, originP.x - destinationP.x,
                            destinationP.y - originP.y);
                } else if (originP.y > destinationP.y) {
                    g2.drawRect(originP.x, destinationP.y, destinationP.x - originP.x,
                            originP.y - destinationP.y);
                } else {
                    g2.drawRect(originP.x, originP.y, destinationP.x - originP.x,
                            destinationP.y - originP.y);
                }
                g2.setStroke(s);
            }

            // draw connections
            // System.out.println("el numero de arcos es aquiii en pintar "+mainGraph.numArcs());
            for (int i = 0; i < mainGraph.numArcs(); i++) {
                Arc a = mainGraph.getArcAt(i);
                Point nodo_origen = null;
                Point nodo_destino = null;
                if (parent.objType != parent.LQD) {
                    nodo_origen = mainGraph.getNodeAt(a.getSource()).getPosicion();
                    nodo_destino = mainGraph.getNodeAt(a.getDestination()).getPosicion();
                }
                if (parent.objType == parent.LQD) {
                    //System.out.println("dibujo el arco "+i+" que su nodo origen id es "+a.getSource());
                    //we found de node
                    for (int n = 0; n < mainGraph.numNodes(); n++) {

                        if (mainGraph.getNodeAt(n).id == a.getSource()) {
                            nodo_origen = mainGraph.getNodeAt(n).getPosicion();
                        //     System.out.println("el nodo origen es "+mainGraph.getNodeAt(n).dsc.getName(0));
                        }
                        if (mainGraph.getNodeAt(n).id == a.getDestination()) {
                            nodo_destino = mainGraph.getNodeAt(n).getPosicion();
                        //   System.out.println("el nodo destino es "+mainGraph.getNodeAt(n).dsc.getName(0));
                        }
                    }
                }

                if (!multipleSelection) {
                    if (i == mainGraph.numArcs() - 1 && elementSelected && typeSelected == ARC) {
                        a.draw(g2, nodo_origen, nodo_destino, true);
                    } else {
                        a.draw(g2, nodo_origen, nodo_destino, false);
                    }
                } else {
                    if (selectedN.contains(new Integer(a.getSource())) ||
                            selectedN.contains(new Integer(a.getDestination()))) {
                        a.draw(g2, nodo_origen, nodo_destino, true);
                    } else {
                        a.draw(g2, nodo_origen, nodo_destino, false);
                    }
                }
            }

            // draw nodes
            for (int i = 0; i < mainGraph.numNodes(); i++) {


                if (!multipleSelection) {
                    if (i == mainGraph.numNodes() - 1 && elementSelected && typeSelected == NODE) {
                        mainGraph.getNodeAt(i).draw(g2, true);
                    } else {
                        mainGraph.getNodeAt(i).draw(g2, false);
                    }
                } else {
                    if (selectedN.contains(new Integer(i))) {
                        mainGraph.getNodeAt(i).draw(g2, true);
                    } else {
                        mainGraph.getNodeAt(i).draw(g2, false);
                    }
                }
            }
        }
    }

    /**
     * Management of mouse events
     * @param e Event
     */
    public void mousePressed(MouseEvent e) {

        UseCase casoUso = null;
        Node nodo;

        boolean aux = multipleSelection;
        elementSelected = false;
        parent.deleteItem.setEnabled(false);
        multipleSelection = false;

        // don't quit multiple selection
        if (parent.cursorAction == SELECTING && e.getButton() == e.BUTTON1 && aux) {
            for (int i = 0; i < selectedN.size(); i++) {
                Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                if (n.isInside(e.getPoint())) {
                    last.x = e.getX();
                    last.y = e.getY();
                    parent.deleteItem.setEnabled(true);
                    multipleSelection = true;
                    break;
                }
            }
        }

        // select (node or connection)
        if ((parent.cursorAction == SELECTING && !multipleSelection) ||
                e.getButton() == e.BUTTON3) {

            for (int i = mainGraph.numNodes() - 1; (i >= 0) && (!elementSelected); i--) {
                Node n = mainGraph.getNodeAt(i);
                if (n.isInside(e.getPoint())) {
                    last.x = n.getPosicion().x - e.getX();
                    last.y = n.getPosicion().y - e.getY();
                    if (parent.objType == parent.LQD) {
                        mainGraph.dropNodeLQD_move(i);
                    } else {
                        mainGraph.dropNode(i);
                    }
                    mainGraph.insertNode(n);

                    elementSelected = true;
                    typeSelected = NODE;
                    node_selected = mainGraph.numNodes() - 1;

                    nodo = n;

                    if (n.getType() != n.type_Dataset) {
                        parent.deleteItem.setEnabled(true);
                    }

                    if (parent.objType != parent.LQD) {
                        for (int j = 0; j < mainGraph.numArcs(); j++) {
                            Arc a = mainGraph.getArcAt(j);
                            int index_origen = a.getSource();
                            int index_destino = a.getDestination();

                            if (index_origen == i) {
                                a.setSource(mainGraph.numNodes() - 1);
                            } else if (index_origen > i) {
                                a.setSource(index_origen - 1);
                            }

                            if (index_destino == i) {
                                a.setDestination(mainGraph.numNodes() - 1);
                            } else if (index_destino > i) {
                                a.setDestination(index_destino - 1);
                            }
                        }
                    }
                    //Print the user case for the selected node
                    if (nodo.getType() != Node.type_Dataset) {
                        casoUso = parent.readXMLUseCase("./help/" + nodo.dsc.getName());
                        if (casoUso == null) {
                            parent.useCaseTextArea.setText("Use Case not found for this method");
                        } else {
                            parent.useCaseTextArea.setText(casoUso.toString());
                            parent.useCaseTextArea.setCaretPosition(0);
                        }
                    } else {
                        parent.useCaseTextArea.setText("No use case available");
                    }

                }
            }
            if (!elementSelected) {
                for (int i = mainGraph.numArcs() - 1; (i >= 0) && (!elementSelected); i--) {
                    Arc a = mainGraph.getArcAt(i);
                    Point origen = null;
                    Point destino = null;
                    if (parent.objType != parent.LQD) {
                        origen = mainGraph.getNodeAt(a.getSource()).getPosicion();
                        destino = mainGraph.getNodeAt(a.getDestination()).getPosicion();
                    } else {
                        // System.out.println("dibujo el arco "+i+" que su nodo origen id es "+a.getSource());
                        //we found de node
                        for (int n = 0; n < mainGraph.numNodes(); n++) {
                            if (mainGraph.getNodeAt(n).id == a.getSource()) {
                                origen = mainGraph.getNodeAt(n).getPosicion();
                            }
                            if (mainGraph.getNodeAt(n).id == a.getDestination()) {
                                destino = mainGraph.getNodeAt(n).getPosicion();
                            }
                        }
                    }
                    if (a.isInside(e.getPoint(), origen, destino)) {
                        mainGraph.dropAndInsertArc(i, a);
                        elementSelected = true;
                        typeSelected = ARC;
                        arc_selected = i;
                        parent.deleteItem.setEnabled(true);
                    }
                }
                if (!elementSelected && e.getButton() == e.BUTTON1) {
                    originP = e.getPoint();
                    destinationP = e.getPoint();
                    paintingBox = true;
                }
            } else {
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            repaint();
        } // draw connection
        else if (parent.cursorAction == PAINT_ARC && e.getButton() == e.BUTTON1 &&
                !multipleSelection) {
            boolean para = false;
            for (int i = mainGraph.numNodes() - 1; (i >= 0) && (!para); i--) {
                Node n = mainGraph.getNodeAt(i);
                if (n.isInside(e.getPoint())) {
                    originP = n.getPosicion();
                    destinationP = n.getPosicion();
                    paintingLine = true;
                    para = true;
                }
            }
        }

    }

    /**
     * Dragging mouse
     * @param e Event
     */
    public void mouseDragged(MouseEvent e) {
        switch (parent.cursorAction) {
            // move node
            case SELECTING:
                if (multipleSelection) {
                    if (e.getX() > 0 && e.getX() < getWidth() && e.getY() > 0 &&
                            e.getY() < getHeight()) {
                        Point desplaza = new Point(e.getX() - last.x,
                                e.getY() - last.y);
                        last.x = e.getX();
                        last.y = e.getY();
                        for (int i = 0; i < selectedN.size(); i++) {
                            Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                            Point nuevo = new Point(n.getPosicion());
                            nuevo.x += desplaza.x;
                            nuevo.y += desplaza.y;
                            n.setPosicion(nuevo);
                        }
                        repaint();
                    }
                } else if (elementSelected && typeSelected == NODE) {
                    updatePosition(e);
                } else if (paintingBox) {
                    int tmp;
                    destinationP = e.getPoint();

                    repaint();
                }
                break;
            // draw conection
            case PAINT_ARC:
                if (paintingLine) {
                    boolean para = false;
                    destinationP = e.getPoint();
                    for (int i = mainGraph.numNodes() - 1; (i >= 0) && (!para); i--) {
                        Node n = mainGraph.getNodeAt(i);
                        if (n.isInside(e.getPoint())) {
                            destinationP = n.getPosicion();
                            para = true;
                        }
                    }
                    repaint();
                }
                break;
        }
    }

    /**
     * Creation of new data set
     * @param punto Initial point
     * @param dsc Parent dsc
     * @param type_Node Type of the node
     */
    public void new_dataset(Point punto, ExternalObjectDescription dsc, int type_Node) {
        DataSet ds = new DataSet(dsc, punto, this, null, type_Node);
        mainGraph.insertNode(ds);

        elementSelected = true;
        typeSelected = NODE;
    //System.out.println("  Lega a crear el nuevo dataset");

    }

    /**
     * Initializes data set
     * @param punto Initial point
     * @param dsc Parent dsc
     * @param checks Checks array
     * @param dinchecks Dinamic checks array
     * @param pdsc Parent dsc
     * @param actualList Actual list
     * @param type_Node Type of the node
     */
    public void initial_dataset(Point punto, ExternalObjectDescription dsc, Vector checks, Vector dinchecks,
            ExternalObjectDescription pdsc, Vector actualList, int type_Node) {
        int c = 0;
        for (int i = 0; i < checks.size(); i++) {
            if (((JCheckBox) checks.elementAt(i)).isSelected()) {
                ((JButton) (dinchecks.elementAt(i))).setText("Del");
                if (c == 0) {
                    pdsc = new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(i), true);
                } else {
                    pdsc.insert(new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(i)), true);
                }
                c++;
            }
        }
        //This can not be erased, only one dataset of LQD is selected
        if (c == 1) {
            for (int i = 0; i < dinchecks.size(); i++) {
                if (((JButton) dinchecks.elementAt(i)).getText() == "Del") {
                    ((JButton) dinchecks.elementAt(i)).setEnabled(false);
                }
            }
        }

        dsc = new ExternalObjectDescription(pdsc);

        //We create the Dataset node. We take the information of which data sets
        //new_dataset(punto,dsc, type_Node);
        DataSet ds = new DataSet(dsc, punto, this, null, type_Node);

        mainGraph.insertNode(ds);

        elementSelected = true;
        typeSelected = NODE;
    }

    /**
     * Nodes of type LQD
     * @return Type
     */
    public int type_lqd() {
        Point punto = new Point(125, 125);
        ExternalObjectDescription dsc = null; //Type LQD
        ExternalObjectDescription dscLQD_C = null; //Type LQD a crisp
        ExternalObjectDescription dscC_LQD = null; //Type crisp a LQD
        ExternalObjectDescription dscC = null; //Type crisp but we are working in LQD

        int one_active = 0;
        if (parent.panelDatasets.isAnySelected()) {
            initial_dataset(punto, dsc, parent.panelDatasets.checks, parent.dinDatasets.checks, parent.dsc, parent.panelDatasets.actualList, NODELQD);
            one_active = 1;
        }

        if (parent.panelDatasets.isAnySelectedLQD_C()) {
            punto = new Point(125 + 205, 125);
            initial_dataset(punto, dscLQD_C, parent.panelDatasets.checksLQD_C, parent.dinDatasets.checksLQD_C, parent.dscLQD_C, parent.panelDatasets.actualListLQD_C, NODELQD_c);
            one_active = 1;
        }

        if (parent.panelDatasets.isAnySelectedC_LQD()) {
            punto = new Point(125, 125 + 205);
            initial_dataset(punto, dscC_LQD, parent.panelDatasets.checksC_LQD, parent.dinDatasets.checksC_LQD, parent.dscC_LQD, parent.panelDatasets.actualListC_LQD, NODEC_LQD);
            one_active = 1;
        }

        if (parent.panelDatasets.isAnySelectedC()) {
            punto = new Point(125 + 205, 125 + 205);
            initial_dataset(punto, dscC, parent.panelDatasets.checksC, parent.dinDatasets.checksC, parent.dscC, parent.panelDatasets.actualListC, NODEC);
            one_active = 1;
        }

        if (one_active == 0) {
            JOptionPane.showMessageDialog(this, "You have to select with minimum one dataset", "Select Datases", JOptionPane.ERROR_MESSAGE);
        }

        return one_active;

    }

    /**
     * Releasing mouse
     * 
     * @param e Event
     */
    public void mouseReleased(MouseEvent e) {
        int iErrorDataType;
        //    if (parent.DatasetNoElegido && parent.accion != PAINT_DATASET) {
        //      return;
        //    }
        //    if (!parent.panelDatasets.isAnySelected() && parent.accion != PAINT_DATASET){
        //      return;
        //    }

        //System.out.println(" mouse released");

        ExternalObjectDescription dsc = null;

        if (e.getButton() == e.BUTTON1) {
            int sust = -1;
            Point punto = null;
            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            if (Frame.buttonPressed == 1) //Button Teaching pressed
            {
                caseG = parent.cursorAction;
            }
            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            switch (parent.cursorAction) {
                case SELECTING:
                    if (e.getClickCount() == 2 && elementSelected && typeSelected == NODE) {
                        Node n = mainGraph.getNodeAt(mainGraph.numNodes() - 1);
                        // System.out.println("identificador del nodo"+ n.id);

                        if (n.type != Node.type_Dataset) {
                            if (n.dsc.getSubtypelqd() == Node.CRISP2 || n.dsc.getSubtypelqd() == Node.LQD) {
                                if (n.dsc.arg.size() != 0) {
                                    n.contain("Selection of datasets and its parameters", 1, n, parent);
                                } else {
                                    JOptionPane.showMessageDialog(parent,
                                            "This node is not connected with other one", "Error", 2);
                                }
                            } else {
                                n.showDialog();
                            }

                        } else {
                            //parent.datasetsChecksPanel.setVisible(true);
                            //parent.datasetsAlgorithmsSplit.setDividerLocation(280);
                            if (n.type_lqd != Node.CRISP) {
                                //Show the datasets contained in the node
                                if (n.type_lqd == Node.CRISP2) {
                                    n.contain("Keel Crisp Dataset", 1, n, parent);
                                }
                                if (n.type_lqd == Node.LQD) {
                                    n.contain("Keel Low Quality Dataset", 1, n, parent);
                                }
                                if (n.type_lqd == Node.LQD_C) {
                                    n.contain("Keel Low Quality to Crisp Dataset", 1, n, parent);
                                }
                                if (n.type_lqd == Node.C_LQD) {
                                    n.contain("Keel Crisp to Low Quality Dataset", 1, n, parent);
                                }
                            } else {
                                parent.mainSplitPane1.setDividerLocation(-1);
                                parent.statusBarItem.setSelected(true);

                                ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");

                                parent.cursorAction = GraphPanel.SELECTING;
                                parent.status.setText("Data Set selection");
                                parent.cursorDraw = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                            }
                            parent.graphDiagramINNER.setToolTipText("Click twice to see node properties");
                        // XXX descomentar
						/*
                        parent.showDatasetAlgorithm.setIcon(parent.hideDatasetsAlgorithmPanelIcon);
                        parent.displayDatasetAlgorithmCheckMenuItem.setSelected(true);
                        parent.showDatasetAlgorithm.setEnabled(true);
                        parent.selecDatasets.setSelected(true);
                        parent.selectMethods.setSelected(false);
                        parent.selectPostprocessMethods.setSelected(false);
                        parent.selectPreprocessMethods.setSelected(false);
                        parent.selectTestMethods.setSelected(false);
                        parent.selectVisualizeMethods.setSelected(false);
                         */
                        }
                    } else if (paintingBox) {
                        destinationP = e.getPoint();
                        paintingBox = false;
                        multipleSelection = true;

                        int tmp;
                        if (originP.x > destinationP.x) {
                            tmp = originP.x;
                            originP.x = destinationP.x;
                            destinationP.x = tmp;
                        }
                        if (originP.y > destinationP.y) {
                            tmp = originP.y;
                            originP.y = destinationP.y;
                            destinationP.y = tmp;
                        }
                        selectedN.removeAllElements();
                        for (int i = 0; i < mainGraph.numNodes(); i++) {
                            if (mainGraph.getNodeAt(i).centre.x >= originP.x &&
                                    mainGraph.getNodeAt(i).centre.x <= destinationP.x &&
                                    mainGraph.getNodeAt(i).centre.y >= originP.y &&
                                    mainGraph.getNodeAt(i).centre.y <= destinationP.y) {
                                selectedN.addElement(new Integer(i));
                            }
                        }
                        if (!selectedN.isEmpty()) {
                            parent.deleteItem.setEnabled(true);
                        }
                    }
                    parent.setCursor(Cursor.getDefaultCursor());

                    break;
                // Insert node

                case PAINT_DATASET:
                    //System.out.println("paint dataset");


                    // Dataset construction
                    int c = 0;
                    if (parent.objType != parent.LQD) {
                        punto = e.getPoint();
                        for (int i = 0; i < mainGraph.numNodes() && sust == -1; i++) {
                            Node n = mainGraph.getNodeAt(i);
                            if (n.isInside(punto)) {
                                sust = i;
                            }
                        }
                        if (parent.panelDatasets.isAnySelected()) {
                            for (int i = 0; i < parent.panelDatasets.checks.size(); i++) {
                                if (((JCheckBox) parent.panelDatasets.checks.elementAt(i)).isSelected()) {
                                    ((JButton) (parent.dinDatasets.checks.elementAt(i))).setText("Del");
                                    if (parent.objType != parent.LQD) {
                                        ((JButton) (parent.dinDatasets.edits.elementAt(i))).setVisible(true);
                                        parent.dinDatasets.add((JButton) (parent.dinDatasets.edits.elementAt(i)));
                                        if (c == 0) {
                                            parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) parent.panelDatasets.actualList.elementAt(i), true);
                                        } else {
                                            parent.dsc.insert(new ExternalObjectDescription((ExternalObjectDescription) parent.panelDatasets.actualList.elementAt(i)), true);
                                        }
                                    } else {
                                        if (c == 0) {
                                            parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) parent.panelDatasets.actualList.elementAt(i), true);
                                        } else {
                                            parent.dsc.insert(new ExternalObjectDescription((ExternalObjectDescription) parent.panelDatasets.actualList.elementAt(i)), true);
                                        }
                                    }
                                    c++;
                                }
                            }
                            if (c == 1) {
                                for (int i = 0; i < parent.dinDatasets.checks.size(); i++) {
                                    if (((JButton) parent.dinDatasets.checks.elementAt(i)).getText() == "Del") {
                                        ((JButton) parent.dinDatasets.checks.elementAt(i)).setEnabled(false);
                                    }
                                }
                            }

                            dsc = new ExternalObjectDescription(parent.dsc);

                            //We create the Dataset node. We take the information of which data sets
                            DataSet ds = new DataSet(dsc, punto, this, null, NODE);
                            //checks if the partitions are correct
                            boolean regenerationNeeded = regenerateDatasetPartitions(ds);
                            //if some regeneration was made, rebuild the data set node
                            if (regenerationNeeded) {
                                ds = new DataSet(dsc, punto, this, null, NODE);
                            }
                            if (sust >= 0 &&
                                    JOptionPane.showConfirmDialog(parent,
                                    "Do you want to replace this node?",
                                    "Confirm",
                                    JOptionPane.YES_NO_OPTION, 3) ==
                                    JOptionPane.YES_OPTION) {
                                // remove input connections
                                for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                                    Arc a = mainGraph.getArcAt(i);
                                    if (a.getDestination() == sust) {
                                        mainGraph.dropArc(i);
                                    }
                                }
                                mainGraph.dropNode(sust);
                                mainGraph.insertNode(ds, sust);
                            } else {
                                mainGraph.insertNode(ds);
                                elementSelected = true;
                            }
                            typeSelected = NODE;
                            parent.cursorAction = SELECTING;
                            //parent.jTree1.setSelectionPath(null);
                            // XXX DESCOMENTAR
                            //parent.cursorSelect.setSelected(true);
                            parent.status.setText("Click in a node to select it");
                            parent.deleteItem.setEnabled(false);
                            parent.setCursor(Cursor.getDefaultCursor());
                            // XXX DESCOMENTAR
                            //parent.cursorSelect.setEnabled(true);
                            //parent.usuario.setEnabled(true);
                            parent.cursorFlux.setEnabled(true);
                            parent.runButton.setEnabled(true);
                            //parent.cursorFluxMenuItem.setEnabled(true);
                            //parent.runExperimentMenuItem.setEnabled(true);
                            //parent.cursorSelectMenuItem.setEnabled(true);
                            //parent.userMethodsMenuItem.setEnabled(true);
                            parent.saveButton.setEnabled(true);
                            parent.saveExpItem.setEnabled(true);
                            parent.saveAsExpItem.setEnabled(true);
                            parent.showAlgButton.setEnabled(true);
                            parent.selecDatasets.setEnabled(true);
                            parent.selectMethods.setEnabled(true);
                            parent.selectPostprocessMethods.setEnabled(true);
                            if (parent.objType == parent.IMBALANCED) {
                                parent.selectPostprocessMethods.setEnabled(false);
                            }
                            parent.selectPreprocessMethods.setEnabled(true);
                            parent.selectTestMethods.setEnabled(true);
                            parent.selectVisualizeMethods.setEnabled(true);
                            //            parent.undoButton.setEnabled(false);
                            //            parent.redoButton.setEnabled(false);


                            //show the dinamic data set panel
                            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
                            //avoid that the panel with the checks appears again
                            //parent.panelDatasets.setVisible(false);

                            /***************************************************************
                             *********************  EDUCATIONAL KEEL  **********************
                             **************************************************************/
                            if (Frame.buttonPressed == 0) //Button Experiments pressed
                            {
                                parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelp/exp_algoins.html"));
                            } else {
                                parent.helpContent.muestraURL(this.getClass().getResource("/contextualHelpDocente/exp_algoins.html"));
                            }
                            /***************************************************************
                             *********************  EDUCATIONAL KEEL  **********************
                             **************************************************************/
                            parent.runButton.setEnabled(true);
                            parent.runExpItem.setEnabled(true);
                            parent.insertDataflowItem.setEnabled(true);
                        }
                    } //parent.objtype
                    else if (parent.objType == parent.LQD) {
                        //NO INSERTAR NADA EN LQD, SI CREEIS QUE ME FALTA ALGO
                        //MANDARMELO POR EMAIL palaciosana@uniovi.es. Ana Palacios
                        // if(Secundary==0)
                        //{
                        // System.out.println(" tipo lqd 0");
                        int active = type_lqd();
                        //  Secundary=1;
                        if (active == 1) {
                            parent.cursorAction = SELECTING;

                            parent.status.setText("Click twice into a node to view its properties");
                            parent.deleteItem.setEnabled(false);
                            parent.setCursor(Cursor.getDefaultCursor());

                            parent.cursorFlux.setEnabled(true);
                            //parent.runButton.setEnabled(true);


                            parent.saveButton.setEnabled(true);
                            parent.saveExpItem.setEnabled(true);
                            parent.saveAsExpItem.setEnabled(true);
                            parent.showAlgButton.setEnabled(true);
                            parent.selecDatasets.setEnabled(true);
                            parent.selectMethods.setEnabled(true);
                            parent.selectPostprocessMethods.setEnabled(false);
                            parent.selectPreprocessMethods.setEnabled(true);
                            parent.selectTestMethods.setEnabled(false);
                            parent.selectVisualizeMethods.setEnabled(true);
                            //show the dinamic data set panel
                            ((CardLayout) parent.selectionPanel1.getLayout()).show(parent.selectionPanel1, "dinDatasetsCard");
                        }
                    //NO SE PUEDE ACTIVAR EL BOTON DE ZIP SINO HAY CONEXIONES
                    //parent.runButton.setEnabled(true);
                    //parent.runExpItem.setEnabled(true);
                    }

                    break;
                case PAINT_ALGORITHM:
                    punto = e.getPoint();
                    for (int i = 0; i < mainGraph.numNodes() && sust == -1; i++) {
                        Node n = mainGraph.getNodeAt(i);
                        if (n.isInside(punto)) {
                            if (parent.objType == parent.LQD) {
                                punto.setLocation(punto.getX(), punto.getY() + 60);
                            } else {
                                sust = i;
                            }
                        }
                    }

                    // algorithm construction

                    if (parent.objType != parent.LQD) {
                        dsc = new ExternalObjectDescription(parent.dsc);
                    } else {
                        if (parent.RamaLqd == 1) {
                            dsc = new ExternalObjectDescription(parent.dscLQD);
                        } else {
                            dsc = new ExternalObjectDescription(parent.dscCRISP);
                        }
                    }
                    Algorithm alg = new Algorithm(dsc, punto, this);

                    if (sust >= 0 && mainGraph.getNodeAt(sust).getType() != Node.type_Dataset &&
                            JOptionPane.showConfirmDialog(parent,
                            "Do you want to replace this node?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION, 3) ==
                            JOptionPane.YES_OPTION) {
                        if (parent.dsc.getSubtype() == Node.type_Preprocess) {
                            // remove some connections
                            for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                                Arc a = mainGraph.getArcAt(i);
                                if (a.getDestination() == sust) {
                                    if ((mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                            Node.type_Dataset) &&
                                            (mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                            Node.type_Preprocess)) {
                                        mainGraph.dropArc(i);
                                    }
                                }
                            }
                        }
                        // remove input connections (preliminar)
                        for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                            Arc a = mainGraph.getArcAt(i);
                            if (a.getDestination() == sust) {
                                mainGraph.dropArc(i);
                            }
                        }


                        parent.insertUndo();
                        mainGraph.dropNode(sust);
                        mainGraph.insertNode(alg, sust);
                    } else {
                        if (parent.objType != parent.LQD) {
                            parent.insertUndo();
                        }
                        mainGraph.insertNode(alg);
                        elementSelected = true;
                    }
                    typeSelected = NODE;
                    parent.cursorAction = SELECTING;
                    this.setToolTipText("Click twice into a node to view its properties");
                    parent.methodsSelectionTree.setSelectionPath(null);
                    parent.preprocessTree.setSelectionPath(null);
                    parent.postprocessSelectionTree.setSelectionPath(null);
                    // XXX DESCOMENTAR
                    //parent.cursorSelect.setSelected(true);
                    parent.status.setText("Click in a node to select it");
                    if (parent.objType != parent.LQD) {
                        parent.deleteItem.setEnabled(true);
                    }

                    parent.setCursor(Cursor.getDefaultCursor());
                    break;
                case PAINT_USER:
                    punto = e.getPoint();
                    for (int i = 0; i < mainGraph.numNodes() && sust == -1; i++) {
                        Node n = mainGraph.getNodeAt(i);
                        if (n.isInside(punto)) {
                            if (parent.objType == parent.LQD) {
                                punto.setLocation(punto.getX(), punto.getY() + 60);
                            } else {
                                sust = i;
                            }
                        }
                    }

                    // user's method construction
                    dsc = new ExternalObjectDescription(parent.dsc);
                    UserMethod mu = new UserMethod(dsc, punto, this);

                    if (sust >= 0 && mainGraph.getNodeAt(sust).getType() != Node.type_Dataset &&
                            JOptionPane.showConfirmDialog(parent,
                            "Do you want to replace this node?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION, 3) ==
                            JOptionPane.YES_OPTION) {
                        // remove input and output connections (subtype doesn't specified)
                        for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                            Arc a = mainGraph.getArcAt(i);
                            if ((a.getDestination() == sust) || (a.getSource() == sust)) {
                                mainGraph.dropArc(i);
                            }
                        }

                        parent.insertUndo();
                        mainGraph.dropNode(sust);
                        mainGraph.insertNode(mu, sust);
                    } else {

                        parent.insertUndo();
                        mainGraph.insertNode(mu);
                        elementSelected = true;
                    }
                    typeSelected = NODE;
                    parent.cursorAction = SELECTING;
                    this.setToolTipText("Click twice into a node to view its properties");
                    // XXX DESCOMENTAR
                    //parent.cursorSelect.setSelected(true);
                    parent.status.setText("Click in a node to select it");
                    parent.deleteItem.setEnabled(true);
                    parent.setCursor(Cursor.getDefaultCursor());
                    break;
                case PAINT_TEST:
                    punto = e.getPoint();
                    for (int i = 0; i < mainGraph.numNodes() && sust == -1; i++) {
                        Node n = mainGraph.getNodeAt(i);
                        if (n.isInside(punto)) {
                            if (parent.objType == parent.LQD) {
                                punto.setLocation(punto.getX(), punto.getY() + 60);
                            } else {
                                sust = i;
                            }
                        }
                    }

                    // test construction
                    dsc = new ExternalObjectDescription(parent.dsc);
                    Test t = new Test(dsc, punto, this);
                    if (sust >= 0 && mainGraph.getNodeAt(sust).getType() != Node.type_Dataset &&
                            JOptionPane.showConfirmDialog(parent,
                            "Do you want to replace this node?",
                            "Confirm",
                            JOptionPane.YES_NO_OPTION, 3) ==
                            JOptionPane.YES_OPTION) {
                        // remove output and some input connections
                        for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                            Arc a = mainGraph.getArcAt(i);
                            if (a.getSource() == sust) {
                                mainGraph.dropArc(i);
                            } else if (a.getDestination() == sust) {
                                if ((mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                        Node.type_Method) &&
                                        (mainGraph.getNodeAt(a.getSource()).dsc.getSubtype() !=
                                        Node.type_Postprocess)) {
                                    mainGraph.dropArc(i);
                                }
                            }
                        }

                        if (parent.objType != parent.LQD) {
                            parent.insertUndo();
                        }
                        mainGraph.dropNode(sust);
                        mainGraph.insertNode(t, sust);
                    } else {
                        if (parent.objType != parent.LQD) {
                            parent.insertUndo();
                        }
                        mainGraph.insertNode(t);
                        elementSelected = true;
                    }
                    if (parent.objType == parent.LQD) {
                        parent.selectVisualizeMethods.setEnabled(false);
                    }
                    typeSelected = NODE;
                    parent.cursorAction = SELECTING;
                    this.setToolTipText("Click twice into a node to view its properties");
                    parent.testSelectionTree.setSelectionPath(null);
                    parent.visualizeSelectionTree.setSelectionPath(null);
                    // XXX DESCOMENTAR
                    //parent.cursorSelect.setSelected(true);
                    parent.status.setText("Click in a node to select it");
                    if (parent.objType != parent.LQD) {
                        parent.deleteItem.setEnabled(true);
                    }
                    parent.setCursor(Cursor.getDefaultCursor());
                    break;

                /* This version doesn't include multiplexors */
                /* case PAINT_MULTIPLEXOR:
                punto = e.getPoint();
                for (int i = 0; i < mainGraph.numNodos() && sust == -1; i++) {
                Nodo n = mainGraph.getNodoAt(i);
                if (n.dentro(punto)) {
                sust = i;
                }
                }
                Multiplexor m = new Multiplexor(punto, this);
                if (sust >= 0 &&
                JOptionPane.showConfirmDialog(parent,
                "Do you want to replace this node?",
                "Confirm",
                JOptionPane.YES_NO_OPTION, 3) ==
                JOptionPane.YES_OPTION) {
                // remove input and output connections
                for (int i = mainGraph.numArcos() - 1; i >= 0; i--) {
                Arco a = mainGraph.getArcoAt(i);
                if ( (a.getDestino() == sust) || (a.getOrigen() == sust)) {
                mainGraph.eliminarArco(i);
                }
                }
                parent.insertUndo();
                mainGraph.eliminarNodo(sust);
                mainGraph.insertarNodo(m, sust);
                }
                else {
                parent.insertUndo();
                mainGraph.insertarNodo(m);
                elementSelected = true;
                }
                typeSelected = NODE;
                parent.accion = SELECTING;
                parent.seleccionar.setSelected(true);
                parent.estado.setText("Click in a node to select it");
                parent.borrar.setEnabled(true);
                parent.setCursor(Cursor.getDefaultCursor());
                break;
                 */
                // Insert connection

                case PAINT_ARC:

                    destinationP = e.getPoint();
                    paintingLine = false;
                    boolean para = false;
                    int nodo_origen = -1;
                    int nodo_destino = -1;

                    for (int i = mainGraph.numNodes() - 1; (i >= 0) && (!para); i--) {
                        Node n2 = mainGraph.getNodeAt(i);
                        if (n2.isInside(originP)) {
                            nodo_origen = i;
                        }
                        if (n2.isInside(destinationP)) {
                            nodo_destino = i;
                        }
                        if ((nodo_origen >= 0) && (nodo_destino >= 0)) {
                            para = true;
                            if (nodo_origen != nodo_destino) {
                                boolean cont = true;

                                //iErrorDataType = mainGraph.getNodeAt(nodo_origen).isPartialFlowCorrect( mainGraph.getNodeAt(nodo_destino) );
                                //verificaci�n parcial

                                String errorType = mainGraph.getNodeAt(nodo_origen).isPartialFlowCorrect(mainGraph.getNodeAt(nodo_destino));

                                if (mainGraph.getNodeAt(nodo_destino).type != Node.type_Test && mainGraph.getNodeAt(nodo_destino).type != Node.type_Visor) {

                                    if (errorType.length() > 1) {

                                        if (parent.objType == parent.LQD) {
                                            JOptionPane.showMessageDialog(this, errorType, "Alert", JOptionPane.ERROR_MESSAGE);
                                            cont = false;
                                        }
                                        else{

                                            JOptionPane.showMessageDialog(this, errorType, "Warning", JOptionPane.WARNING_MESSAGE);

                                            //5-6-2011: Connections still should be allowed
                                            /*if (parent.objType == parent.INVESTIGATION) {
                                                //cont = false;
                                            }*/



                                            //5-6-2011: Connections still should be allowed
                                            /*if (Frame.buttonPressed == 1) {
                                                cont = false;
                                            }*/
                                        }


                                    }
                                }

                                if (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() == Node.type_Method) {
                                    if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Method) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Two methods can not be joined.", "Error", 2);
                                        cont = false;
                                    }
                                }
                                if (mainGraph.getNodeAt(nodo_destino).type == Node.type_Dataset) {
                                    JOptionPane.showMessageDialog(parent,
                                            "A Dataset node can not have inputs.", "Error", 2);
                                    cont = false;
                                } else if (mainGraph.getNodeAt(nodo_origen).type == Node.type_userMethod || mainGraph.getNodeAt(nodo_destino).type == Node.type_userMethod) {
                                    if (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() == Node.type_Undefined) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Origin User?s Method is not defined yet",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Undefined) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Destiny User?s Method is not defined yet",
                                                "Error", 2);
                                        cont = false;
                                    }
                                } else if ((mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                        Node.type_Preprocess) &&
                                        (mainGraph.getNodeAt(nodo_destino).type !=
                                        Node.type_Multiplexor)) {
                                    if ((mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                            Node.type_Dataset) &&
                                            (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                            Node.type_Preprocess)) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Pre-Process inputs only can be DataSet or other Pre-Process.",
                                                "Error", 2);
                                        cont = false;
                                    }
                                } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                        Node.type_Test) {
                                    if ((mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                            Node.type_Method) &&
                                            (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                            Node.type_Postprocess)) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Test inputs only can be Methods or Post-Process.",
                                                "Error", 2);
                                        cont = false;
                                    }
                                } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                        Node.type_Visor) {
                                    if (parent.objType == parent.LQD) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The node results can not be connected with other node.",
                                                "Error", 2);
                                        cont = false;
                                    } else {
                                        if ((mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Method) &&
                                                (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Postprocess)) {

                                            JOptionPane.showMessageDialog(parent,
                                                    "Visors inputs only can be Methods or Post-Process.",
                                                    "Error", 2);
                                            cont = false;
                                        }
                                    }
                                } else if (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() ==
                                        Node.type_Test) {
                                    JOptionPane.showMessageDialog(parent,
                                            "A Test node cannot have outputs.", "Error", 2);
                                    cont = false;
                                } else if (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() ==
                                        Node.type_Visor) {
                                    JOptionPane.showMessageDialog(parent,
                                            "A Visor node cannot have outputs.", "Error", 2);
                                    cont = false;
                                } else if (mainGraph.getNodeAt(nodo_destino).type ==
                                        Node.type_Multiplexor) {
                                    // multiplexor checks --> equivalents subtype inputs
                                    if ((mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                            Node.type_Dataset) ||
                                            (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                            Node.type_Preprocess)) {
                                        if ((mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Dataset) &&
                                                (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Preprocess)) {
                                            JOptionPane.showMessageDialog(parent,
                                                    "Multiplexor inputs must be DataSets or Pre-Process.",
                                                    "Error", 2);
                                            cont = false;
                                        }
                                    } else if ((mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                            Node.type_Method) ||
                                            (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() ==
                                            Node.type_Postprocess)) {
                                        if ((mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Method) &&
                                                (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                                Node.type_Postprocess)) {
                                            JOptionPane.showMessageDialog(parent,
                                                    "Multiplexor inputs must be Methods or Post-Process.",
                                                    "Error", 2);
                                            cont = false;
                                        }
                                    }
                                }



                                if (parent.objType == parent.LQD) {

                                    if (mainGraph.getNodeAt(nodo_origen).dsc.arg.size() == 0 &&
                                            mainGraph.getNodeAt(nodo_origen).getType() !=
                                            Node.type_Dataset) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The origen node must be connected before with other node",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_origen).getType() ==
                                            Node.type_Dataset && mainGraph.getNodeAt(nodo_destino).dsc.getSubtypelqd() == Node.LQD && (mainGraph.getNodeAt(nodo_origen).type_lqd == Node.LQD_C || mainGraph.getNodeAt(nodo_origen).type_lqd == Node.CRISP2)) {
                                        JOptionPane.showMessageDialog(parent,
                                                "These two preprocessing mechanism can not be together (one is crisp and the other LQD)",
                                                "Error", 2);
                                        cont = false;
                                    } else if ((mainGraph.getNodeAt(nodo_destino).dsc.getSubtypelqd() == Node.LQD || mainGraph.getNodeAt(nodo_destino).dsc.getSubtypelqd() == Node.C_LQD) && (mainGraph.getNodeAt(nodo_origen).dsc.getSubtypelqd() == Node.CRISP2 || mainGraph.getNodeAt(nodo_origen).dsc.getSubtypelqd() == Node.LQD_C)) {
                                        JOptionPane.showMessageDialog(parent,
                                                "These two preprocessing mechanism can not be together (one is crisp and the other LQD)",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() != Node.type_Dataset && mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() !=
                                            Node.type_Dataset &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_origen).par.elementAt(0)).dataset_used.size() == 1 && ((Parameters) mainGraph.getNodeAt(nodo_origen).par.elementAt(0)).dataset_used.get(0).contains("100") == true &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).dataset_used.size() == 1 && ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).dataset_used.get(0).contains("10cv") == true) //mainGraph.getNodeAt(nodo_origen).dsc.getName().contains("Prelabelling")
                                    //&& mainGraph.getNodeAt(nodo_destino).dsc.getName().contains("Prelabelling"))
                                    {
                                        JOptionPane.showMessageDialog(parent,
                                                "These two algorithms cannot be linked. The originating node" +
                                                "supports 100boostrap but the destination node supports 10cv only",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Preprocess &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).cost_instance == true && mainGraph.getNodeAt(nodo_origen).m_bOutputMultiClass == false && mainGraph.getNodeAt(nodo_origen).type_lqd == Node.C_LQD) //Node.C_LQD)
                                    {
                                        JOptionPane.showMessageDialog(parent,
                                                "The files \"Crisp to LQD\" don't have multiclass",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Preprocess && mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() ==
                                            Node.type_Preprocess &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_origen).par.elementAt(0)).cost_instance == true &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).cost_instance == true) //mainGraph.getNodeAt(nodo_origen).dsc.getName().contains("Prelabelling")
                                    //&& mainGraph.getNodeAt(nodo_destino).dsc.getName().contains("Prelabelling"))
                                    {
                                        JOptionPane.showMessageDialog(parent,
                                                "These two preprocessing mechanism can not be together, the origen node only can be connect with the method GFS_Cost_Instances",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Preprocess && mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() ==
                                            Node.type_Preprocess && mainGraph.getNodeAt(nodo_origen).dsc.getName().contains("prelabelling")) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The origen node only can be connect with the method GFS_Cost_Instances",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_origen).getType() == Node.type_Dataset &&
                                            mainGraph.getNodeAt(nodo_destino).dsc.getName().contains("GFS_Cost_Instances")) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The node GFS_Cost_Instances only can be connect with algorithms the preprocessing",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_origen).getType() != Node.type_Dataset && mainGraph.getNodeAt(nodo_destino).getType() != Node.type_Dataset && ((Parameters) mainGraph.getNodeAt(nodo_origen).par.elementAt(0)).cost_instance == true && ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).cost_instance == false) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The destination node does not suport instances with cost",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_origen).getType() != Node.type_Dataset && mainGraph.getNodeAt(nodo_destino).getType() != Node.type_Dataset &&
                                            ((Parameters) mainGraph.getNodeAt(nodo_origen).par.elementAt(0)).cost_instance == false && ((Parameters) mainGraph.getNodeAt(nodo_destino).par.elementAt(0)).cost_instance == true) {
                                        JOptionPane.showMessageDialog(parent,
                                                "The origin node does not suport instances with cost",
                                                "Error", 2);
                                        cont = false;
                                    } else if (mainGraph.getNodeAt(nodo_origen).dsc.getSubtype() == Node.type_Method && mainGraph.getNodeAt(nodo_destino).dsc.getSubtype() == Node.type_Method) {
                                        JOptionPane.showMessageDialog(parent,
                                                "Imposible connect two algorithms of classification",
                                                "Error", 2);
                                        cont = false;
                                    }


                                }


                                // check that connection doesn't exist
                                for (int j = 0; j < mainGraph.numArcs() && cont; j++) {
                                    if (parent.objType != parent.LQD) {
                                        Arc b = mainGraph.getArcAt(j);
                                        if (b.getSource() == nodo_origen &&
                                                b.getDestination() == nodo_destino) {
                                            cont = false;
                                            JOptionPane.showMessageDialog(parent,
                                                    "This connection already exists.", "Error", 2);
                                        }
                                    } else {
                                        Arc b = mainGraph.getArcAt(j);
                                        if (b.getSource() == mainGraph.getNodeAt(nodo_origen).id &&
                                                b.getDestination() == mainGraph.getNodeAt(nodo_destino).id) {
                                            cont = false;
                                            JOptionPane.showMessageDialog(parent,
                                                    "This connection already exists.", "Error", 2);
                                        }
                                    }
                                }

                                //check that destination node only have a connection (1-n connections if it is a test node)
                                if (parent.objType != parent.LQD) {
                                    int contador_entradas = 0;
                                    for (int j = 0; j < mainGraph.numArcs() && cont; j++) {
                                        Arc b = mainGraph.getArcAt(j);
                                        if (b.getDestination() == nodo_destino && mainGraph.getNodeAt(nodo_destino).getType() != Node.type_Test) {
                                            cont = false;
                                            JOptionPane.showMessageDialog(parent,
                                                    "This node can only have an input.", "Error", 2);
                                        } else if (b.getDestination() == nodo_destino && mainGraph.getNodeAt(nodo_destino).getType() == Node.type_Test && ((Parameters) (((Test) mainGraph.getNodeAt(nodo_destino)).par.elementAt(0))).getNumInputs() > 0) {
                                            contador_entradas++;
                                            if (contador_entradas >= ((Parameters) (((Test) mainGraph.getNodeAt(nodo_destino)).par.elementAt(0))).getNumInputs()) {
                                                cont = false;
                                                JOptionPane.showMessageDialog(parent,
                                                        "This test can only have " + ((Parameters) (((Test) mainGraph.getNodeAt(nodo_destino)).par.elementAt(0))).getNumInputs() + " inputs.", "Error", 2);
                                            }
                                        }
                                    }
                                }


                                if (cont) {
                                    Arc a = null;
                                    if (parent.objType == parent.LQD) {
                                        a = new Arc(mainGraph.getNodeAt(nodo_origen).id, mainGraph.getNodeAt(nodo_destino).id, this);
                                    }
                                    if (parent.objType != parent.LQD) {
                                        parent.insertUndo();
                                        a = new Arc(nodo_origen, nodo_destino, this);
                                    }
                                    mainGraph.insertArc(a);
                                    if (parent.objType != parent.LQD) {
                                        if (existCycles()) {
                                            if (parent.objType != parent.LQD) {
                                                mainGraph.dropArc(mainGraph.numArcs() - 1);
                                            }

                                            JOptionPane.showMessageDialog(parent,
                                                    "This connection creates a cicle in the experiment.",
                                                    "Error", 2);
                                        }
                                    } else {
                                        elementSelected = true;
                                        //parent.deleteItem.setEnabled(true);
                                        typeSelected = ARC;

                                        //Insert the datasets in the destination if the origen is a type.dataset
                                        parent.runButton.setEnabled(true);
                                        if (mainGraph.getNodeAt(nodo_origen).getType() == Node.type_Dataset) {
                                            ((DataSet) mainGraph.getNodeAt(nodo_origen)).contain("Selection of datasets and its parameters", 0, mainGraph.getNodeAt(nodo_destino), parent);
                                        } else {
                                            ((Algorithm) mainGraph.getNodeAt(nodo_origen)).contain("Datasets and its parameters", 2, mainGraph.getNodeAt(nodo_destino), parent);
                                        }


                                    }
                                }
                            } //if the nodes are diferent
                        }//if we have the first and destination node
                    }//for numbers of nodes
                    break;
            }

            //System.out.println(" sale de todos");
            //refresh the panel
            repaint();

            //this sentence "paints" the dinamic data set over the checks data sets
            //panel, but it is not funcional in this new version of the GUI
            //      parent.dinDatasets.repaint();

            /***************************************************************
             *********************  EDUCATIONAL KEEL  **********************
             **************************************************************/
            if (Frame.buttonPressed == 1) //Button Teaching pressed
            {

                //window of partitions is opened and the user has modified the experiment area
                if ((parent.getExecDocentWindowState() == false) && (caseG == PAINT_ALGORITHM || caseG == PAINT_USER || caseG == PAINT_TEST)) {
                    //System.out.println("AKI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    Object[] options = {"OK", "CANCEL"};
                    int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                            "OK presses to STOP experiment.Results will be losed!. \n", "Warning!",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[0]);
                    if (n == JOptionPane.YES_OPTION) {

                        parent.deleteExecDocentWindow();
                        parent.closedEducationalExec(null);
                    } else {
                        //Undo changes
                        parent.forceUndo();
                    }
                }
            }
        /***************************************************************
         *********************  EDUCATIONAL KEEL  **********************
         **************************************************************/
        } // context menu
        else if (e.isPopupTrigger() && elementSelected) {
            parent.setCursor(Cursor.getDefaultCursor());
            JPopupMenu popup = new JPopupMenu();
            if (typeSelected == NODE &&
                    mainGraph.getNodeAt(mainGraph.numNodes() - 1).type != Node.type_Multiplexor && mainGraph.getNodeAt(mainGraph.numNodes() - 1).type != Node.type_Dataset) {
                JMenuItem menuItem = new JMenuItem("Show Parameters");
                menuItem.setIcon(new ImageIcon(this.getClass().getResource(
                        "/keel/GraphInterKeel/resources/ico/experiments/division.gif")));
                menuItem.addActionListener(new GraphPanel_jMenuItem2_actionAdapter(this));
                popup.add(menuItem);
                popup.addSeparator();
            }
            if (mainGraph.getNodeAt(mainGraph.numNodes() - 1).type != Node.type_Dataset || typeSelected == ARC) {
                JMenuItem menuItem2 = new JMenuItem("Delete");
                menuItem2.setIcon(new ImageIcon(this.getClass().getResource(
                        "/keel/GraphInterKeel/resources/ico/experiments/borrar.gif")));
                menuItem2.addActionListener(new GraphPanel_jMenuItem1_actionAdapter(this));
                popup.add(menuItem2);
            }
            if (parent.objType == parent.LQD) {
                if (typeSelected == NODE && mainGraph.getNodeAt(mainGraph.numNodes() - 1).type == Node.type_Dataset) {
                    JMenuItem menuItem = new JMenuItem("Show Parameters");
                    menuItem.setIcon(new ImageIcon(this.getClass().getResource(
                            "/keel/GraphInterKeel/resources/ico/experiments/division.gif")));
                    menuItem.addActionListener(new GraphPanel_jMenuItem2_actionAdapter(this));
                    popup.add(menuItem);
                    popup.addSeparator();
                }
            /* else if(typeSelected == ARC)
            {
            JMenuItem menuItem2 = new JMenuItem("Delete");
            menuItem2.setIcon(new ImageIcon(this.getClass().getResource(
            "/keel/GraphInterKeel/resources/ico/experiments/borrar.gif")));
            menuItem2.addActionListener(new GraphPanel_jMenuItem1_actionAdapter(this));
            popup.add(menuItem2);
            }*/

            }
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    //Mouse handlers
    public void mouseEntered(MouseEvent e) {
        requestFocusInWindow();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Updates the position of the node in the panel
     * @param e the event associated to the node movement
     */
    public void updatePosition(MouseEvent e) {
        Node n = mainGraph.getNodeAt(mainGraph.numNodes() - 1);
        Point nuevo = new Point(last.x + e.getPoint().x, last.y + e.getPoint().y);
        if (nuevo.x >= 26 && nuevo.y >= 26 && nuevo.x <= (getWidth() - 26) &&
                nuevo.y <= (getHeight() - 42)) {
            n.setPosicion(nuevo);
            repaint();
        }
    }

    // scroll control
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {

        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;

        }
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement) * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1) * maxUnitIncrement -
                    currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }

    // keyboard events
    public void keyTyped(KeyEvent evt) {
    }

    public void keyReleased(KeyEvent evt) {
    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public void keyPressed(KeyEvent evt) {
        if (Frame.buttonPressed == 0) //Button Experiments pressed
        {
            this.keyPressedAux(evt);
        } else //Button Teaching pressed
        {
            //System.out.println("ESTOY AKI!!!!!!!!!!!!!!!");
            //experiment opened
            if (parent.getExecDocentWindowState() == false) {
                Object[] options = {"OK", "CANCEL"};
                int n = JOptionPane.showOptionDialog(this, "The actual experiment is opened!. \n" +
                        "OK presses to STOP experiment.Results will be losed. \n", "Warning!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    parent.deleteExecDocentWindow();
                    parent.closedEducationalExec(null);
                    this.keyPressedAux(evt);
                }
            } else {
                this.keyPressedAux(evt);
            }
        }
    }

    /***************************************************************
     *********************  EDUCATIONAL KEEL  **********************
     **************************************************************/
    public void keyPressedAux(KeyEvent evt) {
        if (!multipleSelection) {
            if (elementSelected && typeSelected == NODE) {
                Node n = mainGraph.getNodeAt(mainGraph.numNodes() - 1);
                if (evt.getKeyCode() == KeyEvent.VK_UP) {
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.y -= 10;
                    if (nuevo.y >= 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.y += 10;
                    if (nuevo.y <= getHeight() - 42) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.x -= 10;
                    if (nuevo.x >= 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.x += 10;
                    if (nuevo.x <= getWidth() - 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (n.getType() != n.type_Dataset) {

                        if (parent.objType != parent.LQD) {
                            parent.insertUndo();
                            for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                                Arc a = mainGraph.getArcAt(i);
                                if (a.getSource() == (mainGraph.numNodes() - 1)) {
                                    mainGraph.dropArc(i);
                                } else if (a.getDestination() == (mainGraph.numNodes() - 1)) {
                                    mainGraph.dropArc(i);
                                }
                            }
                            mainGraph.dropNode(mainGraph.numNodes() - 1);
                        } else {
                            if (mainGraph.getNodeAt(node_selected).dsc.getName(0).compareTo("Results") == 0) {
                                JOptionPane.showMessageDialog(parent, "This node can not be erased", "Error", 2);
                            } else {
                                if (JOptionPane.showConfirmDialog(this, "Do you want to remove this node and all its way?",
                                        "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                                    boolean found = false;
                                    for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                                        Arc a = mainGraph.getArcAt(i);
                                        int nodes = mainGraph.numNodes();
                                        if (a.getDestination() == (mainGraph.getNodeAt(node_selected).id)) {
                                            found = true;
                                            mainGraph.dropArcLQD(i);
                                            if (nodes == mainGraph.numNodes()) {
                                                i = mainGraph.numArcs();
                                            } else {
                                                break;
                                            }
                                        }

                                    }
                                    if (found == false) {
                                        mainGraph.dropNodeLQD_move(node_selected);
                                    }
                                }
                            }
                        }//else
                        elementSelected = false;
                        parent.deleteItem.setEnabled(false);
                    }

                    repaint();
                }
            } //If a node
            else if (elementSelected && typeSelected == ARC &&
                    evt.getKeyCode() == KeyEvent.VK_DELETE) {

                if (parent.objType != parent.LQD) {
                    parent.insertUndo();
                    mainGraph.dropArc(mainGraph.numArcs() - 1);
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Do you want to remove this arc and all the arc contained in the way?",
                            "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                        mainGraph.dropArcLQD(arc_selected);
                    }
                }
                elementSelected = false;
                parent.deleteItem.setEnabled(false);
                repaint();
            }
            evt.consume();
        } else { // multiple selection
            Point minimo = new Point();
            Point maximo = new Point();
            minimo.x = minimo.y = Integer.MAX_VALUE;
            maximo.x = maximo.y = Integer.MIN_VALUE;
            for (int i = 0; i < selectedN.size(); i++) {
                Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                Point p = new Point(n.getPosicion());

                if (p.x < minimo.x) {
                    minimo.x = p.x;
                }
                if (p.y < minimo.y) {
                    minimo.y = p.y;
                }
                if (p.x > maximo.x) {
                    maximo.x = p.x;
                }
                if (p.y > maximo.y) {
                    maximo.y = p.y;
                }
            }

            if (evt.getKeyCode() == KeyEvent.VK_UP) {
                for (int i = 0; i < selectedN.size(); i++) {
                    Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.y -= 10;
                    if (minimo.y >= 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
                for (int i = 0; i < selectedN.size(); i++) {
                    Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.y += 10;
                    if (maximo.y <= getHeight() - 42) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                for (int i = 0; i < selectedN.size(); i++) {
                    Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.x -= 10;
                    if (minimo.x >= 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
                for (int i = 0; i < selectedN.size(); i++) {
                    Node n = mainGraph.getNodeAt(((Integer) (selectedN.elementAt(i))).intValue());
                    Point nuevo = new Point(n.getPosicion());
                    nuevo.x += 10;
                    if (maximo.x <= getWidth() - 26) {
                        n.setPosicion(nuevo);
                        repaint();
                    }
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                if (parent.objType == parent.LQD) {
                    JOptionPane.showMessageDialog(this, "Several elements are selected, select the element that you want to remove",
                            "Select only one element", JOptionPane.ERROR_MESSAGE);
                } else {
                    parent.insertUndo();
                    for (int j = 0; j < selectedN.size(); j++) {
                        int el = ((Integer) (selectedN.elementAt(j))).intValue();
                        Node n = mainGraph.getNodeAt(el - j);
                        mainGraph.dropNode(el - j);
                        mainGraph.insertNode(n);
                        for (int k = 0; k < mainGraph.numArcs(); k++) {
                            Arc a = mainGraph.getArcAt(k);
                            int index_origen = a.getSource();
                            int index_destino = a.getDestination();
                            if (index_origen == el - j) {
                                a.setSource(mainGraph.numNodes() - 1);
                            } else if (index_origen > el - j) {
                                a.setSource(index_origen - 1);
                            }
                            if (index_destino == el - j) {
                                a.setDestination(mainGraph.numNodes() - 1);
                            } else if (index_destino > el - j) {
                                a.setDestination(index_destino - 1);
                            }
                        }

                        if (n.getType() != n.type_Dataset) {
                            for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                                Arc a = mainGraph.getArcAt(i);
                                if (a.getSource() == (mainGraph.numNodes() - 1)) {
                                    mainGraph.dropArc(i);
                                } else if (a.getDestination() == (mainGraph.numNodes() - 1)) {
                                    mainGraph.dropArc(i);
                                }
                            }
                            mainGraph.dropNode(mainGraph.numNodes() - 1);
                        }
                    }
                    multipleSelection = false;

                    repaint();
                }
            }
            evt.consume();
        }
    }

    /**
     * method that check if graph contains cycles
     * @return true if cycles are present, false otherwise
     */
    private boolean existCycles() {

        boolean visitados[]; // bits vector that indicates if a node was visited
        Stack pila = new Stack(); // stack
        int nVisit = 0; // number of nodes visited (for conexant components)
        boolean parar = false; //indicates if a cycle is found
        int i;
        int nodo, el;
        Vector temp = new Vector();
        boolean insert = true;

        visitados = new boolean[mainGraph.numNodes()];
        Arrays.fill(visitados, false);

        while (nVisit < mainGraph.numNodes() && !parar) {
            for (i = 0; visitados[i]; i++) {
                ;
            }
            pila.push(new Integer(i));
            while (!pila.empty() && !parar) {
                if (!insert) {
                    temp.removeAllElements(); // if path is finished, restart
                }
                nodo = ((Integer) (pila.pop())).intValue();
                visitados[nodo] = true;
                nVisit++;
                temp.addElement(new Integer(nodo));
                insert = false;
                for (i = 0; i < mainGraph.numArcs(); i++) {
                    if (mainGraph.getArcAt(i).getSource() == nodo) {
                        el = mainGraph.getArcAt(i).getDestination();
                        if (temp.contains(new Integer(el))) { // there is a cycle
                            parar = true;
                        } else {
                            pila.push(new Integer(el));
                            insert = true;
                        }
                    }
                }
            }
        }

        return parar;
    }

    /**
     * Remove action
     * @param e Event
     */
    void jMenuItem1_actionPerformed(ActionEvent e) {
        // remove connection

        if (typeSelected == ARC) {

            if (parent.objType != parent.LQD) {
                parent.insertUndo();
                mainGraph.dropArc(mainGraph.numArcs() - 1);
            } else {
                if (JOptionPane.showConfirmDialog(this, "Do you want to remove this arc and all the arc contained in the way?",
                        "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                    mainGraph.dropArcLQD(arc_selected);
                }
            }
        // remove node
        } else if (typeSelected == NODE) {

            if (parent.objType != parent.LQD) {
                parent.insertUndo();
                for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                    Arc a = mainGraph.getArcAt(i);
                    if (a.getSource() == (mainGraph.numNodes() - 1)) {
                        mainGraph.dropArc(i);
                    } else if (a.getDestination() == (mainGraph.numNodes() - 1)) {
                        mainGraph.dropArc(i);
                    }
                }
                mainGraph.dropNode(mainGraph.numNodes() - 1);
            } else {
                if (mainGraph.getNodeAt(node_selected).dsc.getName(0).compareTo("Results") == 0) {
                    JOptionPane.showMessageDialog(parent, "This node can not be erased", "Error", 2);
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Do you want to remove this node and all its way?",
                            "Remove node", JOptionPane.YES_NO_OPTION, 3) == JOptionPane.YES_OPTION) {
                        boolean found = false;
                        for (int i = mainGraph.numArcs() - 1; i >= 0; i--) {
                            Arc a = mainGraph.getArcAt(i);
                            int nodes = mainGraph.numNodes();
                            if (a.getDestination() == (mainGraph.getNodeAt(node_selected).id)) {

                                found = true;
                                mainGraph.dropArcLQD(i);
                                if (nodes == mainGraph.numNodes()) {
                                    i = mainGraph.numArcs();
                                } else {
                                    break;
                                }
                            }



                        }
                        if (found == false) {
                            mainGraph.dropNodeLQD_move(node_selected);
                        }
                    }
                }
            }
        }
        elementSelected = false;
        parent.deleteItem.setEnabled(false);

        repaint();
    }

    /**
     * Remove node
     * @param e Event
     */
    void jMenuItem2_actionPerformed(ActionEvent e) {

        Node n = mainGraph.getNodeAt(mainGraph.numNodes() - 1);

        if (parent.objType != parent.LQD) {
            if (n.type != Node.type_Dataset) {
                n.showDialog();
            }
        } else {
            if (mainGraph.getNodeAt(node_selected).dsc.getName(0).compareTo("Results") == 0) {
                JOptionPane.showMessageDialog(parent, "This node can not be erased", "Error", 2);
            } else {
                if (n.type != Node.type_Dataset) {
                    if (n.dsc.arg.size() != 0) {
                        n.contain("Selection of datasets and its parameters", 2, n, parent);
                    } else {
                        JOptionPane.showMessageDialog(parent, "This node is not connected with other one", "Error", 2);
                    }
                } else {

                    if (n.type_lqd == Node.CRISP2) {
                        n.contain("Keel Crisp Dataset", 1, n, parent);
                    }
                    if (n.type_lqd == Node.LQD) {
                        n.contain("Keel Low Quality Dataset", 1, n, parent);
                    }
                    if (n.type_lqd == Node.LQD_C) {
                        n.contain("Keel Low Quality a Crisp Dataset", 1, n, parent);
                    }
                    if (n.type_lqd == Node.C_LQD) {
                        n.contain("Keel Crisp a Low Quality Dataset", 1, n, parent);
                    }
                }
            }
        }
    }

    /**
     * Builder
     */
    public GraphPanel() {
        try {
            initPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize
     * @throws java.lang.Exception
     */
    private void initPanel() throws Exception {
        this.setBackground(new Color(225, 225, 225));
        this.setFont(new java.awt.Font("Arial", 0, 11));
    }

    /**
     * This method checks if any partition of the selected
     * data sets is missing. If it is the case, then it will ask the user
     * to re-generate them.
     * @param ds Data set
     * @return true if a new data set node needs to be created
     */
    protected boolean regenerateDatasetPartitions(DataSet ds) {
        Vector missingPartitions;
        String msg;
        PartitionCreator pc;
        ProgressMonitor pm;
        int total;
        int counter = 0;
        String message[] = new String[ds.getMissingVector().size()];

        Arrays.fill(message, "");

        //if missing partitions, let us re-generate them
        if (!ds.isComplete()) {
            msg = "There were missing partitions for the following data sets:\n";
            missingPartitions = ds.getMissingVector();
            for (int i = 0; i < missingPartitions.size(); i++) {
                Vector aux = ((Vector) (missingPartitions.get(i)));
                if (aux.isEmpty() == false) {
                    if (!addToMessage(message, counter, ds.dsc.name[i])) {
                        counter++;
                    }
                }
            }
            for (int i = 0; i < counter; i++) {
                msg += "       " + message[i] + "\n";
            }


            msg += "\n Do you want them to be generated?";

            int ans = JOptionPane.showConfirmDialog(this, msg, "Missing Partitions Found", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ans == 0) {
                //User wants to regenerate the partitions
                total = parent.numberKFoldCross * missingPartitions.size();
                pm = new ProgressMonitor(this, "Generating missing partitions", "", 0, total);
                pm.setMillisToDecideToPopup(0);
                pm.setMillisToPopup(0);

                pc = new PartitionCreator(parent, ds, pm);

                pc.execute();

                //wait for the SwingWorker thread to finish before continuing
                //the experiment generation
                synchronized (pc) {
                    try {
                        pc.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (pm.isCanceled()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Add to message
     * @param message New sub message
     * @param counter Counter
     * @param name Name associated
     * @return Message
     */
    private boolean addToMessage(String message[], int counter, String name) {

        boolean exists = false;
        int i = 0;

        while ((!exists) && (i < counter)) {

            if (message[i].equalsIgnoreCase(name)) {
                exists = true;
            }
            i++;
        }

        if (!exists) {
            message[counter] = name;
        }

        return exists;
    }
}

class GraphPanel_jMenuItem1_actionAdapter
        implements java.awt.event.ActionListener {

    GraphPanel adaptee;

    GraphPanel_jMenuItem1_actionAdapter(GraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuItem1_actionPerformed(e);
    }
}

class GraphPanel_jMenuItem2_actionAdapter
        implements java.awt.event.ActionListener {

    GraphPanel adaptee;

    GraphPanel_jMenuItem2_actionAdapter(GraphPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuItem2_actionPerformed(e);
    }
}
