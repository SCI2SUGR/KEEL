package keel.GraphInterKeel.experiments;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.awt.event.*;
import java.util.*;

import keel.GraphInterKeel.datacf.DataCFFrame;

/**
 * <p>Title: Keel</p>
 * <p>Description: DataSets selection</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Universidad de Granada</p>
 * @author Victor Manuel Gonzalez Quevedo
 * @version 2.0
 */
public class DinamicDataset extends JPanel implements Scrollable {

    private int maxUnitIncrement = 10;
    private int pos = 10;
    Vector checks;
    Vector datasetList = new Vector();
    Vector datasetXML = new Vector();
    Vector actualList;
    Vector edits;
    Experiments parent;
    boolean activos[];
    JDialog dialogDinamic;
    JButton importB = new JButton();
    Hashtable<String, Boolean> dataActive = new Hashtable<String, Boolean>();
    JButton selectAll = new JButton();
    JButton invertSelection = new JButton();
    JButton selectAllUser = new JButton();
    JButton invertSelectionUser = new JButton();
    int datasetsNoUser;
    int datasetsUser;

    public DinamicDataset() {
        super();
    }

    public DinamicDataset(Experiments newParent) {
        try {
            this.parent = newParent;
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        //importB.setBackground(new Color(0, 0, 0));
        importB.setText("Import");
        importB.addActionListener(new DinamicDataset_importar_actionAdapter(this));

        
        selectAll.setBackground(new Color(225, 225, 225));
        selectAll.setText("Select All");
        selectAll.addActionListener(new DinamicDataset_selectAll_actionAdapter(this));
        invertSelection.setBackground(new Color(225, 225, 225));
        invertSelection.setText("Invert");
        invertSelection.addActionListener(new DinamicDataset_invertSelection_actionAdapter(this));
        selectAllUser.setBackground(new Color(225, 225, 225));
        selectAllUser.setText("Select All");
        selectAllUser.addActionListener(new DinamicDataset_selectAllUser_actionAdapter(this));
        invertSelectionUser.setBackground(new Color(225, 225, 225));
        invertSelectionUser.setText("Invert");
        invertSelectionUser.addActionListener(new DinamicDataset_invertSelectionUser_actionAdapter(this));
    
    }

    /**
     * Insert a new data set in the list, from an External Object Description
     * @param ds The external object description which contains the data set(s)
     * @param path the path to the  file(s) of this data set
     */
    public void insert(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetList.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXML.add(ds);
    }

    /**
     * Clear the vectors which stores the list of data sets
     */
    public void removeAllData() {
        datasetList.removeAllElements();
        datasetXML.removeAllElements();
    }

    /**
     * Checks if any of the data sets are selected in the list
     * @return
     */
    public boolean isAnySelected() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for saving the selected data sets so we can restore them later
     */
    public void saveSelected() {
        dataActive = new Hashtable<String, Boolean>();

        for (int i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                dataActive.put(((DatasetXML) datasetXML.elementAt(i)).nameComplete, new Boolean(true));
            }
        }
    }

    /**
     * Clear all data estructures, and allocates new memory for them
     */
    public void clear() {
        checks = new Vector();
        datasetList = new Vector();
        actualList = new Vector();
        this.removeAll();
    }

    /**
     * Reload the data set list, given a type of experiment
     * @param type The type of experiment
     */
    public void reload(int type) {
        pos = 20;
        this.removeAll();
        checks = new Vector();
        edits = new Vector();

        String cad = "classification";
        switch (type) {
            case Experiments.CLASSIFICATION:
                cad = "classification";
                break;
            case Experiments.REGRESSION:
                cad = "regression";
                break;
            case Experiments.UNSUPERVISED:
                cad = "unsupervised";
                break;
        }

        actualList = new Vector();

        JLabel titulo1 = new JLabel("KEEL Datasets");
        titulo1.setFont(new Font("Arial", Font.BOLD, 14));
        titulo1.setBounds(new Rectangle(10, 0, 200, 16));
        this.add(titulo1);

        datasetsNoUser = 0;
        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == false) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, pos, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    chk.addActionListener(new DinamicDataset_Checks_actionAdapter(this));
                    this.add(chk);
                    checks.add(chk);
                    JButton edt = new JButton();
                    edt.setBounds(new Rectangle(180, pos, 60, 20));
                    edt.setOpaque(true);
                    edt.setText("Edit");
                    edt.setFont(new Font("Arial", Font.PLAIN, 8));
                    edt.addActionListener(new DinamicDataset_Edits_actionAdapter(this));
                    edits.add(edt);
                    String nameAbrv = "   " + ((DatasetXML) datasetXML.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, pos, 200, 20));
                    this.add(txt);
                    pos += 25;
                    actualList.add(datasetList.elementAt(i));
                    datasetsNoUser++;
                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        //Buttons to select all datasets and invert selection

        pos += 10;

        selectAll.setBounds(new Rectangle(15, pos, 110, 20));
        this.add(selectAll);

        invertSelection.setBounds(new Rectangle(130, pos, 110, 20));
        this.add(invertSelection);

        pos += 30;

        /*
         * USER DATASETS
         */

        JLabel titulo2 = new JLabel("User Datasets");
        titulo2.setFont(new Font("Arial", Font.BOLD, 14));
        titulo2.setBounds(new Rectangle(10, pos, 200, 16));
        this.add(titulo2);
        pos += 25;

        datasetsUser = 0;
        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == true) {
                    JButton chk = new JButton();
                    chk.setBounds(new Rectangle(115, pos, 60, 20));
                    chk.setOpaque(true);
                    chk.setText("Add");
                    chk.setFont(new Font("Arial", Font.PLAIN, 8));
                    chk.addActionListener(new DinamicDataset_Checks_actionAdapter(this));
                    this.add(chk);
                    checks.add(chk);
                    JButton edt = new JButton();
                    edt.setBounds(new Rectangle(180, pos, 60, 20));
                    edt.setOpaque(true);
                    edt.setText("Edit");
                    edt.setFont(new Font("Arial", Font.PLAIN, 8));
                    edt.addActionListener(new DinamicDataset_Edits_actionAdapter(this));
                    edits.add(edt);
                    String nameAbrv = "   " + ((DatasetXML) datasetXML.elementAt(i)).nameComplete;
                    if (nameAbrv.length() > 20) {
                        nameAbrv = nameAbrv.substring(0, 20) + "...";
                    }
                    JLabel txt = new JLabel(nameAbrv);
                    txt.setBounds(new Rectangle(0, pos, 200, 20));
                    this.add(txt);
                    pos += 25;
                    actualList.add(datasetList.elementAt(i));

                    datasetsUser++;
                }

            } catch (java.net.MalformedURLException ex) {
            }
        }
        activos = new boolean[checks.size()];

        //Buttons to select all datasets and invert selection

        pos += 10;

        selectAllUser.setBounds(new Rectangle(15, pos, 110, 20));
        this.add(selectAllUser);

        invertSelectionUser.setBounds(new Rectangle(130, pos, 110, 20));
        this.add(invertSelectionUser);

        pos += 30;

        importB.setBounds(new Rectangle(85, pos, 90, 20));
        this.add(importB);
        pos += 20;

        parent.dinDatasetsScrollPane.getViewport().setBackground(this.getBackground());

        this.setPreferredSize(new Dimension(250, pos + 10));

        this.repaint();

    }

    /**
     * Once the Buttons has been loaded again, we must set their state
     * as it was previously set by the user <- we take the state saved from saveSelected()
     */
    public void reloadPreviousActiveDataSets() {
        Boolean isActive;
        int i;

        //System.out.println("  > Inici reloadPreviousActiveDatasets");

        //Initialize the state of the Dataset
        //((Nodo)parent.grafo.getNodos().elementAt(0)).updateState();

        for (int id = 0; id < checks.size(); id++) {
            isActive = dataActive.get(((DatasetXML) datasetXML.elementAt(id)).nameComplete);
            if (isActive == null) { //new data set
                ((JButton) (checks.elementAt(id))).setText("Add");
                ((JButton) (edits.elementAt(id))).setVisible(false);
                this.remove(((JButton) (edits.elementAt(id))));
            } else {
                if (!isActive) { //not selected before
                    ((JButton) (checks.elementAt(id))).setText("Add");
                    ((JButton) (edits.elementAt(id))).setVisible(false);
                    this.remove(((JButton) (edits.elementAt(id))));
                } else { //selected before
                    ((JButton) (checks.elementAt(id))).setText("Del");
                    ((JButton) (edits.elementAt(id))).setVisible(true);
                    this.add((JButton) (edits.elementAt(id)));
                }
            }
        }

        if (Layer.numLayers == 1) {
            for (i = 0; i < checks.size(); i++) {
                if (((JButton) checks.elementAt(i)).getText() == "Del") {
                    ((JButton) checks.elementAt(i)).setEnabled(false);
                }
            }
        } else {
            for (i = 0; i < checks.size(); i++) {
                ((JButton) checks.elementAt(i)).setEnabled(true);
            }
        }
    //System.out.println("  > End reloadPreviousActiveDatasets");
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

    /**
     * When the "Edit" button is pressed, this function is executed
     * @param e The event associated
     */
    void edits_actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        int i, id = 0;
        DataSet dataset = new DataSet();
        int cont = -1;

        //search the editbutton clicked
        for (i = 0; i < edits.size(); i++) {
            if (s == edits.elementAt(i)) {
                id = i;
            }
        }
        //search the correct layer
        for (i = 0; i <= id; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                cont++;
            }
        }

        for (i = 0; i < parent.experimentGraph.numNodes(); i++) {
            if (parent.experimentGraph.getNodeAt(i).getType() == Node.type_Dataset) {
                dataset = (DataSet) parent.experimentGraph.getNodeAt(i);
            }
        }

        if (parent.cvType == Experiments.P5X2) {
            dialogDinamic = new DialogDataset2(parent, "DataSet", true, dataset, cont);
        } else {
            dialogDinamic = new DialogDataset(parent, "DataSet", true, dataset, cont);
        //dialogDinamic.getContentPane().setBackground(new Color(225, 225, 225));
        }
        // Center dialog
        dialogDinamic.setSize(464, 580);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialogDinamic.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialogDinamic.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialogDinamic.setResizable(false);
        dialogDinamic.setVisible(true);

    }

    private void loadDatasetInfo() {

        File dir;
        String[] ficheros;
        int i, c;
        ExternalObjectDescription dsc, dscal;
        String valores[];
        boolean actTemp[];
        int posAction = 0;
        boolean action = false;
        boolean missing = false;
        boolean found;
        Vector listas = new Vector();

        actTemp = new boolean[checks.size()];

        for (i = 0, c = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                actTemp[i] = true;
                if (c == 0) {
                    parent.dsc = new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(i));
                } else {
                    parent.dsc.insert(new ExternalObjectDescription((ExternalObjectDescription) actualList.elementAt(i)));
                }
                c++;
            } else {
                actTemp[i] = false;
            }
        }
        dsc = new ExternalObjectDescription(parent.dsc);


        valores = dsc.getAllNames();

        Layer.numLayers = valores.length;
        if (Layer.numLayers == 1) {
            for (i = 0; i < checks.size(); i++) {
                if (((JButton) checks.elementAt(i)).getText() == "Del") {
                    ((JButton) checks.elementAt(i)).setEnabled(false);
                }
            }
        } else {
            for (i = 0; i < checks.size(); i++) {
                ((JButton) checks.elementAt(i)).setEnabled(true);
            }
        }
        Layer.layerActivo = 0;
        System.out.println("New data sets number = " + Layer.numLayers);

        for (i = 0; i < Layer.numLayers; i++) {
            listas.addElement(new Vector());
        }

        Vector save;
        //update the extenal object descriptions
        //for the data set added
        for (i = 0; i < parent.graphDiagramINNER.mainGraph.numNodes(); i++) {
            if (parent.graphDiagramINNER.mainGraph.getNodeAt(i).type == Node.type_Dataset) {
                parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc = new ExternalObjectDescription(dsc);
                save=(Vector)((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.clone();
                ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector = new Vector();

                for (int j = 0; j < dsc.getNamesLength(); j++) {
                    ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.addElement(new Vector());
                }

                if (parent.cvType == Experiments.PK) {
                    //add 10 fold cross validation files for each layer
                    for (int k = 0; k < Layer.numLayers; k++) {
                        try {
                            dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                            ficheros = dir.list();

                            for (int l = 1; l <= parent.numberKFoldCross; l++) {

                                String pareja = "";

                                found = false;
                                for (int j = 0; j < ficheros.length && !found; j++) {
                                    if (ficheros[j].indexOf(parent.numberKFoldCross + "-" + l + "tra.dat") != -1) {
                                        pareja = ficheros[j] + ",";
                                        found = true;
                                    }
                                }

                                if (!found) {
                                    missing = true;
                                }

                                found = false;
                                for (int j = 0; j < ficheros.length && !found; j++) {
                                    if (ficheros[j].indexOf(parent.numberKFoldCross + "-" + l + "tst.dat") != -1) {
                                        pareja += ficheros[j];
                                        found = true;
                                    }
                                }

                                if (!found || missing) {
                                    missing = true;
                                    ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).addMissingPartition(pareja, k);
                                } else {
                                    ((Vector) (listas.elementAt(k))).add(pareja);
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                } else if (parent.cvType == Experiments.P5X2) {
                    // add 5x2 cross validation files for each layer
                    for (int k = 0; k < Layer.numLayers; k++) {
                        try {
                            dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                            ficheros = dir.list();

                            for (int l = 1; l <= 5; l++) {
                                String pareja = "";
                                String pareja2 = "";

                                found = false;
                                for (int j = 0; j < ficheros.length && !found; j++) {
                                    if (ficheros[j].indexOf("5x2-" + l + "tra.dat") != -1) {
                                        pareja = ficheros[j];
                                        found = true;
                                    }
                                }

                                if (!found) {
                                    missing = true;
                                }

                                found = false;
                                for (int j = 0; j < ficheros.length && !found; j++) {
                                    if (ficheros[j].indexOf("5x2-" + l + "tst.dat") != -1) {
                                        pareja2 = ficheros[j] + ",";
                                        pareja2 = pareja;
                                        pareja += "," + ficheros[j];
                                        found = true;
                                    }
                                }

                                if (!found || missing) {
                                    missing = true;
                                    ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).addMissingPartition(pareja, k);
                                } else {
                                    ((Vector) (listas.elementAt(k))).add(pareja);
                                    ((Vector) (listas.elementAt(k))).add(pareja2);
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                } else {
                    boolean stop;
                    for (int k = 0; k < Layer.numLayers; k++) {
                        try {
                            dir = new File("." + dsc.getPath(k) + dsc.getName(k));
                            ficheros = dir.list();
                            stop=false;
                            for (int j = 0; j < ficheros.length && !stop; j++) {
                                 if (ficheros[j].compareTo(dsc.getName(k) + "-10-1tra.dat") == 0) {
                                       ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + "-10-1tra.dat" + "," + dsc.getName(k) + "-10-1tst.dat");
                                       stop=true;
                                 }
                                 else{
                                     if(ficheros[j].compareTo(dsc.getName(k) + "-5-1tra.dat") == 0){
                                        ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + "-5-1tra.dat" + "," + dsc.getName(k) + "-5-1tst.dat");
                                        stop=true;
                                     }
                                     else{
                                        if(ficheros[j].compareTo(dsc.getName(k) + ".dat") == 0){
                                             ((Vector) (listas.elementAt(k))).add(dsc.getName(k) + ".dat" + "," + dsc.getName(k) + ".dat");
                                             stop=true;
                                        }
                                     }
                                 }
                            }
                        }catch (Exception e) {
                        }
                    }

                }

                if (missing == true) {
                    parent.status.setText("There are missing partitions. They will be regenerated when the experiment is generated.");
                } else {
                    parent.status.setText("All the partitions of the data sets selected have been found.");
                }
                for (int l = 0; l < Layer.numLayers; l++) {
                    ((DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i)).tableVector.setElementAt((Vector) (((Vector) listas.elementAt(l)).clone()), l);
                }

                //restore previous edited Layers
                DataSet aux;
                Vector main;
                String name;
                String name2;
                Boolean find;

                aux=(DataSet) parent.graphDiagramINNER.mainGraph.getNodeAt(i);
                main=(Vector)aux.tableVector.clone();

                for (int l = 0; l < Layer.numLayers; l++) {
                    name=dsc.getName(l);
                    find=false;
                    for(int index=0;index<save.size()&&!find;index++){
						name2="XXXXX";
						if(((Vector)(save.get(index))).size()>0){
                        name2=(String)((Vector)(save.get(index))).get(0);
						}
                        if(name2.startsWith(name)){
                            main.setElementAt(save.get(index), l);
                            find=true;
                        }
                    }
                }

                aux.tableVector=(Vector)main.clone();

            } // end-typedataset
            else {
                dscal = new ExternalObjectDescription(parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc);
                parent.graphDiagramINNER.mainGraph.getNodeAt(i).dsc = new ExternalObjectDescription(dscal.getName(), dscal.getPath(), dscal.getSubtype(), dscal.getJarName());
                for (int k = 0, conta = 0; k < checks.size(); k++) {
                    if (activos[k] == true && actTemp[k] == false) {//one dataset has been removed
                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.removeElementAt(conta);
                        action = false;
                        posAction = k;
                    } else if (activos[k] == false && actTemp[k] == true) {//one dataset has been added
                        //System.out.println (k+"-"+conta+"*****************************************"+((Parameters)(parent.graphDiagramINNER.grafo.getNodeAt(i).par.elementAt(0))).getValues());
                        Parameters temporal = new Parameters((Parameters) (parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.elementAt(0)));
                        //temporal.setValues(temporal.getDefaultValues());

                        parent.graphDiagramINNER.mainGraph.getNodeAt(i).par.insertElementAt(new Parameters(temporal), conta);
                        action = true;
                        posAction = k;
                    //System.out.println ("#"+k+"-"+conta+"*****************************************"+((Parameters)parent.graphDiagramINNER.grafo.getNodeAt(i).par.elementAt(0)).getValues());
                    }
                    if (activos[k] == true) {
                        conta++;
                    }
                }
            }
        }
        activos[posAction] = action;

        parent.reload_algorithms();

    }

    void actDatasetIO() {
        int iDataSetIndex;

        iDataSetIndex = 0;
        while (((Node) parent.experimentGraph.getNodes().elementAt(iDataSetIndex)).type != Node.type_Dataset) {
            iDataSetIndex++;
        }
        if (iDataSetIndex < parent.experimentGraph.getNodes().size()) {
            DataSet dataSet = (DataSet) parent.experimentGraph.getNodes().elementAt(iDataSetIndex);
            dataSet.updateState();
            dataSet.actInputOutput(parent.dsc, parent.graphDiagramINNER);
        }
    }

    public void import_actionPerformed(ActionEvent e) {

        this.saveSelected();

        //new DataCF dialog
        DataCFFrame frame = new DataCFFrame();
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

        frame.setParent(this.parent);
        frame.getSelectorToolbar().setVisible(false);
        frame.getSelectorTabbedPane().removeAll();
        if (parent.cvType == Experiments.P5X2)
            frame.addImportTab(false,false);
        else if (parent.cvType == Experiments.PK)
            frame.addImportTab(false,true);
        this.parent.setVisible(false);
        frame.setVisible(true);

    }

    /**
     * Sort the data sets list loaded from disc, so they
     * appear in alphabetic order
     */
    public void sortDatasets() {
        //sort the data set list
        Collections.sort(datasetXML);
        Collections.sort(datasetList);
    }

    /**
     * Tracks the button pressed, and implements the changes which derive
     * from the action
     * @param e actionevent associated
     */
    void checks_actionPerformed(ActionEvent e) {

        int i;
        int id = 0;
        Object s = e.getSource();

        for (i = 0; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
            } else {
                activos[i] = false;
            }
        }

        //search the checkbutton clicked
        for (i = 0; i < checks.size(); i++) {
            if (s == checks.elementAt(i)) {
                id = i;
            }
        }
        //    activos[id] = !activos[id];

        if (((JButton) (checks.elementAt(id))).getText() == "Del") {
            ((JButton) (checks.elementAt(id))).setText("Add");
            ((JButton) (edits.elementAt(id))).setVisible(false);
            this.remove(((JButton) (edits.elementAt(id))));
        } else {
            ((JButton) (checks.elementAt(id))).setText("Del");
            ((JButton) (edits.elementAt(id))).setVisible(true);
            this.add((JButton) (edits.elementAt(id)));
        }

        //at least there must be a dataset selected
        if (Layer.numLayers == 1 && ((JButton) (checks.elementAt(id))).getText() == "Add") {
            ((JButton) (checks.elementAt(id))).setText("Del");
            ((JButton) (edits.elementAt(id))).setVisible(true);
            this.add((JButton) (edits.elementAt(id)));
        }

        loadDatasetInfo();

        this.repaint();
        //System.out.println("  > Final checks_actionPerformed");

        actDatasetIO();

    }

    public void selectAll_actionPerformed(ActionEvent e) {

        int i;

        //System.out.println("Tamanio "+ checks.size());

        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            activos[i] = true;
        }


        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            if ((activos[i]) && ((JButton) (checks.elementAt(i))).getText() == "Add") {
                ((JButton) checks.elementAt(i)).setText("Del");
                ((JButton) edits.elementAt(i)).setVisible(true);
                this.add((JButton) (edits.elementAt(i)));
            }
        }

        loadDatasetInfo();

        this.repaint();
        //System.out.println("  > Final selectAll_actionPerformed");

        actDatasetIO();
    }

    public void invertSelection_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = false;
            } else {
                activos[i] = true;
            }
        }

        for (i = datasetsNoUser; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
                oneActive = true;
            } else {
                activos[i] = false;
            }
        }

        for (i = 0; i < checks.size(); i++) {
            if (activos[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
                if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                    ((JButton) checks.elementAt(i)).setText("Del");
                    ((JButton) edits.elementAt(i)).setVisible(true);
                    this.add((JButton) (edits.elementAt(i)));
                } else {
                    ((JButton) (checks.elementAt(i))).setText("Add");
                    ((JButton) (edits.elementAt(i))).setVisible(false);
                    this.remove(((JButton) (edits.elementAt(i))));
                }
            }

            loadDatasetInfo();

            this.repaint();

            actDatasetIO();

        }
//        else{
//            System.out.println("  > Unable to perform invertSelection_actionPerformed");
//        }
    }

    public void selectAllUser_actionPerformed(ActionEvent e) {

        for (int i = datasetsNoUser; i < checks.size(); i++) {
            activos[i] = true;
        }


        for (int i = datasetsNoUser; i < checks.size(); i++) {
            if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                ((JButton) checks.elementAt(i)).setText("Del");
                ((JButton) edits.elementAt(i)).setVisible(true);
                this.add((JButton) (edits.elementAt(i)));
            }
        }

        loadDatasetInfo();

        this.repaint();
        //System.out.println("  > Final selectAll_actionPerformed");

        actDatasetIO();

    }

    public void invertSelectionUser_actionPerformed(ActionEvent e) {

        int i;
        boolean oneActive = false;

        for (i = datasetsNoUser; i < checks.size(); i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = false;
            } else {
                activos[i] = true;
            }
        }

        for (i = 0; i < checks.size() && i < datasetsNoUser; i++) {
            if (((JButton) checks.elementAt(i)).getText() == "Del") {
                activos[i] = true;
                oneActive = true;
            } else {
                activos[i] = false;
            }
        }

        for (i = 0; i < checks.size(); i++) {
            if (activos[i]) {
                oneActive = true;
            }
        }
        //Ensure at least one dataset will remain selected
        if (oneActive) {

            for (i = datasetsNoUser; i < checks.size(); i++) {
                if (((JButton) (checks.elementAt(i))).getText() == "Add") {
                    ((JButton) checks.elementAt(i)).setText("Del");
                    ((JButton) edits.elementAt(i)).setVisible(true);
                    this.add((JButton) (edits.elementAt(i)));
                } else {
                    ((JButton) (checks.elementAt(i))).setText("Add");
                    ((JButton) (edits.elementAt(i))).setVisible(false);
                    this.remove(((JButton) (edits.elementAt(i))));
                }
            }

            loadDatasetInfo();

            this.repaint();

            actDatasetIO();

        }
//        else{
//            System.out.println("  > Unable to perform invertSelectionUser_actionPerformed");
//        }
    }
}

class DinamicDataset_importar_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_importar_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.import_actionPerformed(e);
    }
}

class DinamicDataset_Checks_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_Checks_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.checks_actionPerformed(e);
    }
}

class DinamicDataset_Edits_actionAdapter implements java.awt.event.ActionListener {

    DinamicDataset adaptee;

    DinamicDataset_Edits_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.edits_actionPerformed(e);
    }
}



class DinamicDataset_remove_actionAdapter implements ActionListener {

    private SelectData adaptee;

    DinamicDataset_remove_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.remove_actionPerformed(e);
    }
}

class DinamicDataset_selectAll_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAll_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAll_actionPerformed(e);
    }
}

class DinamicDataset_invertSelection_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelection_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelection_actionPerformed(e);
    }
}

class DinamicDataset_selectAllUser_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_selectAllUser_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllUser_actionPerformed(e);
    }
}

class DinamicDataset_invertSelectionUser_actionAdapter implements ActionListener {

    private DinamicDataset adaptee;

    DinamicDataset_invertSelectionUser_actionAdapter(DinamicDataset adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionUser_actionPerformed(e);
    }
}
