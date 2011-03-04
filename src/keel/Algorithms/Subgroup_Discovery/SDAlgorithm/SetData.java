/**
 * <p>
 * @author Writed by Alberto Fernández (University of Granada) 15/01/2006
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

import java.util.*;

public class SetData{

    /**
     * <p>
     * Data structure for the examples
     * </p>
     */

    private LinkedList datos;

    /**
     * <p>
     * Constructor
     * </p>
     */
    public SetData() {
        datos = new LinkedList();
    }

    /**
     * <p>
     * Delete a data
     * </p>
     * @param int               Position to delete
     */
    public void deleteDato(int i) {
        datos.remove(i);
    }

    /**
     * <p>
     * Add a data
     * </p>
     * @param Instance              Instance to add
     */
    public void addDato(Instance m) {
        Instance mim = m.copy();
        datos.add(mim);
    }

    /**
     * <p>
     * Return an instance
     * </p>
     * @param int               Position of the instance
     * @return                  The instance indicated in the position
     */
    public Instance getDato(int i) {
        return (Instance) datos.get(i);
    }

    /**
     * <p>
     * Sets an instance
     * </p>
     * @param int               Position to insert the instance
     * @param Instance          Instance to insert
     */
    public void setDato(int i, Instance m) {
        datos.set(i, m);
    }

    /**
     * <p>
     * Return the number of instances
     * </p>
     * @return                  The size of the dataset
     */
    public int size() {
        return (datos.size());
    }

    /**
     * <p>
     * Show an instance
     * </p>
     */
    public void print() {
        for (int i = 0; i < datos.size(); i++) {
            Instance m = (Instance) datos.get(i);
            m.print();
        }

    }

    /**
     * <p>
     * Copy the dataset in another
     * </p>
     * @return                  A new dataset
     */
    public SetData copiaConjDatos() {
        SetData c = new SetData();

        for (int i = 0; i < datos.size(); i++) {
            Instance aux;
            Instance m = (Instance) datos.get(i);
            aux = m.copy();
            c.addDato(aux);
        }

        return c;
    }

    /**
     * <p>
     * Return the examples for a class
     * </p>
     * @param value         Value of the class
     * @return              The number of examples for the class
     */
    public int getExamplesClass(int value){

        int conta=0;

        for(int i=0; i<datos.size(); i++){
            Instance m = (Instance) datos.get(i);
            if(m.getClas()==value){
                conta++;
            }

        }
        return conta;
    }

}
