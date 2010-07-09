/**
 * <p>
 * @author Written by Antonio Alejandro Tortosa (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */



package keel.Algorithms.Genetic_Rule_Learning.PART;



class Report {
/**
 * <p>
 * Auxiliar class used for compact information in the serching method of
 * the class PART
 * </p>
 */
  
  //it stores a combination (without repetitions) of positive numbers
  private int[] card;
  //the length of card
  private int length; 
  //the value of a metric that evaluates the combination in card
  private double value; 

  /**
   * Constructor.
   * @param card the combination (without repetitions)
   * @param length the card's length
   * @param value the value of the combination according some metric
   */
  public Report(int[] card,int length,double value){
    this.length=length;
    this.card=new int[card.length];
    for (int i=0;i<length;i++)
      this.card[i]=card[i];
    this.value=value;
  }

  /**
   * Return a positition of the combination.
   * @param i the position
   * @return the i-th positition of the combination.
   */
  public int get(int i){return card[i];}

  /**
   * Returns the combination.
   * @return the combination.
   */
  public int[] getCard(){return card;}

  /**
   * Returns the length of the combination.
   * @return the length of the combination.
   */
  public int length(){return length;}

  /**
   * Returns the value of the combination.
   * @return the value of the combination.
   */
  public double getValue(){return value;}

  /**
   * Sets all the parameters of the report.
   * @param card the combination (without repetitions)
   * @param length the card's length
   * @param value the value of the combination according some metric
   */
  public void set(int[] card,int length,double value){
    for (int i=0;i<length;i++)
      this.card[i]=card[i];
    this.length=length;
    this.value=value;
  }

  /**
   * Returns the string representation of this report.
   * @return the string representation of this report.
   */
  public String toString(){
    String output="{";

    for (int i=0;i<length;i++){
      output+=card[i]+",";
    }

    output+="} "+"value: "+value;

    return output;
  }

}