/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.KeelToPMML;

/**
 *
 * @author alberto
 */
public class StringPair {
    
    private String first;
    private String second;

    public StringPair(String f, String s)
    {
        first = f;
        second = s;
    }
    
    
    public String getKey()
    {
        return first;
    }
    
    public String getValue()
    {
        return second;
    }
    
    public void setKey(String s)
    {
        first = s;
    }
    
    public void setValue(String s)
    {
        second = s;
    }
    
}
