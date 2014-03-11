/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Semi_Supervised_Learning.utilities;

/**
 *
 * @author diegoj
 */
public class NumericFunction<IndexType>
{
    IndexType one;
    IndexType two;
    double defaultValue = 0.0;
    
    NumericFunction(IndexType a, IndexType b)
    {
        one = a;
        two = b;
    }
    
     double make()
     {
        return defaultValue;
     }
}
