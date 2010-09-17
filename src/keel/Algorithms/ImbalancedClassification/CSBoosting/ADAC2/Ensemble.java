/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
* <p>
* @author Written by Mikel Galar Idoate (Universidad Pública de Navarra) 30/5/2010
* @version 0.1
* @since JDK 1.5
*</p>
*/


package keel.Algorithms.ImbalancedClassification.CSBoosting.ADAC2;


import org.core.Randomize;

/**
 * Class to implement the Adaboost algorithm ensembles
   @author Mikel Galar Idaote (UPNA)
   @version 1.1 (17-05-10)
 */
class Ensemble {
    /* Parameters used by the ensemble */
   String trainMethod;
   String costType;
   int nClassifier;
   boolean resampling;
   /* Iteration counter */
   int t;

   /* Costs for the majority and minority classes */
   double CostMaj, CostMin;      // For Cost-Sensitive

   /* References to the original and the actual (modified by adaboost data-distribution change datasets */
   myDataset originalDS, actualDS;
   /* weights of the instances for adaboost and weight of each classifier in the voting*/
   double[] weights, alfa;
   /* number of instances */
   int nData;

   /* number of majority and minority examples and their integer value*/
   int nMaj, nMin;
   int majC, minC;

   /* if the data set has to be prepared (needs resampling) */
   boolean prepareDSNeeded;

   /* reference to the base classifiers */
   AdaC2 classifier;

   /** Constructor of the adaboost ensemble
    *
    * @param originalDS original data-set to perform the boosted learning
    * @param nClassifier number of maximum classifiers in to boost
    * @param classifier the reference to the base classifiers
    */
   public Ensemble(myDataset originalDS, int nClassifier, AdaC2 classifier) {
      this.originalDS = originalDS;

      /* Initialization of helper variables */
      nData = originalDS.getnData();
      majC = originalDS.claseNumerica(originalDS.claseMasFrecuente());
      nMaj = originalDS.numberInstances(majC);
      minC = majC == 0 ? 1 : 0;
      nMin = originalDS.numberInstances(minC);

      /* Uniform weights */
      weights = new double[nData];
      for (int i = 0; i < nData; i++)
         weights[i] = 1.0 / (float)nData;

      /* data-set init */
      actualDS = originalDS;
      this.nClassifier = nClassifier;
      /* first iteration*/
      t = 0;
      alfa = new double[nClassifier];
      this.classifier = classifier;
      prepareDSNeeded = false;

      /* configure if the resampling is asked */
      trainMethod = classifier.parameters.getParameter(5);
      if (trainMethod.equalsIgnoreCase("RESAMPLING"))
      {
          resampling = true;
          prepareDSNeeded = true;
          Randomize.setSeed(Long.parseLong(classifier.parameters.getParameter(0)));
      }

      /* Configure the costs, adaptive or manual */
      costType = classifier.parameters.getParameter(6);
      if (costType.equalsIgnoreCase("ADAPTIVE"))
      {
         CostMaj = (double)nMin /  (double)nMaj;
         CostMin = 1.0;
      }
      else
      {
          CostMaj = Float.parseFloat(classifier.parameters.getParameter(7));
          CostMaj = Float.parseFloat(classifier.parameters.getParameter(8));
      }
      
         

    
   }

   /** Method to perform the voting strategy
    *
    * @param example instance to be predicted
    * @return the predicted class
    */
   String computeClassScores(double[] example) {
      double sum = 0;
      double confidence = 1;
      for (int t = 0; t < nClassifier; t++)
      {
         if (alfa[t] != 0)
         {
            if (classifier.obtainClass(t, example) == 0)
               sum += confidence * alfa[t];
            else
               sum -= confidence * alfa[t];
         }
      }
      if (sum >= 0)
         return originalDS.getOutputValue(0);
      else
         return originalDS.getOutputValue(1);
   }

   /** The next iteration of boosting is prepared
    *
    * @return true if adaboost has finished
    */
   boolean nextIteration()
   {
      boolean fin = false;
      
     fin = modifyWeightsAdaC2();
      
      t++;

      return fin;
   }


   /**
    *
    * @return the weights of the adaboost iteration
    */
   double[] getWeights()
   {
         return weights;
   }

   /** set the cost of missclassifications
    *
    * @param Cmaj cost for the majority
    * @param Cmin cost for the minority
    */
   void setCosts(double Cmaj, double Cmin)
   {
      this.CostMaj = Cmaj;
      this.CostMin = Cmin;
   }

   /**
    *
    * @return the new data-set for training, resampled if needed
    */
   myDataset getDS()
   {
      if (prepareDSNeeded)
         prepareDataset();
      return actualDS;
   }

   /**
    * Resamples the data set if needed with the given data distribution
    */
   public void prepareDataset()
   {
      if (resampling)
      {
         myDataset auxDS = new myDataset(actualDS, weights);
         actualDS = auxDS;
      }
   }


   /** AdaC2 boosting algorithm
    *
    * @return true if the boosting has finished
    */
   private boolean modifyWeightsAdaC2() {
      double[] corrects = new double[nData];
      double[] C = new double[nData];
      double sumFail = 0, sumCorrect = 0;

      // calcular alfa_t
      double Z = 0;
      for (int i = 0; i < nData; i++)
      {
         C[i] = majC == actualDS.getOutputAsInteger(i) ? CostMaj : CostMin;

         if (classifier.obtainClass(t, originalDS.getExample(i)) == originalDS.getOutputAsInteger(i))
         {
            corrects[i] = 1;
            sumCorrect += C[i] * weights[i];
         }
         else
         {
            corrects[i] = -1;
            sumFail += C[i] * weights[i];
         }
      }

      if (sumFail < 0.001 || sumFail >= sumCorrect)
      {
         if (t > 0 && sumFail >= sumCorrect)
         {
            t = t - 1;
            nClassifier = t;
         }
         else
            alfa[t] = 1.0;
         return true;
      }

      alfa[t]  = 0.5 * Math.log(sumCorrect / sumFail);

      for (int i = 0; i < nData; i++)
      {
            weights[i] *= Math.exp(-1.0 * alfa[t] * corrects[i]) * C[i];
            Z += weights[i];
      }
      for (int i = 0; i < nData; i++)
         weights[i] /= Z;
      

      return false;
   }

}

