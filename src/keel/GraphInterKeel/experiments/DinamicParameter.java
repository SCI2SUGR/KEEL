/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.GraphInterKeel.experiments;

import java.util.Vector;

/**
 *
 * @author tua
 */
public class DinamicParameter {

    public Vector<Vector<String>> parameter_data= new Vector<Vector<String>>();
   //Fist: number of instances
    //2: number of classes
    //3: number of attributes
    //4: The classes {0,1} for example
    //5: Begin the parameters of the algorithm
    
    public DinamicParameter()
    {
    }
    public int size()
    {
        return parameter_data.size();
    }
     public void insert(Vector<String> contain)
     {
         parameter_data.addElement(contain);
     }
     public void remove(int position)
     {
         parameter_data.remove(position);
     }
     public Vector<String> get(int position)
     {
         return parameter_data.get(position);
     }
     public void set(int position,Vector<String> contain)
     {
         parameter_data.set(position, contain);
     }
}
