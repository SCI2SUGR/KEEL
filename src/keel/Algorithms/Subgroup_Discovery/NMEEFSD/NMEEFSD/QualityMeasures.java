/**
 * <p>
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package NMEEFSD;

import org.core.*;

public class QualityMeasures {
    /**
     * <p>
     * Defines the quality measures of the individual
     * </p>
     */

    private double[] v_objetivos;
    private int num_objetivos;
    private double cnf;


    /**
     * <p>
     * Creates a new instance of QualityMeasures
     * </p>
     * @param nobj              Number of objectives
     */
    public QualityMeasures(int nobj) {
        num_objetivos = nobj;
        v_objetivos = new double[num_objetivos];
        for(int i=0; i<num_objetivos; i++){
            v_objetivos[i]=0.0;
        }
    }


    /**
     * <p>
     * Returns the num_objetivos of the individual
     * </p>
     * @return              Number of objectives
     */
    public int getNumObjectives (){
        return num_objetivos;
    }

    /**
     * <p>
     * Sets the num_objetivos of the individual
     * </p>
     * @param a             Number of objectives
     */
    public void setNumObjectives (int a){
        num_objetivos = a;
    }

    /**
     * <p>
     * Gets the value of the objective pos
     * </p>
     * @param pos               Position of the objective
     * @return                  Value of the objective
     */
    public double getObjectiveValue (int pos){
        return v_objetivos[pos];
    }

    /**
     * <p>
     * Sets the value of the objective pos
     * </p>
     * @param pos               Position of the objective
     * @param value             Value of the objective
     */
    public void setObjectiveValue (int pos, double value){
        v_objetivos[pos] = value;
    }

    /**
     * <p>
     * Gets the value of the confidence
     * </p>
     * @return                  Value of the confidence
     */
    public double getCnf (){
        return cnf;
    }

    /**
     * <p>
     * Sets the value of the confidence
     * </p>
     * @param acnf              Value of the confidence
     */
    public void setCnf (double acnf){
        cnf = acnf;
    }


    /**
     * <p>
     * Copy in this object the values of qmeasures
     * </p>
     * @param qmeasures           Quality measures
     * @param nobj              Number of objectives
     */
    public void Copy (QualityMeasures qmeasures, int nobj) {
        for (int i=0; i<nobj; i++){
            this.v_objetivos[i] = qmeasures.v_objetivos[i];
        }
        this.setCnf(qmeasures.getCnf());
    }


    /**
     * <p>
     * Prints the measures
     * </p>
     * @param nFile             Fichero to write the quality measures
     * @param AG                Genetic algorithm
     */
    public void Print(String nFile, Genetic AG) {
        String contents;
        contents = "Measures: ";
        
        for(int i=0; i<AG.getNumObjectives(); i++){
            contents += AG.getNObjectives(i)+": ";
            contents += getObjectiveValue(i);
            contents += ", ";
        }

        contents += "confidence: "+getCnf();

        contents+= "\n";
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }



}
