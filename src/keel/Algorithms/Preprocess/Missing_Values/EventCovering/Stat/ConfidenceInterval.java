package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;
public class ConfidenceInterval {

  public double min;
  public double value;
  public double max;
  public double confidence;

  public String toString() {
    return value+" ["+min+","+max+"]("+(confidence*100)+"%)";
  }

}

