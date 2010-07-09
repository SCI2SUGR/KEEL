/**
 * <p>
 * @author Written by Antonio Alejandro Tortosa (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.PART;

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
        PART part=new PART(par);
        part.execute();
      }
    }
    catch (Exception e)
    {
      System.err.println(e.getMessage());
      System.exit(-1);
    }
  }
}