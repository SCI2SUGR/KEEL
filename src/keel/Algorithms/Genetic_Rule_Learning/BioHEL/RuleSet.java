package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: RuleSet</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import java.util.Vector;

public class RuleSet{
    
    private Vector<Rule> rule;
    
    
    public RuleSet(){
        rule=new Vector<Rule>(25,10);
    }
    
    public Rule get(int i){
        return rule.get(i);
    }
    
    public void set(int i, Rule r){
        rule.set(i, r);
    }
    
    public void add(Rule r){
        rule.add(r);
    }
    
    public int size(){
        return rule.size();
    }
}
