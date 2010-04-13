package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

class Principal {
  public static void main(String[] args) {
    try{
      parseParameters param = new parseParameters();
      param.parseConfigurationFile(args[0]);
      M5Rules m5rules = new M5Rules(param);
      m5rules.execute();
    }catch(Exception ex){
      ex.printStackTrace();
      System.out.println("Fuera\n"+ex.getMessage());
    }
  }
}