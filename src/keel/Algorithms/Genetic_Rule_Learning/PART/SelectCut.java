/**
 * <p>
 * @author Written by Antonio Alejandro Tortosa (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */



package keel.Algorithms.Genetic_Rule_Learning.PART;

import java.util.Enumeration;


public class SelectCut{
/**
 * <p>
 * Class to select a cut point in a dataset.
 * </p>
 */
	
  /** Minimum number of objects in interval. */
  private int minItemsets;

  /** The training data. */
  private MyDataset dataset;

  /** Creates a new cut model.
   *
   * @param nObj		Minimum number of objects.
   * @param allData	The dataset.
   */
  public SelectCut( int nObj, MyDataset allData )
  {
    minItemsets = nObj;
    dataset = allData;
  }

  /** Function to select the cut point.
   *
   * @param data	The dataset used to compute the cut point.
   *
   * @return		The cut point computed.
   */
  public final Cut selectModel( MyDataset data )
  {
    double minResult, averageInfoGain = 0, sumOfWeights;
    Cut [] current;
    Cut best = null, noCut = null;
    int models = 0, i;
    boolean multiVal = true;
    Classification checkClassification;
    MyAttribute attribute;

    try
    {
      // Check if all Dataset belong to one class or if not
      // enough Dataset to Split.
      checkClassification = new Classification( data );
      noCut = new Cut( checkClassification );

      if ( checkClassification.getTotal() < 2*minItemsets ||
           checkClassification.getTotal() == checkClassification.perClass( checkClassification.maxClass() ) )
        return noCut;

      // Check if all attributes are nominal and have a
      // lot of values.
      if ( dataset != null )
      {
        Enumeration enum2 = data.enumerateAttributes();

        while ( enum2.hasMoreElements() )
        {
          attribute = (MyAttribute) enum2.nextElement();

          if ( ( attribute.isContinuous() ) || ( (double)attribute.numValues() < ( 0.3 * (double)dataset.numItemsets() ) ) )
          {
            multiVal = false;
            break;
          }
        }
      }

      current = new Cut[data.numAttributes()];
      sumOfWeights = data.sumOfWeights();

      // For each attribute.
      for ( i = 0; i < data.numAttributes(); i++ )
      {
        // Apart from class attribute.
        if ( i != (data).getClassIndex() )
        {
          // Get models for current attribute.
          current[i] = new Cut( i, minItemsets, sumOfWeights );
          current[i].classify( data );

          // Check if useful Split for current attribute
          // exists and check for enumerated attributes with
          // a lot of values.
          if ( current[i].checkModel() )
            if ( dataset != null )
            {
              if ( ( data.getAttribute(i).isContinuous() ) ||
                   ( multiVal || (double)data.getAttribute(i).numValues() < ( 0.3*(double)dataset.numItemsets() ) ) )
              {
                averageInfoGain = averageInfoGain + current[i].getInfoGain();
                models++;
              }
            }
            else
            {
              averageInfoGain = averageInfoGain+current[i].getInfoGain();
              models++;
            }
        }
        else
          current[i] = null;
      }

      // Check if any useful Split was found.
      if ( models == 0 )
        return noCut;

      averageInfoGain = averageInfoGain / (double)models;

      // Find "best" attribute to Split on.
      minResult = 0;

      for ( i=0;i<data.numAttributes();i++ )
      {
        if ( ( i != (data).getClassIndex() ) && ( current[i].checkModel() ) )
          // Use 1E-3 here to get a closer approximation to the original
          // implementation.
          if ( ( current[i].getInfoGain() >= ( averageInfoGain-1E-3 ) ) &&
               current[i].getGainRatio() >minResult )
          {
            best = current[i];
            minResult = current[i].getGainRatio();
          }
      }

      // Check if useful Split was found.
      if ( minResult == 0 )
        return noCut;

      // Add all Dataset with unknown values for the corresponding
      // attribute to the classification for the model, so that
      // the complete classification is stored with the model.
      best.classification().addWithUnknownValue( data,best.attributeIndex() );

      // Set the Split point analogue to C45 if attribute numeric.
      if ( dataset != null )
        best.setCutPoint( dataset );

      return best;
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    return null;
  }
}