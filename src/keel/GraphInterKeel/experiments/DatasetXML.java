package keel.GraphInterKeel.experiments;

import java.util.*;

import org.jdom.Element;

public class DatasetXML implements Comparable {

    public String nameAbr;
    public String nameComplete;
    public String problemType;
    public Vector partitions;

    // Variables for checking the correctness of data set and method
    public boolean m_bContinuous;
    public boolean m_bInteger;
    public boolean m_bNominal;
    public boolean m_bMissing;
    public boolean m_bImprecise;
    public boolean m_bMultiClass;
    public boolean m_bMultiOutput;
    public double missing;
    public Vector properties;
    public int nAttributes;
    public int nClasses;
    public int nInstances;
    public Vector<String> classes;
    public String field;
    public boolean user;

    /**
     * Builder
     * @param dataset Node containing the datasets
     */
    public DatasetXML(Element dataset) {

        Element temporal;
        int i;
        String value;
        

        nameAbr = dataset.getChildText("nameAbr");
        //System.out.println (" \n > Reading dataset: "+nameAbr );
        nameComplete = dataset.getChildText("nameComplete");
        problemType = dataset.getChildText("problemType");
        partitions = new Vector();
        temporal = dataset.getChild("partitions");

        for (i = 0; i < temporal.getChildren().size(); i++) {
            partitions.addElement(new String(((Element) (temporal.getChildren().get(i))).getText()));
        }

        value = dataset.getChildText("continuous");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bContinuous = true;
        } else {
            m_bContinuous = false;
        }
        //System.out.println ("    > Continous: "+m_bContinuous );

        value = dataset.getChildText("integer");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bInteger = true;
        } else {
            m_bInteger = false;
        }
        //System.out.println ("    > Integer: "+m_bInteger );

        value = dataset.getChildText("nominal");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bNominal = true;
        } else {
            m_bNominal = false;
        }
        //System.out.println ("    > Nominal: "+m_bNominal );

        value = dataset.getChildText("imprecise");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bImprecise = true;
        } else {
            m_bImprecise = false;
        }
        //System.out.println ("    > Imprecise: "+m_bImprecise );

        value = dataset.getChildText("missing");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMissing = true;
        } else {
            m_bMissing = false;
        }

        value = dataset.getChildText("multiclass");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMultiClass = true;
        } else {
            m_bMultiClass = false;
        }
        //System.out.println ("    > MutliClass: "+m_bMultiClass );

        value = dataset.getChildText("multioutput");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMultiOutput = true;
        } else {
            m_bMultiOutput = false;
        }
        //System.out.println ("    > MutliOutput: "+m_bMultiOutput );

        if (dataset.getChild("percMissinValues") != null) {
            missing = Double.parseDouble(dataset.getChildText("percMissingValues"));
        } else {
            missing = 0;
        }

        if (dataset.getChild("nAttributes") != null) {
            nAttributes = Integer.parseInt(dataset.getChildText("nAttributes"));
        }

        if (dataset.getChild("nInstances") != null) {
            nInstances = Integer.parseInt(dataset.getChildText("nInstances"));
        }

        if (problemType.compareTo("Classification")==0) {
            if (dataset.getChild("nClasses") != null) {
                nClasses = Integer.parseInt(dataset.getChildText("nClasses"));
            }
        }

         classes = new Vector<String>();
         int con=0;
        value = dataset.getChildText("classes"+con);
        while(value!=null)
        {
            classes.addElement(value);
            con++;
            value = dataset.getChildText("classes"+con);
        }
        
        field = dataset.getChildText("field");
        if (dataset.getChild("userDataset") == null) {
            user = false;
        } else {
            user = true;
        }
        
       
    }

    /**
     * Implements the lexicographic order
     * @param o Object to compare
     * @return The lexicographic order 
     */
    public int compareTo(Object o) {
        DatasetXML data = (DatasetXML) o;
        
        return this.nameAbr.compareTo(data.nameAbr);
    }
}
