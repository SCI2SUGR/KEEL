/**
 * <p>
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.C45RulesSA;

class Principal {
  public static void main(String[] args) {


   try
    {
      if (args.length != 1)
        throw new Exception("\nError: you have to specify the parameters file\n\tusage: java -jar C45.java parameterfile.txt");
      else {
        String param=args[0];
        parseParameters par = new parseParameters();
        par.parseConfigurationFile(param);
        C45 c45tree = new C45(par);
        Tree t = c45tree.getTree();
        C45RulesSA c45rules=new C45RulesSA(t,par);
        c45rules.execute();
      }
    }
    catch (Exception e)
    {
      System.err.println(e.getMessage());
      System.exit(-1);
    }
  }
}