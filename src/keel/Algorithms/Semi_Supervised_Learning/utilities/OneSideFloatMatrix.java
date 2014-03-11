/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Semi_Supervised_Learning.utilities;

import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import org.core.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class OneSideFloatMatrix<IndexType>
{
    HashMap<IndexType,HashMap<IndexType, Double>> matrix;
    
    
    public OneSideFloatMatrix()
    {
        matrix = new HashMap<IndexType,HashMap<IndexType, Double>>();
    }
    
    public OneSideFloatMatrix(ArrayList<IndexType>origin, ArrayList<IndexType>destiny)
    {
        for(IndexType o : origin)
            for(IndexType d : destiny)
                if(o != d)//testing references
                {
                    matrix.put(o, new HashMap<IndexType, Double>());
                    NumericFunction f = new NumericFunction<IndexType>(o,d);
                    matrix.get(o).put(d, f.make());
                }
    }
    
    public void add(IndexType a)
    {
        matrix.put(a, new HashMap<IndexType, Double>());
    }

}
