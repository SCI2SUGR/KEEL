package keel.Algorithms.Genetic_Rule_Learning.SIA;

import java.util.*;


/**
 * <p>Title: Reglas</p>
 * <p>Description: It defines a Rule-set</p>
 * <p>Company: KEEL</p>
 * @author Alberto Fernández (University of Granada) 27/02/2005
 * @since JDK1.5
 * @version 1.2
 */
public class ruleSet {

/*  Rules:

   R1: 'IF' cond1 ^ ...  ^ condn 'THEN' 'Class =' Ci, Strength
   R2: ......
   RN: ....

      */

  private LinkedList ruleSet;

  /**
   * Default Builder
   */
  public ruleSet() {
    super();
    ruleSet = new LinkedList();
  }

  /**
   * It add a new rule to the set
   * @param regl the rule
   */
  public void addRule(Rule regl) {
    //ruleSet.add(regl.copiaRule());
    ruleSet.add(regl);
  }

  /**
   * It removes a rule in the i-th position
   * @param i the position to remove
   */
  public void deleteRule(int i) {
    ruleSet.remove(i);
  }

  /**
   * It returns the i-th rule
   * @param i rule position
   * @return the i-th rule
   */
  public Rule getRule(int i) {
    return  ((Rule) ruleSet.get(i)).copyRule();
  }

  /**
   * It returns the size of the rule set
   * @return the size of the rule set
   */
  public int size() {
    return (ruleSet.size());
  }

  /**
   * It returns the whole rule set
   * @return the whole rule set
   */
  public LinkedList getRuleSet() {
    return ruleSet;
  }

  /**
   * It copies the rule set into a new one
   * @return A new rule set which is an exact copy of the current one
   */
  public ruleSet copyRuleSet() {
    int i;
    ruleSet c = new ruleSet();

    for (i = 0; i < ruleSet.size(); i++) {
      Rule reg = (Rule) ruleSet.get(i);
      c.addRule(reg.copyRule());
    }
    return c;
  }

  /**
   * It prints all rules by screen
   */
  public void print() {
    int i;

    for (i = 0; i < ruleSet.size(); i++) {
      Rule r = (Rule) ruleSet.get(i);
      System.out.print("Rule_" + (i+1) + ": ");
      r.print();
    }
  }

  /**
   * It prints all rules into a string
   * @return a string containing all rules
   */
  public String printString() {
    int i;
    String cad = "";

    for (i = 0; i < ruleSet.size(); i++) {
      Rule r = (Rule) ruleSet.get(i);
      cad += (i+1) + ": ";
      cad += r.printString();
    }

    return cad;
  }

  /**
   * It returns the last rule (it is supossed to be the one with less strength)
   * @return the last rule
   */
  public Rule getLastRule() {
    return (Rule) ruleSet.getLast();
  }

  /**
   * Insertion operator
   * @param r Rule to insert
   * If r is in the rule set, it is not inserted again
   * If the size of the rule set is lower than 50, we insert the new rule
   * If the size of the rule set is higher than 50, we replace the last rule (less strength)
   */
  public void insertion(Rule r){
    boolean seguir = true;
    for (int i = 0; i < this.size() && seguir; i++){
      if (this.getRule(i).isEqual(r)){
        seguir = false;
      }
    }
    if (seguir){
      if (this.size() < 50){
        this.addRule(r);
      }
      else{
        Collections.sort(this.getRuleSet());
        this.deleteRule(49);
        this.addRule(r);
      }
    }
  }

}
