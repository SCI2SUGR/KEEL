package keel.GraphInterKeel.experiments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import keel.GraphInterKeel.datacf.*;

/**
 * <p>Title: Keel</p>
 * <p>Description: DataSets selection</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Universidad de Granada</p>
 * @author Victor Manuel Gonzalez Quevedo
 * @version 2.0
 */
public class SelectData extends JPanel implements Scrollable {

    private int maxUnitIncrement = 10;
    private int pos = 10;
    Vector checks;
    Vector datasetList = new Vector();
    Vector datasetXML = new Vector();
    Vector actualList;
    JButton importB = new JButton();
    
    JButton remove = new JButton();
    int cadParent;
    String cad;
    String cadParent_aux;
    JButton selectAll = new JButton();
    JButton invertSelection = new JButton();
    int numberOfUserDataset = 0;
    JButton selectAllUser = new JButton();
    JButton invertSelectionUser = new JButton();
    Experiments parent;
    Hashtable<String, Boolean> dataActive = new Hashtable<String, Boolean>();
    int oddWidth, evenWidth, maxWidth;

    public SelectData() {
        super();

    }

    public SelectData(Experiments frame) {
        try {
            parent = frame;
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        importB.setBackground(new Color(225, 225, 225));
        importB.setText("Import");
        importB.addActionListener(new SelectData_importar_actionAdapter(this));

        
        remove.setBackground(new Color(225, 225, 225));
        remove.setText("Remove");
        remove.addActionListener(new SelectData_remove_actionAdapter(this));
        selectAll.setBackground(new Color(225, 225, 225));
        selectAll.setText("Select All");
        selectAll.addActionListener(new SelectData_selectAll_actionAdapter(this));
        invertSelection.setBackground(new Color(225, 225, 225));
        invertSelection.setText("Invert");
        invertSelection.addActionListener(new SelectData_invertSelection_actionAdapter(this));
        selectAllUser.setBackground(new Color(225, 225, 225));
        selectAllUser.setText("Select All");
        selectAllUser.addActionListener(new SelectData_selectAllUser_actionAdapter(this));
        invertSelectionUser.setBackground(new Color(225, 225, 225));
        invertSelectionUser.setText("Invert");
        invertSelectionUser.addActionListener(new SelectData_invertSelectionUser_actionAdapter(this));
    

    }

    /**
     * Insert a new External Object Description (of a data set) in the list
     * @param ds the new data sets�
     * @param path the path to the data set(s) file(s)
     */
    public void insert(DatasetXML ds, String path) {
        // Inserts a dataset info
        datasetList.add(new ExternalObjectDescription(ds.nameAbr, path, 0));
        datasetXML.add(ds);
    }

    /**
     * Method for saving the selected data sets so we can restore them later
     */
    public void saveSelected() {
        dataActive = new Hashtable<String, Boolean>();

        for (int i = 0; i < checks.size(); i++) {
            dataActive.put(((DatasetXML) datasetXML.elementAt(i)).nameComplete, new Boolean(((JCheckBox) checks.elementAt(i)).isSelected()));
        }

    }

    /**
     * Removes all the data sets from the list
     */
    public void removeAllData() {
        datasetList.removeAllElements();
        datasetXML.removeAllElements();
    }

    /**
     * Test if any of the data sets in the list are selected by their
     * correspondent check button
     * @return True if at least one is selected, false otherwise
     */
    public boolean isAnySelected() {
        // Check that one or more datasets are selected
        for (int i = 0; i < checks.size(); i++) {
            if (((JCheckBox) checks.elementAt(i)).isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clear all the data estructures of this object, and allocates new
     * memory for them
     */
    public void clear() {
        checks = new Vector();
        datasetList = new Vector();
        actualList = new Vector();
        this.removeAll();
    }

    /**
     * Reload the data set list, given the experiment type
     * @param type The current experiment type, which determines the data sets to be loaded
     */
    public void reload(int type) {
        int componentWidth = parent.datasetsChecksPanel.getWidth() -
                parent.mainSplitPane1.getDividerSize() - 30;
        boolean leftColumn;
        int leftPos, rightPos, align;
        pos = 10;
        int width;
        this.removeAll();
        checks = new Vector();
        this.numberOfUserDataset = 0;

        //        String cad = "classification";
        switch (type) {
            case Experiments.CLASSIFICATION:
                 {
                    cad = "classification";
                    
                    this.cadParent = 0;
                
                }
                break;
            case Experiments.REGRESSION:
                 {
                    cad = "regression";
                    
                    this.cadParent = 1;
                
                }
                break;
            case Experiments.UNSUPERVISED: {
                cad = "unsupervised";
                
                this.cadParent = 2;
                
                break;
            }
        }

        actualList = new Vector();

        JLabel titulo1 = new JLabel("KEEL Datasets");
        titulo1.setFont(new Font("Arial", Font.BOLD, 14));
        titulo1.setBounds(new Rectangle(10, 0, 200, 16));
        this.add(titulo1);

        //compute the maximum length of the differents data sets names
        computeDatasetsLabelWidth();
        maxWidth *= 1.5;
        //we begin putting the check buttons in the left side always
        leftColumn = true;
        //here we set the alignment from the left side of the panel for our
        //buttons
        leftPos = 15;
        //rightPos = leftPos + check Width + oddWidth + 10 + little offset
        rightPos = 15 + maxWidth + 10;


        for (int i = 0; i < datasetList.size(); i++) {
            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURI().toURL();
                File f = new File(recursoInterno.getFile());

                //File f = new File( ( (DescObjetoExterno) datasetList.elementAt(i)).getPath(0));

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == false) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        pos += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;

                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, pos, 25, 16));
                    chk.setOpaque(false);
                    this.add(chk);
                    checks.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXML.elementAt(i)).nameComplete);

                    txt.setBounds(new Rectangle(align + 30, pos, componentWidth - 45, 16));
                    this.add(txt);
                    actualList.add(datasetList.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        pos += 30;

        
        selectAll.setBounds(new Rectangle(15, pos, 110, 20));
        //        selectAll.setFont(new Font("Arial", Font.PLAIN, 10));
        this.add(selectAll);
        invertSelection.setBounds(new Rectangle(130, pos, 110, 20));
        //        invertSelection.setFont(new Font("Arial", Font.PLAIN, 10));
        this.add(invertSelection);
        pos += 30;
        


        JLabel titulo2 = new JLabel("User Datasets");
        titulo2.setFont(new Font("Arial", Font.BOLD, 14));
        titulo2.setBounds(new Rectangle(10, pos, 200, 16));
        this.add(titulo2);
        //user data sets now follows
        leftColumn = true;
        for (int i = 0; i < datasetList.size(); i++) {

            File data = new File(((ExternalObjectDescription) datasetList.elementAt(i)).getPath(0));
            try {
                java.net.URL recursoInterno = data.toURL();
                File f = new File(recursoInterno.getFile());

                //File f = new File( ( (DescObjetoExterno) datasetList.elementAt(i)).getPath(0));

                if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(cad) == 0 && ((DatasetXML) datasetXML.elementAt(i)).user == true) {
                    //set the align for this data set
                    if (leftColumn) {
                        align = leftPos;
                        width = maxWidth;
                        pos += 20;
                    } else {
                        align = rightPos;
                        width = maxWidth;
                    }
                    //                    leftColumn = !leftColumn;
                    this.numberOfUserDataset++;
                    JCheckBox chk = new JCheckBox();
                    chk.setBounds(new Rectangle(align, pos, 25, 16));
                    chk.setOpaque(false);
                    this.add(chk);
                    checks.add(chk);
                    JLabel txt = new JLabel(((DatasetXML) datasetXML.elementAt(i)).nameComplete);
                    txt.setBounds(new Rectangle(align + 30, pos, componentWidth - 45, 16));
                    this.add(txt);
                    actualList.add(datasetList.elementAt(i));
                }
            } catch (java.net.MalformedURLException ex) {
            }
        }

        pos += 30;
        
        if (numberOfUserDataset != 0) {
            remove.setBounds(new Rectangle(15, pos, 110, 20));
            this.add(remove);
            importB.setBounds(new Rectangle(130, pos, 110, 20));
            this.add(importB);
            pos += 25;
            selectAllUser.setBounds(new Rectangle(15, pos, 110, 20));
            this.add(selectAllUser);
            invertSelectionUser.setBounds(new Rectangle(130, pos, 110, 20));
            this.add(invertSelectionUser);
            pos += 30;
        } else {
            importB.setBounds(new Rectangle(15, pos, 90, 20));
            this.add(importB);
            pos += 30;

        }

        

        parent.checksDatasetsScrollPane.getViewport().setBackground(this.getBackground());
        //this.setPreferredSize(new Dimension(rightPos+(int)1.5*maxWidth+30, pos + 10));
        this.setPreferredSize(new Dimension(componentWidth, pos + 35));

        this.repaint();
    }

    /**
     * Once the Buttons has been loaded again, we must set their state
     * as it was previously set by the user <- we take the state saved from saveSelected()
     */
    public void reloadPreviousActiveDataSets() {
        Boolean isActive;
        int i;
        for (int id = 0; id < checks.size(); id++) {
            isActive = dataActive.get(((DatasetXML) datasetXML.elementAt(id)).nameComplete);
            if (isActive != null) {
                ((JCheckBox) checks.elementAt(id)).setSelected(isActive.booleanValue());
            }
        }
    }

    /**
     * scroll control
     */
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
     * Import user data sets button control
     * @param e The action event related to this code
     */
    public void importar_actionPerformed(ActionEvent e) {

        this.saveSelected();

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

        
        this.reload(this.cadParent);
    
    }

    
    public void remove_actionPerformed(ActionEvent e) {
        boolean anySelected = false;

        for (int i = 0; i < this.actualList.size(); i++) {
            if (((JCheckBox) checks.elementAt(i)).isSelected()) {
                for (int j = 0; j < datasetList.size(); j++) {
                    if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                        //Borrar el dataset de usuario
                        this.deleteFromXML(j);
                        actualList.remove(i);
                        checks.remove(i);
                        String nameData=((DatasetXML)datasetXML.elementAt(j)).nameAbr;
                        FileUtils.rmdir("./data/"+nameData);
                        datasetList.remove(j);
                        datasetXML.remove(j);
                        this.reload(this.cadParent);
                        this.reloadPreviousActiveDataSets();
                        anySelected = true;
                        i = -1;
                        break;
                    }
                }
            }
        }
        if (anySelected == false) {
            JOptionPane.showMessageDialog(null, "Check one or more user datasets", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void selectAll_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == false) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    public void invertSelection_actionPerformed(ActionEvent e) {

        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == false) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(!((JCheckBox) checks.elementAt(i)).isSelected());
                }
            }
        }
    }

    public void selectAllUser_actionPerformed(ActionEvent e) {
        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(true);
                }
            }
        }
    }

    public void deselectAll() {
        for (int i = 0; i < checks.size(); i++) {
            ((JCheckBox) checks.elementAt(i)).setSelected(false);
        }
    }

    public void invertSelectionUser_actionPerformed(ActionEvent e) {
        for (int i = 0; i < this.actualList.size(); i++) {
            for (int j = 0; j < datasetList.size(); j++) {
                if (datasetList.elementAt(j).equals(actualList.elementAt(i)) && ((DatasetXML) datasetXML.elementAt(j)).user == true) {
                    ((JCheckBox) checks.elementAt(i)).setSelected(!((JCheckBox) checks.elementAt(i)).isSelected());
                }
            }
        }
    }

    /**
     * Delete a data set from the XML file
     * @param index the index of the data set to be deleted
     */
    protected void deleteFromXML(int index) {
        /*Load the previuos datasets.xml*/
        Document data = new Document();
        try {
            SAXBuilder builder = new SAXBuilder();
            data = builder.build("./data/Datasets.xml");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Dataset specification XML file not found");
            return;
        }

        /* Delete user datasets */
        String datasetName = ((DatasetXML) datasetXML.elementAt(index)).nameAbr;
        java.util.List allChildren = (java.util.List) data.getRootElement().getChildren("dataset");
        // Remove the children
        java.util.List toRemove = new ArrayList();
        for (Object element : allChildren) {
            Element el = (Element) element;
            if (el.getChildText("nameAbr").equals(datasetName)) {
                toRemove.add(element);
            }
        }
        allChildren.removeAll(toRemove);

        try {
            File f = new File("./data/Datasets.xml");
            FileOutputStream file = new FileOutputStream(f);
            XMLOutputter fmt = new XMLOutputter();
            fmt.setFormat(Format.getPrettyFormat());
            fmt.output(data, file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

    /**
     * This function sorts the inserted data set lists,
     * so they will appear sorted in the GUI
     */
    public void sortDatasets() {
        //sort the data set list
        Collections.sort(datasetXML);
        Collections.sort(datasetList);
    }

    /**
     * We try to compute the JLabels width in pixels, from the length of
     * the label text in characters...  (not finished)
     */
    protected void computeDatasetsLabelWidth() {
        int width;

        maxWidth = evenWidth = oddWidth = 0;
        for (int i = 0; i < datasetXML.size(); i++) {
            if (((DatasetXML) datasetXML.elementAt(i)).problemType.toLowerCase().compareTo(this.cad) == 0) {
                JLabel txt = new JLabel(((DatasetXML) datasetXML.elementAt(i)).nameComplete);
                //compute the width of this label from the assigned text
                Dimension d = txt.getPreferredSize();
                width = d.width;
                if (maxWidth < width) {
                    maxWidth = width;
                }
                if (i % 2 == 0 && oddWidth < width) {
                    oddWidth = width;
                }
                if (i % 2 != 0 && evenWidth < width) {
                    evenWidth = width;
                }
            //                if(i%2 == 0)
            //                    System.out.print(txt.getText()+" "+width+" ");
            //                else
            //                    System.out.println(txt.getText()+" "+width);
            }
        }

    }
}

class SelectData_importar_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_importar_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.importar_actionPerformed(e);
    }
}


class SelectData_remove_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_remove_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.remove_actionPerformed(e);
    }
}

class SelectData_selectAll_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAll_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAll_actionPerformed(e);
    }
}

class SelectData_invertSelection_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelection_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelection_actionPerformed(e);
    }
}

class SelectData_selectAllUser_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_selectAllUser_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.selectAllUser_actionPerformed(e);
    }
}

class SelectData_invertSelectionUser_actionAdapter implements ActionListener {

    private SelectData adaptee;

    SelectData_invertSelectionUser_actionAdapter(SelectData adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.invertSelectionUser_actionPerformed(e);
    }
}
